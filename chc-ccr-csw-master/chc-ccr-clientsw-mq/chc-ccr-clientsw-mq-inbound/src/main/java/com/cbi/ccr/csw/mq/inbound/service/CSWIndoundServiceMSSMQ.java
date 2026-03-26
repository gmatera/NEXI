package com.cbi.ccr.csw.mq.inbound.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.dto.fms.MSSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.MSSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMSSMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQRepository;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundServiceMSSMQ extends CSWCommonInboundServiceMQ<MSSRecvMQ, MSSRecvMQRepository, MSSMessageDTO>{
	
	public CSWIndoundServiceMSSMQ(MSSRecvMQRepository dbRepository) {
		super(dbRepository, MSSRecvMQ.class);
	}
	
	@Autowired
	private MSSRecvMQRepository mssRecvRepository;
		
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	protected MqPrimitiveSenderService mqPrimitiveSenderService;
	
	//protected FlowioOutputMapper<MQ1951SecReceiveMsgind> flowioMapper1951;
	
	@PostConstruct
	private void initBinders() {
		
	}
	
	public void processMSSMessage(InputStream message, MessageWrapperDTO wrapper, Long id) throws ChcException, ChcStubException {
		if(wrapper.getType() != ServiceType.MSS)
			throw new ChcException(I18nService.INVALID_SERVICE_DATA);
		LocalDateTime tmsEndSending = LocalDateTime.now();
		
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

		ConfigurationMSSMQ configMSS = configurationService.loadMSSMQConfiguration(dto.getLocalBaId(), dto.getRemoteBaId());
		
		LocalDateTime now = LocalDateTime.now();
		MSSRecvMQ mssRecv = mapper.map(dto, MSSRecvMQ.class);
		
		mssRecv.setId(id);
		mssRecv.setCswInsertTimestamp(now);
		mssRecv.setFerSubTime(dto.getTmsStartSending());
		mssRecv.setFenSubTime(tmsEndSending);
		mssRecv.setHostFirstSubTms(dto.getTmsReceived());
		mssRecv.setMsgId(dto.getMsgId());
		mssRecv.setStatus(RecvStatusMQ.RECEIVING);
		mssRecv.setMessageLeng(dto.getMessageLeng());
		
//		mssRecv.setFerSubTime(LocalDateTime.parse(dto.getFerSubTime(), DateTimeFormatter.ofPattern("yyMMddhhmmssZ")));
		
		mssRecv.setMsgDigestLen((dto.getMsgDigest() == null) ? 0L : dto.getMsgDigest().length());
		mssRecv.setUdrLen(dto.getUdr() == null ? 0 : dto.getUdr().length());
	
		if(mssRecv.getPriority() == null)
			mssRecv.setPriority(0);
		
		List<MSSRecvMQ> list = new ArrayList<>(1);
		list.add(mssRecv);
		final File decryptedMessage = new File(localStoragePathDecrypted, UUID.randomUUID().toString());
		transactionTemplate.executeWithoutResult(t ->  list.set(0, mssRecvRepository.save(list.get(0))));

		try {

			list.get(0).setStatus(RecvStatusMQ.DELIVERED);
			list.get(0).setCswStatus(ClientTaskStatus.SUCCESS);
			list.get(0).setComplete(Boolean.TRUE.equals(configMSS.getRcvCompletionAlgo()) ?  1 :  0);
			
			
			encryptionUtil.decryptFile(message, decryptedMessage, getEncryptionServiceKey(wrapper.getMessageId()));
			CswLog.getLogData().setMessage("INBOUND MSS - Message decrypted");
			CswLog.getLogData().setUdr(list.get(0).getUdr());
			CswLog.getLogData().setId(list.get(0).getId().toString());
//			CswLog.debug(log);
//			mssRecv.setReceiveTime(LocalDateTime.now());

			try(FileInputStream fisMessage = new FileInputStream(decryptedMessage)){
				if(mssRecv.getMsgDigestAlg() != null)
					validationService.validateSha256(fisMessage, mssRecv.getMsgDigest());
			}

//			handleFile(mssRecv, file, dto, configFMS);
			
			CswLog.getLogData().setMessage("INBOUND FMS - Message decrypted and validated");
//			CswLog.debug(log);
			LocalDateTime tmsCswSave = LocalDateTime.now();
			//transactionTemplate.executeWithoutResult(t -> {entityManager.persist(mssRecv); entityManager.flush();});
			transactionTemplate.executeWithoutResult(t ->  list.set(0, mssRecvRepository.save(list.get(0))));

			list.get(0).setHostFirstDelTms(tmsCswSave);
			Long idEntity = list.get(0).getId();
			transactionTemplate.executeWithoutResult(t -> {
				BlobHelper.saveBlob(BlobEntity.builder()
						.file(decryptedMessage)
						.key(idEntity)
						.keyColum("ID")
						.tableName("MSS_RECV_MQI")
						.blobColum("MESSAGE")
						.build(), entityManager);
			});
			
			list.get(0).setFirstBaDlvTms(LocalDateTime.now());
			CswLog.getLogData().setMessage(String.format("CSW-INBOUND %s message received id: %s", wrapper.getType(), mssRecv.getId()));
			CswLog.getLogData().setLocalBaId(mssRecv.getLocalBaId());
			CswLog.getLogData().setRemoteBaId(mssRecv.getRemoteBaId());
			CswLog.getLogData().setUdr(mssRecv.getUdr());
			CswLog.getLogData().setId(mssRecv.getId().toString());
//			CswLog.info(log);
			
			// SEND 1951
//			CswLog.getLogData().setMessage("Sending 1951 ..");
//			CswLog.debug(log);
			mqPrimitiveSenderService.send1951(list, tmsEndSending, tmsCswSave, dto, configMSS, decryptedMessage);
			
//			CswLog.getLogData().setMessage("Sent 1951");
//			CswLog.info(log);
			
			transactionTemplate.executeWithoutResult(t ->  list.set(0, mssRecvRepository.save(list.get(0))));
			
		} catch (ChcException e) {
			log.error("CSW FMS processing",e);
			mssRecv.setStatusInfo("Errore Interno: "+e.getMessage());
			updateWhenInError(mssRecv);
			throw mapException(e);
			
		} catch (PersistenceException e) {
			log.error("CSW FMS database error ",e);
			throw mapException(e);
		} catch (Exception e) {
			log.error("CSW errror receving FMS ",e);
			mssRecv.setStatusInfo("Errore Interno: "+e.getMessage());
			updateWhenInError(mssRecv);
			throw mapException(e);
			
		} finally{
			
			try {
				Files.delete(Paths.get(decryptedMessage.getAbsolutePath()));
			} catch (IOException e) {
				// ignored
			}
		}
	}
	
//	private void altro(MSSRecvMQ mssRecv) {
//		// ATTENDE CHE LA BANCA INVII 1933 - QUANDO ARRIVA:
//		// SEND 1934 PER CONFERMARE 1933
//		MQ1934SecReleaseMsgcnf mq1934 = new MQ1934SecReleaseMsgcnf();
//		mq1934.setReqBaLoc(mssRecv.getLocalBaId());
//		mq1934.setReqBaRem(mssRecv.getRemoteBaId());
//		// TODO
//	}

	private void updateWhenInError(MSSRecvMQ mssRecv){
		mssRecv.setComplete(1);
		mssRecv.setCswStatus(ClientTaskStatus.FAILED);
		mssRecv.setStatus(RecvStatusMQ.RECEIVE_ERROR);
		transactionTemplate.executeWithoutResult(t -> mssRecvRepository.save(mssRecv));
	}
	
}
