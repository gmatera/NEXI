package com.cbi.ccr.csw.mq.inbound.service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.jms.JMSException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cbi.ccr.csw.domain.CSWCommonRepositoryMQ;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSMSSCommon;
import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.mq.common.mq.FileQueueUtil;
import com.cbi.ccr.csw.mq.common.service.CommonConfigurationServiceMQ;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveConfigurationLoader;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.i.CswEntityMq;
import com.cbi.ccr.csw.mq.domain.i.CswEntityOutMqWithFile;
import com.cbi.ccr.csw.service.common.CSWCommonService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.frw.common.Msg;
import com.cbi.frw.common.exception.ChcException;

import encoding.EncodingUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CommonInboundNotificationServiceMq<
	E extends CswEntityMq, 
	R extends CSWCommonRepositoryMQ<E, Long>,
	C extends  ConfigurationFMSFTSMSSCommon>  extends CSWCommonService {

	@Autowired
	protected CommonConfigurationServiceMQ configurationService;
	
	@Autowired
	protected MqPrimitiveConfigurationLoader mqPrimitiveConfigurationLoader;
	
	@Autowired
	protected JmsTemplate jmsTemplate;
	
	@Autowired
	protected FileQueueUtil fileQueueUtil;
	
	@Autowired
	protected R repo;
	
	protected abstract C getConfiguration(E entity) throws ChcException;
	protected abstract void sendPrimitiveMessage(E entity, NotificationDTO notification, String message) throws ChcException;
	
	@Transactional
	public void processNotification(NotificationDTO notificationDTO) throws ChcException {
		
		Optional<E> entitySendOptional = repo.findById(notificationDTO.getMessageKey());
		if (entitySendOptional.isPresent()) {
			E entity = entitySendOptional.get();
			
//			CswLog.getLogData().setUdr(entity.getUdr());
//			CswLog.getLogData().setVfn(fmsSend.getVfn());
			
			try {
				C config = getConfiguration(entity);
				if (notificationDTO.getSuccess().booleanValue()) {

					updateNotificationStatus(entity, SendStatusMQ.REMOTELY_CONFIRMED, null);

					sendPrimitiveMessage(entity, notificationDTO, null);
//					deleteFileFromMQ(entity);
					
					if (Boolean.TRUE.equals(config.getSndCompletionAlgo())) {
						entity.setComplete(1);
						updateNotificationStatus(entity, SendStatusMQ.CLEANABLE, ClientTaskStatus.SUCCESS);
					}else {
						entity.setComplete(0);
						updateNotificationStatus(entity, SendStatusMQ.SENT, ClientTaskStatus.SUCCESS);
					}
					CswLog.info(log, "Positive Notification recived");

				} else {
					entity.setComplete(1);
					entity.setStatusInfo(Msg.getMessage(I18nService.ERROR_ON_HUB.name()));
					entity.setRejectReason(70);
					updateNotificationStatus(entity, SendStatusMQ.IN_ERROR, ClientTaskStatus.FAILED);
					sendPrimitiveMessage(entity, notificationDTO, "negative notification received");
					
					CswLog.error(log, "Negative Notification recived");

				}

			} catch (ChcException e) {
				CswLog.getLogData().setCode(e.getCode());
				CswLog.error(log, e.getLocalizedMessage());
				throw e;
			}
			
		} else {
			CswLog.error(log, String.format("Notification RECORD not found id: %s", notificationDTO.getMessageKey()));
		}
	}
	
	private void deleteFileFromMQ(E entity) {
		if(entity instanceof CswEntityOutMqWithFile) {
			// delete file fom queue
			CswEntityOutMqWithFile eFile = (CswEntityOutMqWithFile) entity;
			try {
				fileQueueUtil.deleteFileGrouped(eFile.getQuequeFileName(), EncodingUtils.decodeHexString(eFile.getGroupId()));
			} catch (JMSException e) {
				CswLog.error(log, String.format("Unable to delete file from queue having gropId: %s", eFile.getGroupId()));
			}
		} 
		
	}

	private E updateNotificationStatus(E entity, SendStatusMQ fmsSendStatus, ClientTaskStatus taskStatus) {
		
		entity.setStatus(fmsSendStatus);
		if (taskStatus != null)
			entity.setCswStatus(taskStatus);
		LocalDateTime now = LocalDateTime.now();
//		fmsSend.setFenDlvMssTmp(now);
		entity.setNotifyTime(now);
//		fmsSend.setCrtDlvMssTmp(now);
//		
		return repo.save(entity);
		
//		return transactionTemplate.execute(t -> {
//			
//			return repo.save(entity);
//		});
	}
	


}
