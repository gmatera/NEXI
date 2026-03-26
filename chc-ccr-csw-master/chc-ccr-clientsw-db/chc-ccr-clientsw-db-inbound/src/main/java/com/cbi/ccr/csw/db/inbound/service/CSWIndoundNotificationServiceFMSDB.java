package com.cbi.ccr.csw.db.inbound.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.db.common.CommonConfigurationServiceDB;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFMSDB;
import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fms.FMSSendDBRepository;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.service.common.CSWCommonService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.Msg;
import com.cbi.frw.common.exception.ChcException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundNotificationServiceFMSDB extends CSWCommonService{

	@Autowired
	private FMSSendDBRepository fmsRepo;
	
	@Autowired
	private CommonConfigurationServiceDB commonConfigService;
	
	public void processNotification(NotificationDTO notificationDTO) throws ChcException {
		int statusCodeBA;
		int statusCodeSync;
		FMSSendStatus fmsSendStatus;
		ClientTaskStatus taskStatus;

		Optional<FMSSend> fmsSendOptional = fmsRepo.findById(notificationDTO.getMessageKey());
		if (fmsSendOptional.isPresent()) {
			FMSSend fmsSend = fmsSendOptional.get();
			
			CswLog.getLogData().setUdr(fmsSend.getUdr());
			CswLog.getLogData().setVfn(fmsSend.getVfn());

			ConfigurationFMSDB configFMS = commonConfigService.loadFMSConfiguration(fmsSend.getLocalBaId(),fmsSend.getRemoteBaId());
			if (notificationDTO.getSuccess().equals(Boolean.TRUE)) {
				statusCodeBA = FMSSendStatus.NOTIFY.getStCodeBA();
				statusCodeSync = FMSSendStatus.NOTIFY.getStCodeSync();
				fmsSendStatus = FMSSendStatus.NOTIFY;
				taskStatus = ClientTaskStatus.SUCCESS;
				if(Boolean.TRUE.equals(configFMS.getSndCompletionAlgo()))
					fmsSend.setComplete(1);
				
				CswLog.info(log, "Positive Notification recived");
			} else {
				statusCodeBA = FMSSendStatus.SENDING_FAILURE.getStCodeBA();
				statusCodeSync = FMSSendStatus.SENDING_FAILURE.getStCodeSync();
				fmsSendStatus = FMSSendStatus.SENDING_FAILURE;
				taskStatus = ClientTaskStatus.FAILED;
				fmsSend.setComplete(1);
				fmsSend.setStatusInfo(Msg.getMessage(I18nService.ERROR_ON_HUB.name()));
				
				CswLog.error(log, "Negative Notification recived");
			}

			updateNotificationStatusa(fmsSend, statusCodeBA, statusCodeSync, fmsSendStatus, taskStatus);
			
		} else {
			CswLog.error(log, String.format("Notification RECORD not found id: %s", notificationDTO.getMessageKey()));
		}
	}

	private void updateNotificationStatusa(FMSSend fmsSend, int statusCodeBA, int statusCodeSync,
			FMSSendStatus fmsSendStatus, ClientTaskStatus taskStatus) {
		fmsSend.setStatusCodeBA(statusCodeBA);
		fmsSend.setStatusCodeSync(statusCodeSync);
		fmsSend.setStatus(fmsSendStatus);
		fmsSend.setCswStatus(taskStatus);
		LocalDateTime now = LocalDateTime.now();
		fmsSend.setFenDlvMssTmp(now);
		fmsSend.setNotifyTime(now);
		fmsSend.setCrtDlvMssTmp(now);
		fmsSend.setSendRequestTimestamp(now);
		fmsSend.setSendCompletedTimestamp(now);

		transactionTemplate.executeWithoutResult(t -> fmsRepo.save(fmsSend));
	}

	
}
