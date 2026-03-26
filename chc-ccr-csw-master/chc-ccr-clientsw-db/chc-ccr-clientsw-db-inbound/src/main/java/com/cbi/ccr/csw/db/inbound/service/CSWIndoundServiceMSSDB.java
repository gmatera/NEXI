package com.cbi.ccr.csw.db.inbound.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMSSDB;
import com.cbi.ccr.csw.domain.mss.MSSRecv;
import com.cbi.ccr.csw.domain.mss.MSSRecvDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSRecvStatus;
import com.cbi.ccr.csw.dto.fms.MSSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.MSSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundServiceMSSDB extends CSWCommonInboundServiceDB<MSSRecv, MSSRecvDBRepository, MSSMessageDTO>{

	@PersistenceContext
	private EntityManager entityManager;
	
	public CSWIndoundServiceMSSDB(MSSRecvDBRepository dbRepository) {
		super(dbRepository, MSSRecv.class);
	}

	public void processMSSMessage(InputStream message, MessageWrapperDTO wrapper, Long id) throws ChcException, ChcStubException {

		if (wrapper.getType() != ServiceType.MSS)
			throw new ChcException(I18nService.INVALID_SERVICE_DATA);

		LocalDateTime now = LocalDateTime.now();
		MSSRecv mssRecv = new MSSRecv();
		mssRecv.setReceiveTimestamp(now);
		mssRecv.setCreateDate(format3(now));
		mssRecv.setFirstBarSubTime(format(now));
		
		MSSInboundMessageDTO dto;
		try {
			dto = JSON.fromJson(encryptionUtil.decrypt(wrapper.getEncryptedBody(), getEncryptionServiceKey(wrapper.getWrapperKey())),
					MSSInboundMessageDTO.class);
		} catch (ChcStubException e) {
			throw new ChcStubException(ErrorMessage.builder()
					.errorCode(e.getError().getErrorCode())
					.httpStatus(503)
					.localizedMessage(e.getLocalizedMessage())
					.build());
		}

		ConfigurationMSSDB config = configurationService.loadMSSConfiguration(dto.getLocalBaId(), dto.getRemoteBaId());
		
		final File decryptedMessage = new File(localStoragePathDecrypted, UUID.randomUUID().toString());
		try {
			
			mapper.map(dto, mssRecv);
			mssRecv.setId(id);
			mssRecv.setRemoteRef(dto.getUdr());
			mssRecv.setMessageType(dto.getMessageType());
			mssRecv.setLocalAuthInfo(dto.getLocalAuthInfo());
			mssRecv.setLocalAuthInfoAlg(dto.getLocalAuthInfoAlg());
			mssRecv.setPriority((dto.getPriority() == null) ? 0 : dto.getPriority());

			mssRecv.setApplCheck(0);
//			mssRecv.setComplete(mssRecv.getApplCheck() == 1 ? 1 : 0);
			
			// Timestamp di quando il CCR riceve il messaggio da consegnare al destinatario.
			mssRecv.setEasSubTime(format(dto.getTmsReceived()));
			// Timestamp di quando il CCR inizia ad inviare il messaggio al ClientSW del ricevente.
			mssRecv.setFerSubTime(format(dto.getTmsStartSending()));
			// Timestamp di quando il CCR inizia ad inviare il messaggio al ClientSW del ricevente.
			mssRecv.setFenDelTime(format(dto.getTmsStartSending()));
			
			now = LocalDateTime.now();
			// Timestamp di quando il ClientSW inizia ad inviare il messaggio alla BA ricevente.
			mssRecv.setFerDelTime(format(now));
			
			// change status to MSG_CONFIRMED
			mssRecv.setStsCode(MSSRecvStatus.MSG_CONFIRMED.getStsCode());

			// aggiorno i timestamp
			transactionTemplate.executeWithoutResult(t -> {
				dbRepository.save(mssRecv); 	
			});
			
			encryptionUtil.decryptFile(message, decryptedMessage, getEncryptionServiceKey(wrapper.getMessageId()));

			try (FileInputStream fisMessage = new FileInputStream(decryptedMessage)) {
				if (mssRecv.getMsgDigestAlg() != null)
					validationService.validateSha256(fisMessage, mssRecv.getMsgDigest());
			}
			
			mssRecv.setMessageLeng(dto.getMessageLeng());
			mssRecv.setNetMsgId(dto.getNetMsgId());
			
			// inserire LAU
//			if(config.getLauEnabled().booleanValue())
//				lauService.checkLauInboundMSSDB(mssRecv, config.getLauKey());
			
//			transactionTemplate.executeWithoutResult(t -> {
//				dbRepository.save(mssRecv); 	
//			});
			// update del blom deve essere fatto dopo aver committato il recod altrimenti non funziona
			transactionTemplate.executeWithoutResult(t -> {
				BlobHelper.saveBlob(BlobEntity.builder()
						.blobColum("MAB")
						.file(decryptedMessage)
						.key(mssRecv.getId())
						.keyColum("SEQID")
						.tableName("FAS_MSG_RECV")
						.build(), entityManager);
			}); 
			
			checkToComplete(config, mssRecv);
			
			mssRecv.setStatus(MSSRecvStatus.MSG_CONFIRMED);
			mssRecv.setCswStatus(ClientTaskStatus.SUCCESS);

			now = LocalDateTime.now();
			// Timestamp di quando il messaggio viene messo nello stato CONFERMATO MSG.
			mssRecv.setRecvRcTimestamp(now);
			// Timestamp di quando il ClientSW termina l’invio del messaggio alla BA ricevente.
			mssRecv.setLastBarSubTime(format(now));
			
			// Il messaggio è pronto per essere eliminato dal processo di pulizia (COMPLETE=true).
			if(mssRecv.getComplete() == 1)
				mssRecv.setBarAcqTime(format(now));
			
			// aggiorno i timestamp
			transactionTemplate.executeWithoutResult(t -> {
				dbRepository.save(mssRecv); 	
			});

			CswLog.getLogData().setLocalBaId(mssRecv.getLocalBaId());
			CswLog.getLogData().setRemoteBaId(mssRecv.getRemoteBaId());
			CswLog.getLogData().setFunction("processMSSMessage");
			CswLog.getLogData().setUdr(mssRecv.getRemoteRef());
			CswLog.info(log, String.format("CSW-INBOUND %s message received id: %s", wrapper.getType(), mssRecv.getId()));
			
		} catch (ChcException e) {
			mssRecv.setStatusInfo(e.getMessage());
			updateWhenInError(mssRecv);
			throw mapException(e);
		} catch (Exception e) {
			mssRecv.setStatusInfo("Errore Interno: " + e.getMessage());
			updateWhenInError(mssRecv);
			throw mapException(e);
		} finally {
			try {
				Files.delete(decryptedMessage.toPath());
			} catch (IOException e) {
				// ignored
			}
		}
	}

	private void updateWhenInError(MSSRecv mssRecv) {
		transactionTemplate.executeWithoutResult(t -> {
			//mssRecv.setMessage(null);
			MSSRecv mssRecv2 = entityManager.merge(mssRecv);
			mssRecv2.setStsCode(MSSRecvStatus.MSG_RECEIVE_ERROR.getStsCode());
			mssRecv2.setStatus(MSSRecvStatus.MSG_RECEIVE_ERROR);
			mssRecv2.setStatusInfo("Errore dell'interfaccia di messaggistica del ricevitore.");
			mssRecv2.setCswStatus(ClientTaskStatus.FAILED);
			mssRecv2.setComplete(1);
			entityManager.persist(mssRecv2);
		});
	}
}
