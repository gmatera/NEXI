package com.cbi.ccr.csw.db.inbound.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cbi.ccr.csw.db.common.CommonConfigurationServiceDB;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.csw.config.AddOnConfigurationFTSRepository;
import com.cbi.ccr.csw.domain.csw.config.AddOnFTSConfiguration;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFTSDB;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.fts.FTSSendDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSSendStatus;
import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.service.common.CSWCommonService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.Msg;
import com.cbi.frw.common.exception.ChcException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundNotificationServiceFTSDB extends CSWCommonService{

	@Autowired
	private FTSSendDBRepository ftsRepo;

	@Autowired
	private CommonConfigurationServiceDB commonConfigService;
	
	@Autowired
	private AddOnConfigurationFTSRepository addOnConfigurationRepository;

	@Transactional
	public void processNotification(NotificationDTO notificationDTO) throws ChcException {
		int stsCode;
		FTSSendStatus ftsSendStatus;
		ClientTaskStatus taskStatus;

		Optional<FTSSend> ftsSendOptional = ftsRepo.findById(notificationDTO.getMessageKey());
	
		if (ftsSendOptional.isPresent()) {
			
			FTSSend ftsSend = ftsSendOptional.get();

			CswLog.getLogData().setVfn(ftsSend.getVfn());
			
			AddOnFTSConfiguration confAddon= null;
			ConfigurationFTSDB configFTS = null;
			
			if (ftsSend.getFtsInterface() == RouteInterface.FS) {
				confAddon = addOnConfigurationRepository.findByLocalBaIdAndRemoteBaId(ftsSend.getLocalBaId(), ftsSend.getRemoteBaId());
				configFTS = new ConfigurationFTSDB();
				configFTS.setSndCompletionAlgo(true);
				
			} else {
				 configFTS = commonConfigService.loadFTSConfiguration(ftsSend.getLocalBaId(), ftsSend.getRemoteBaId());
			}
			
			if (notificationDTO.getSuccess().booleanValue()) {
				stsCode = FTSSendStatus.EXPORT_COMPLETE.getStsCode();
				ftsSendStatus = FTSSendStatus.EXPORT_COMPLETE;
				taskStatus = ClientTaskStatus.SUCCESS;
				LocalDateTime now = LocalDateTime.now();
				ftsSend.setSendRequestTimestamp(now);
				ftsSend.setSendConfirmedTimestamp(now);
				ftsSend.setSendCompletedTimestamp(now);
				if(configFTS.getSndCompletionAlgo().booleanValue())
						ftsSend.setComplete(1);
				
				CswLog.info(log, "Positive Notification recived");
			} else {
				stsCode = FTSSendStatus.EXPORT_ERROR.getStsCode();
				ftsSendStatus = FTSSendStatus.EXPORT_ERROR;
				taskStatus = ClientTaskStatus.FAILED;
				ftsSend.setSendErrorTimestamp(LocalDateTime.now());
				ftsSend.setComplete(1);
				ftsSend.setStatusInfo(Msg.getMessage(I18nService.ERROR_ON_HUB.name()));
				CswLog.error(log, "Negative Notification recived");
			}

			ftsSend.setStatus(ftsSendStatus);
			ftsSend.setStsCode(stsCode);
			ftsSend.setCswStatus(taskStatus);
			
			if (ftsSend.getFtsInterface()==RouteInterface.FS && confAddon != null)
				renameFile(ftsSend, confAddon, notificationDTO.getSuccess());
			
			ftsRepo.save(ftsSend);
			
		} else {
			CswLog.error(log, String.format("Notification RECORD not found id: %s", notificationDTO.getMessageKey()));
		}
	}

	private void renameFile(FTSSend ftsSend, AddOnFTSConfiguration confAddon, boolean success) throws ChcException {
		String prefix = null;
		String sendingPrefix = confAddon.getSendingPrefix();
		if (success) {
			prefix = confAddon.getSentPrefix();
		} else {
			prefix = confAddon.getErrorDeliverPrefix();
		}
		try {
			File file = new File(ftsSend.getFileName());
//			File file2 = new File(ftsSend.getFileName().replace(sendingPrefix, ""));
			Files.move(
					Paths.get(confAddon.getSndPath() + File.separator + confAddon.getSendingPrefix() + file.getName()),
					Paths.get(confAddon.getSndPath() + File.separator + prefix + file.getName()),
					StandardCopyOption.REPLACE_EXISTING);
//			
//			Files.move(
//					Paths.get(File.separator + FilenameUtils.getPath(ftsSend.getFileName()) + FilenameUtils.getName(ftsSend.getFileName())),
//					Paths.get(File.separator + FilenameUtils.getPath(ftsSend.getFileName()) + prefix + FilenameUtils.getName(ftsSend.getFileName().replace(sendingPrefix, ""))), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new ChcException(I18nService.ERR_PROCESSING_MESSAGE, e.getLocalizedMessage());
		}
	}

	
}
