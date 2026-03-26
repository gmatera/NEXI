package com.cbi.ccr.csw.db.inbound.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.DSNCreationAlgo;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFMSDB;
import com.cbi.ccr.csw.domain.fms.FMSRecv;
import com.cbi.ccr.csw.domain.fms.FMSRecvDBRepository;
import com.cbi.ccr.csw.domain.fms.FMSRecvStatus;
import com.cbi.ccr.csw.dto.fms.FMSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.logging.LogLevel;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundServiceFMSDB extends CSWCommonInboundServiceDB<FMSRecv, FMSRecvDBRepository, FMSMessageDTO>{

	public CSWIndoundServiceFMSDB(FMSRecvDBRepository dbRepository) {
		super(dbRepository, FMSRecv.class);
	}
	
	@Autowired
	private FMSRecvDBRepository fmsRecvRepository;
	@PersistenceContext
	private EntityManager entityManager;
	
	public void processFMSMessage(InputStream message, InputStream file, MessageWrapperDTO wrapper, Long id) throws ChcException, ChcStubException {
		if(wrapper.getType() != ServiceType.FMS)
			throw new ChcException(I18nService.INVALID_SERVICE_DATA);
//		log("INBOUND FMS - Entered Inbound FMS");;
		
		// TIMESTAMP
		LocalDateTime now = LocalDateTime.now();
		FMSRecv fmsRecv = new FMSRecv();
		fmsRecv.setFerSubLstFtsTmp(now);
		fmsRecv.setFerDlvLstFtsTmp(now);
		fmsRecv.setFenDlvMssTmp(now);
		fmsRecv.setCrtDlvMssTmp(now);
		// TIMESTAMP fine 
		
		FMSInboundMessageDTO dto;
		try {
			dto = JSON.fromJson(encryptionUtil.decrypt(wrapper.getEncryptedBody(),getEncryptionServiceKey(wrapper.getWrapperKey())), FMSInboundMessageDTO.class);
		} catch (ChcStubException e) {
			throw new ChcStubException(ErrorMessage.builder()
					.errorCode(e.getError().getErrorCode())
					.httpStatus(503)
					.localizedMessage(e.getLocalizedMessage())
					.build());
		}

		ConfigurationFMSDB configFMS = configurationService.loadFMSConfiguration(dto.getLocalBaId(), dto.getRemoteBaId());
		
		File decryptedMessage = null;
		try {
			fmsRecv.setId(id);
			fmsRecv.setLocalBaId(dto.getLocalBaId());
			fmsRecv.setRemoteBaId(dto.getRemoteBaId());
			fmsRecv.setVfn(dto.getVfn());
			fmsRecv.setUdr(dto.getUdr());
			fmsRecv.setMessageType(dto.getMessageType());
			
			// TIMESTAMP
			fmsRecv.setFtsDelivTime(dto.getTmsReceived());
			
			fmsRecv.setFerSubFstFtsTmp(dto.getTmsStartSending());
			fmsRecv.setFerDlvFstFtsTmp(dto.getTmsStartSending());
			fmsRecv.setFenSubMssTmp(dto.getTmsStartSending());
			fmsRecv.setCrtSubMssTmp(dto.getTmsStartSending());
			// TIMESTAMP fine
			
			fmsRecv.setStatusCodeBA(0);
			fmsRecv.setStatusCodeSync(0);
			fmsRecv.setStatus(FMSRecvStatus.RECEIVING);
			fmsRecv.setMessageLeng(dto.getMessageLeng());
			fmsRecv.setCatAppl(dto.getCatAppl());
			fmsRecv.setTur(dto.getTur());
			fmsRecv.setLocalAuthInfo(dto.getLocalAuthInfo());
			fmsRecv.setLocalAuthInfoAlg(dto.getLocalAuthInfoAlg());
			
			if(configFMS.getRcvDnsCreationAlgo() == null) {
				throw new ChcException(I18nService.ERR_DSN_MISSING_CONFIGURATION);
			} else if((configFMS.getRcvDnsCreationAlgo().equals(DSNCreationAlgo.DSN_3) ||
					configFMS.getRcvDnsCreationAlgo().equals(DSNCreationAlgo.DSN_4) ||
					configFMS.getRcvDnsCreationAlgo().equals(DSNCreationAlgo.DSN_7) ||
					configFMS.getRcvDnsCreationAlgo().equals(DSNCreationAlgo.DSN_8)) &&
					fmsRecv.getVfn().length() < 32) {
//				fmsRecv.setStatusInfo("VFN is invalid for rcvDsnCreationAlgo = " + configFMS.getRcvDnsCreationAlgo().getValue());  inserito nerlla chcException
				fmsRecv.setFileName(fmsRecv.getVfn());
				//updateWhenInError(fmsRecv);
				throw new ChcException(I18nService.ERR_DSN_INVALID_ALGORITHM, "= " + configFMS.getRcvDnsCreationAlgo().getValue());
			} else {
				fmsRecv.setDataSetName(createDSN(configFMS.getRcvDnsCreationAlgo(), configFMS.getRcvDsnPrefix(), dto.getVfn(), dto.getRemoteBaId(), dto.getLocalBaId()));
				fmsRecv.setFileName(removeLastSlash(configFMS.getRcvPath())+ File.separator + fmsRecv.getDataSetName());

			}
			
//			fmsRecv.setStatusInfo(createStatusInfo(CodePage.getEnumByValue(dto.getCodePage()), LineSeparator.getEnumByLabel(dto.getLineSeparator()),
//					RecordFormat.getEnumByValue(dto.getRecordFormat()), dto.getMaxRecordLength()));
			
			// inserire LAU
//			if(configFMS.getLauEnabled().booleanValue())
//				lauService.checkLauInboundFMDB(fmsRecv, configFMS.getLauKey());
			
			// TIMESTAMP
			now = LocalDateTime.now();
			fmsRecv.setModTime(now);
			fmsRecv.setReceiveTimestamp(now);
			fmsRecv.setFtsInsertTime(now);
			fmsRecv.setReadRequestTimestamp(now);
			// Timestamp di quando il ClientSW inizia a salvare il file-messaggio da consegnare alla BA ricevente,
			fmsRecv.setMsRecvTime(now);
			// TIMESTAMP fine 
			
			transactionTemplate.executeWithoutResult(t -> fmsRecvRepository.save(fmsRecv));
			CswLog.getLogData().setFunction("processFMSMessage");
			CswLog.getLogData().setLocalBaId(fmsRecv.getLocalBaId());
			CswLog.getLogData().setRemoteBaId(fmsRecv.getRemoteBaId());
			CswLog.getLogData().setUdr(fmsRecv.getUdr());
			CswLog.getLogData().setVfn(fmsRecv.getVfn());
			CswLog.debug(log, String.format("INBOUND FMS - RECEIVING FMS %s", fmsRecv));
			
			decryptedMessage = new File(localStoragePathDecrypted, UUID.randomUUID().toString());
			
			fmsRecv.setStatusCodeBA(1);
			fmsRecv.setStatusCodeSync(6);
			fmsRecv.setStatus(FMSRecvStatus.RECEIVED);
			fmsRecv.setCswStatus(ClientTaskStatus.SUCCESS);
			fmsRecv.setMsConfTime(now);
			// replaced by checkToComplete
			// fmsRecv.setComplete(Boolean.TRUE.equals(configFMS.getRcvCompletionAlgo()) ?  1 :  0);
			
			encryptionUtil.decryptFile(message, decryptedMessage, getEncryptionServiceKey(wrapper.getMessageId()));
			CswLog.debug(log, String.format("INBOUND FMS - Message decrypted %s ", fmsRecv));
			//fmsRecv.setReceiveTime(LocalDateTime.now());

			try(FileInputStream fisMessage = new FileInputStream(decryptedMessage)){
				if(fmsRecv.getMsgDigestAlg() != null)
					validationService.validateSha256(fisMessage, fmsRecv.getMsgDigest());
			}

			long newFileSize = handleFile(fmsRecv, file, dto, configFMS, wrapper.getFileId());
			// dimensione dopo conversione
			fmsRecv.setFileSize(newFileSize);
			fmsRecv.setStatusInfo(createStatusInfo(configFMS.getRcvCodePage(), configFMS.getRcvLineSeparator(),
					configFMS.getSndRecordFormat(), configFMS.getSndMaxRecLength()));
			
			CswLog.debug(log, String.format("INBOUND FMS - Message decrypted and validated %s", fmsRecv));
			
			saveBlob(decryptedMessage, fmsRecv);
			
			/* TIMESTAMP
			 * "Timestamp di quando il ClientSW termina il salvataggio del file-messaggio da consegnare alla BA ricevente.
 				formato UTC."
			 */
			now = LocalDateTime.now();
			fmsRecv.setReceiveTime(now);
			fmsRecv.setReadCompletedTimestamp(now);
			
			checkToComplete(configFMS, fmsRecv);
			if(fmsRecv.getComplete() == 1) {
				fmsRecv.setReadNotifiedTimestamp(now);
			}
			
			transactionTemplate.executeWithoutResult(t -> fmsRecvRepository.save(fmsRecv));
			CswLog.info(log, String.format("CSW-INBOUND %s message received id: %s", wrapper.getType(), fmsRecv.getId()));
			
		} catch (ChcException e) {
			fmsRecv.setStatusInfo(e.getMessage());
			updateWhenInError(fmsRecv);
			throw mapException(e);
		} catch (PersistenceException e) {
			CswLog.getLogData().setMessage("CSW FMS database error");
			CswLog.error(log, e);
	//		log.error("CSW FMS database error ",e);
			throw mapException(e);
		} catch (Exception e) {
			fmsRecv.setStatusInfo("Errore Interno: "+e.getMessage());
			updateWhenInError(fmsRecv);
			throw mapException(e);
			
		} finally{
			if(decryptedMessage != null) {
				try {
					Files.delete(Paths.get(decryptedMessage.getAbsolutePath()));
				} catch (IOException e) {
					// ignored
				}
			}
		}
	}
	
	private void saveBlob(File decryptedMessage, FMSRecv fmsRecv) {
		transactionTemplate.executeWithoutResult(t -> {
			fmsRecvRepository.save(fmsRecv); 	
			BlobHelper.saveBlob(BlobEntity.builder()
					.file(decryptedMessage)
					.key(fmsRecv.getId())
					.keyColum("FAS_SEQID")
					.tableName("SYNC_RECV")
					.blobColum("MESSAGE")
					.build(), entityManager);
		});
	}

	private void updateWhenInError(FMSRecv fmsRecv){
		fmsRecv.setStatusCodeBA(3);
		fmsRecv.setStatusCodeSync(0);
		fmsRecv.setStatus(FMSRecvStatus.RECEPTION_FAILED);
		fmsRecv.setCswStatus(ClientTaskStatus.FAILED);
		fmsRecv.setComplete(1);
		// TIMESTAMP
		fmsRecv.setReadErrorTimestamp(LocalDateTime.now());
		
		transactionTemplate.executeWithoutResult(t -> fmsRecvRepository.save(fmsRecv));
	}
	
	private String removeLastSlash(String str) {
		if(str != null) {
		return (str.charAt(str.length() - 1) == '/' || str.charAt(str.length() - 1) == '\\') ? str.substring(0, str.length() - 1) : str;
		}
		return str;
	}

}
