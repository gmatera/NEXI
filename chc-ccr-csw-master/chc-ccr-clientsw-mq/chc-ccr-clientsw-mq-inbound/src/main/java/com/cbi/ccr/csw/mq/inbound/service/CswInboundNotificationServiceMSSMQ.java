package com.cbi.ccr.csw.mq.inbound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1941SecSendMsgind;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveService;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMSSMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQRepository;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;

import chc.framework.util.parsing.input.FlowioOutputMapper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CswInboundNotificationServiceMSSMQ  extends CommonInboundNotificationServiceMq<MSSSendMQ, MSSSendMQRepository, ConfigurationMSSMQ>{

	@Autowired
	private MqPrimitiveSenderService primitiveSenderService;
	
	@Override
	protected ConfigurationMSSMQ getConfiguration(MSSSendMQ entity) throws ChcException {
		return configurationService.loadMSSMQConfiguration(entity.getLocalBaId(), entity.getRemoteBaId());
	}

	@Override
	protected void sendPrimitiveMessage(MSSSendMQ entity, NotificationDTO notification, String message) throws ChcException {
		
		MQ1941SecSendMsgind primitive = MqPrimitiveService.get1941PrimitiveMSS(entity, message);
		FlowioOutputMapper<MQ1941SecSendMsgind> flowioWriter = new FlowioOutputMapper<>(BinderFactory.binder1941());
		try {
			CswLog.getLogData().setMessage("Sending 1941 ..");
			CswLog.getLogData().setSystem(LogSystemEnum.CSW.name());
			CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
			CswLog.getLogData().setService(ServiceEnum.MSS.name());
			CswLog.getLogData().setLocalBaId(primitive.getBaLoc());
			CswLog.getLogData().setRemoteBaId(primitive.getBaRem());
			CswLog.getLogData().setUdr(primitive.getUdr());
			CswLog.getLogData().setId(entity.getId().toString());
			CswLog.getLogData().setFunction("sendPrimitiveMessage");
//			CswLog.debug(log);
			
			byte[] primitiveByte = flowioWriter.writeByte(primitive);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(primitive.getId());
			p.setServiceType(ServiceType.MSS);
			p.setEntityId(entity.getId());
			
			primitiveSenderService.storePrimitiveOnDB(primitiveByte, p);
			
//			String queue = mqPrimitiveConfigurationLoader.getQueueByPrimitive("1941");
//			jmsTemplate.convertAndSend(queue, flowioWriter.writeByte(primitive));
//			CswLog.getLogData().setMessage("Sent 1941");
//			CswLog.debug(log);
		} catch (NoSuchFieldException | JmsException e) {
			throw new ChcException(I18nCommon.ERR_NOT_FOUND, e.getMessage());
		}
	}

}
