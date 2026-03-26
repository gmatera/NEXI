package com.cbi.ccr.csw.db.inbound.service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.csw.config.AddOnConfigurationFTSRepository;
import com.cbi.ccr.csw.domain.csw.config.AddOnFTSConfiguration;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.DSNCreationAlgo;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFTSDB;
import com.cbi.ccr.csw.domain.fts.FTSRecv;
import com.cbi.ccr.csw.domain.fts.FTSRecvDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSRecvStatus;
import com.cbi.ccr.csw.dto.fms.FTSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.FTSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundServiceFTSDB extends CSWCommonInboundServiceDB<FTSRecv, FTSRecvDBRepository, FTSMessageDTO>{

	public CSWIndoundServiceFTSDB(FTSRecvDBRepository dbRepository) {
		super(dbRepository, FTSRecv.class);
	}
	
	@Autowired
	private FTSRecvDBRepository ftsRecvRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	AddOnConfigurationFTSRepository addOnConfigurationFTSRepository;

	public void processFTSMessage(InputStream file, MessageWrapperDTO wrapper,  Long id) throws ChcException, ChcStubException {
		
		//it will accept fts and addon 
		if(wrapper.getType() != ServiceType.FTS && wrapper.getType() != ServiceType.AON)
			throw new ChcException(I18nService.INVALID_SERVICE_DATA);
		
		// TIMESTAMP
		LocalDateTime startTime2 = LocalDateTime.now();
		FTSRecv ftsRecv = new FTSRecv();
		ftsRecv.setLastDelTime(format(startTime2));
		// TIMESTAMP fine
		
		FTSInboundMessageDTO dto;
		try {
			dto = JSON.fromJson(encryptionUtil.decrypt(wrapper.getEncryptedBody(), getEncryptionServiceKey(wrapper.getWrapperKey())), FTSInboundMessageDTO.class);
		} catch (ChcStubException e) {
			throw new ChcStubException(ErrorMessage.builder()
					.errorCode(e.getError().getErrorCode())
					.httpStatus(503)
					.localizedMessage(e.getLocalizedMessage())
					.build());
		}
		
		ConfigurationFTSDB configFTS = getConfiguration(dto.getLocalBaId(), dto.getRemoteBaId(), wrapper.getType(), (wrapper.getType() == ServiceType.AON) ? RouteInterface.FS : RouteInterface.DB);
		
		try {
			
			ftsRecv.setId(id);
			ftsRecv.setLocalBaId(dto.getLocalBaId());
			ftsRecv.setRemoteBaId(dto.getRemoteBaId());
			ftsRecv.setApplicativeDataField(dto.getUdr());
			ftsRecv.setVfn(dto.getVfn());
			ftsRecv.setStsCode(FTSRecvStatus.READ_REQUEST.getStsCode());
			ftsRecv.setStatus(FTSRecvStatus.READ_REQUEST);
			
			// TIMESTAMP
			LocalDateTime now = LocalDateTime.now();
			ftsRecv.setCreateDate(format3(now));
			
			ftsRecv.setStartTime(format(dto.getTmsReceived()));
			ftsRecv.setFirstDelTime(format(dto.getTmsStartSending()));
			ftsRecv.setRcvRequestTimestamp(dto.getTmsStartSending());
			
			// Timestamp di quando il ClientSW inizia a salvare il file da consegnare alla BA ricevente,
			ftsRecv.setRecvDeliveredTimestamp(now);
			// TIMESTAMP fine
			
			ftsRecv.setApplCheck(0);
			ftsRecv.setComplete(ftsRecv.getApplCheck() == 1 ? 1 : 0);
			ftsRecv.setLocalAuthInfo(dto.getLocalAuthInfo());
			ftsRecv.setLocalAuthInfoAlg(dto.getLocalAuthInfoAlg());
	
			if(wrapper.getType()!= ServiceType.AON) {
				if(configFTS.getRcvDnsCreationAlgo() == null) {
					throw new ChcException(I18nService.ERR_DSN_MISSING_CONFIGURATION);
				} else if((configFTS.getRcvDnsCreationAlgo().equals(DSNCreationAlgo.DSN_3) ||
						configFTS.getRcvDnsCreationAlgo().equals(DSNCreationAlgo.DSN_4) ||
						configFTS.getRcvDnsCreationAlgo().equals(DSNCreationAlgo.DSN_7) ||
						configFTS.getRcvDnsCreationAlgo().equals(DSNCreationAlgo.DSN_8)) &&
						ftsRecv.getVfn().length() < 32) {
					//ftsRecv.setStatusInfo("VFN is invalid for rcvDsnCreationAlgo = " + configFTS.getRcvDnsCreationAlgo().getValue());  inserito nel chcException
					ftsRecv.setFileName(ftsRecv.getVfn());
					throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, "= " + configFTS.getRcvDnsCreationAlgo().getValue());
				} else {
				ftsRecv.setFileName(removeLastSlash(configFTS.getRcvPath()) + File.separator + createDSN(configFTS.getRcvDnsCreationAlgo(), configFTS.getRcvDsnPrefix(), dto.getVfn(), dto.getRemoteBaId(), dto.getLocalBaId()));
				}
			}else {
				ftsRecv.setFtsInterface(RouteInterface.FS);
				String newFile = removeLastSlash(configFTS.getRcvPath()) + File.separator + FilenameUtils.getName(dto.getFileName());
				if(Files.exists(Paths.get(newFile))) {
					throw new ChcException(I18nService.FILE_ALREADY_EXISTS);
				}
				ftsRecv.setFileName(newFile);
			}
			
			ftsRecv.setStsCode(FTSRecvStatus.READ_FILE_DELIVERED.getStsCode());
			// save READ_FILE_DELIVERED
			transactionTemplate.executeWithoutResult(t -> ftsRecvRepository.save(ftsRecv));
			
			// validate file
			long newFileSize = handleFile(ftsRecv, file, dto, configFTS, wrapper.getFileId());
			ftsRecv.setFileSize(newFileSize);
			
			ftsRecv.setStatus(FTSRecvStatus.READ_FILE_DELIVERED);
			ftsRecv.setCswStatus(ClientTaskStatus.SUCCESS);
			//ftsRecv.setComplete(Boolean.TRUE.equals(configFTS.getRcvCompletionAlgo()) ?  1 :  0);
			
			ftsRecv.setStatusInfo(createStatusInfo(configFTS.getRcvCodePage(), configFTS.getRcvLineSeparator(),
			configFTS.getSndRecordFormat(), configFTS.getSndMaxRecLength()));
			
			checkToComplete(configFTS, ftsRecv);
			// inserire LAU
//			if(configFTS.getLauEnabled().booleanValue())
//				lauService.checkLauInboundFTSDB(ftsRecv, configFTS.getLauKey());
			
			// TIMESTAMP
			// Timestamp di quando il ClientSW termina il salvataggio del file da consegnare alla BA ricevente.
			ftsRecv.setEasComplTime(format(LocalDateTime.now()));
			ftsRecv.setReceiveTimestamp(startTime2);
			
			transactionTemplate.executeWithoutResult(t -> ftsRecvRepository.save(ftsRecv));
			
			CswLog.getLogData().setLocalBaId(ftsRecv.getLocalBaId());
			CswLog.getLogData().setRemoteBaId(ftsRecv.getRemoteBaId());
			CswLog.getLogData().setFunction("processFTSMessage");
			CswLog.getLogData().setVfn(ftsRecv.getVfn());
			CswLog.info(log, String.format("CSW-INBOUND %s message received id: %s", wrapper.getType(), ftsRecv.getId()));

		} catch (ChcException e) {
			ftsRecv.setStatusInfo(e.getMessage());
			updateWhenInError(ftsRecv);
			throw mapException(e);
		} catch (Exception e) {
			ftsRecv.setStatusInfo("Errore Interno: "+e.getMessage());
			updateWhenInError(ftsRecv);
			throw mapException(e);
		} 
	}

	private void updateWhenInError(FTSRecv ftsRecv) {
		ftsRecv.setStsCode(FTSRecvStatus.READ_ERROR.getStsCode());
		// TIMESTAMP
		ftsRecv.setRecvErrorTimestamp(LocalDateTime.now());
		ftsRecv.setStatus(FTSRecvStatus.READ_ERROR);
		ftsRecv.setCswStatus(ClientTaskStatus.FAILED);
		ftsRecv.setComplete(1);
		ftsRecvRepository.save(ftsRecv);
	}

	private ConfigurationFTSDB getConfiguration(String localBa, String remoteBa, ServiceType serviceType,
			RouteInterface ftsInterface) throws ChcException {
		ConfigurationFTSDB conf;

		if (serviceType == ServiceType.FTS)
			conf = configurationService.loadFTSConfiguration(localBa, remoteBa);
		else {
			AddOnFTSConfiguration addonConf = addOnConfigurationFTSRepository.findByLocalBaIdAndRemoteBaId(localBa,
					remoteBa);
			if (addonConf == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, localBa, remoteBa);
			conf = new ConfigurationFTSDB();
			conf.setSndCodePage(CodePage.BINARY);
			conf.setRcvPath(removeLastSlash(addonConf.getRcvPath()) + File.separator);		
		}
		return conf;
	}
	
	private String removeLastSlash(String str) {
		if(str != null) {
			return (str.charAt(str.length() - 1) == '/' || str.charAt(str.length() - 1) == '\\') ? str.substring(0, str.length() - 1) : str;
		}
		return str;
	}
}
