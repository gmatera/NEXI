package com.cbi.ccr.csw.db.inbound.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cbi.ccr.csw.db.common.CommonConfigurationServiceDB;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMSSDB;
import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.domain.mss.MSSSendDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSSendStatus;
import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.service.common.CSWCommonService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.Msg;
import com.cbi.frw.common.exception.ChcException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundNotificationServiceMSSDB extends CSWCommonService{

	@Autowired
	private MSSSendDBRepository mssRepo;

	@Autowired
	private CommonConfigurationServiceDB commonConfigService;

	@Transactional
	public void processNotification(NotificationDTO notificationDTO) throws ChcException{
		int stsCode;
		MSSSendStatus mssSendStatus;
		ClientTaskStatus taskStatus;
		LocalDateTime scTimeStamp = null;

		Optional<MSSSend> mssSendOptional = mssRepo.findById(notificationDTO.getMessageKey());
		if (mssSendOptional.isPresent()) {
			MSSSend mssSend = mssSendOptional.get();
			
			CswLog.getLogData().setFunction("processNotification");
			CswLog.getLogData().setUdr(mssSend.getRemoteRef());
			
			ConfigurationMSSDB configMSS = commonConfigService.loadMSSConfiguration(mssSend.getLocalBaId(), mssSend.getRemoteBaId());

			if (notificationDTO.getSuccess().equals(Boolean.TRUE)) {
				stsCode = MSSSendStatus.MSG_SENT_CONFIRMED.getStsCode();
				mssSendStatus = MSSSendStatus.MSG_SENT_CONFIRMED;
				taskStatus = ClientTaskStatus.SUCCESS;
				scTimeStamp = LocalDateTime.now();
				CswLog.info(log, "Positive Notification recived");
			} else {
				stsCode = MSSSendStatus.SENDING_ERROR.getStsCode();
				mssSendStatus = MSSSendStatus.SENDING_ERROR;
				taskStatus = ClientTaskStatus.FAILED;
				mssSend.setStatusInfo(Msg.getMessage(I18nService.ERROR_ON_HUB.name()));
				mssSend.setComplete(1);
				CswLog.error(log, "Negative Notification recived");
			}
			LocalDateTime now = LocalDateTime.now();
			mssSend.setStatus(mssSendStatus);
			mssSend.setStsCode(stsCode);
			mssSend.setCswStatus(taskStatus);
			mssSend.setFerDelTime(format(now));
			mssSend.setFenDelTime(format(now));
			mssSend.setLastEasSubTime(format(now));
			mssSend.setSendScTimestamp(scTimeStamp);
			
			if(Boolean.TRUE.equals(configMSS.getRcvCompletionAlgo())) {
				mssSend.setComplete(1);
			}
			
			mssRepo.save(mssSend);
		
		} else {
			CswLog.error(log, String.format("Notification RECORD not found id: %s", notificationDTO.getMessageKey()));
		}

	}
}
