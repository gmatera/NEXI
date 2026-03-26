package com.cbi.ccr.csw.mq.inbound.service;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.dto.fms.FTSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1412SecReadFileind;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFTSMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQRepository;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundServiceFTSMQ extends CSWCommonFMSFTSInboundServiceMQ<FTSRecvMQ, FTSRecvMQRepository, FTSMessageDTO> {
	
	public CSWIndoundServiceFTSMQ(FTSRecvMQRepository dbRepository) {
		super(dbRepository, FTSRecvMQ.class);
	}
	
	@Autowired
	private FTSRecvMQRepository ftsRecvRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public void processFTSMessage(InputStream file, MessageWrapperDTO wrapper, Long id) throws ChcException, ChcStubException {
		if(wrapper.getType() != ServiceType.FTS)
			throw new ChcException(I18nService.INVALID_SERVICE_DATA);
		
		ZonedDateTime tmsEndSending = ZonedDateTime.now();
		
		FTSMessageDTO dto;
		try {
			dto = JSON.fromJson(encryptionUtil.decrypt(wrapper.getEncryptedBody(), getEncryptionServiceKey(wrapper.getWrapperKey())),
					FTSMessageDTO.class);
		} catch ( ChcStubException e) {
			throw new ChcStubException(ErrorMessage.builder()
					.errorCode(e.getError().getErrorCode())
					.httpStatus(503)
					.localizedMessage(e.getLocalizedMessage())
					.build());
		}
		
		ConfigurationFTSMQ configFTS = configurationService.loadFTSMQConfiguration(dto.getLocalBaId(), dto.getRemoteBaId());
		
		LocalDateTime now = LocalDateTime.now();
		ZonedDateTime tmsCswSave = ZonedDateTime.now();
		FTSRecvMQ ftsRecv = mapper.map(dto, FTSRecvMQ.class);
		ftsRecv.setCswInsertTimestamp(now);
		ftsRecv.setFerSubTime(dto.getTmsStartSending());
		ftsRecv.setFenSubTime(tmsEndSending.toLocalDateTime());
		ftsRecv.setHostFirstSubTms(dto.getTmsReceived());
		ftsRecv.setHostFirstDelTms(tmsCswSave.toLocalDateTime());
		ftsRecv.setFirstBaDlvTms(LocalDateTime.now());
		ftsRecv.setId(id);
		ftsRecv.setTransferId(dto.getClientSwMessageId().toString());
		ftsRecv.setFileDigestLen(new Long(dto.getFileDigest().length()));
		
		ftsRecv.setStatus(RecvStatusMQ.RECEIVING);
		
		// la property sndCharType e not null, quindi il controllo si deve fare prima di salvare l'entity
		if(dto.getCodePage() == null) {
			if(configFTS.getHubCodePage() == null) {
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "missing hub code page");
			} else {
				ftsRecv.setSndCharType(configFTS.getHubCodePage());
			}
		}else {
			ftsRecv.setSndCharType(Enum.valueOf(CodePage.class, dto.getCodePage()));
		}

		try {
			
			
			if(configFTS.getRcvAutoRead() == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "Rcv Auto read not present");
			
			if (Boolean.TRUE.equals(configFTS.getRcvAutoRead()))
				ftsRecv.setStatus(RecvStatusMQ.READING);
			else
				ftsRecv.setStatus(RecvStatusMQ.RECEIVED);
			ftsRecv.setCswStatus(ClientTaskStatus.SUCCESS);
			ftsRecv.setComplete(Boolean.TRUE.equals(configFTS.getRcvCompletionAlgo()) ? 1 : 0);
			
			
			ftsRecv.setFileName(removeLastSlash(configFTS.getRcvPath()) + File.separator + ftsRecv.getFileName());
			long newFileSize = handleFile(ftsRecv, file, dto, configFTS, wrapper.getFileId());
			ftsRecv.setNetFileSize(newFileSize);
			ftsRecv.setFileSize(newFileSize);

			// il metodo handleFile imposta il CodePage se non presente nel dto
			ftsRecv.setSndCharType(Enum.valueOf(CodePage.class, dto.getCodePage()));
			ftsRecv.setRcvCharType(configFTS.getRcvCodePage());
			ftsRecv.setLineSeparator(configFTS.getRcvLineSeparator());
			ftsRecv.setRecordFormat(configFTS.getRcvRecordFormat());
			ftsRecv.setMaxRecLen(dto.getMaxRecordLength());

			
			if(configFTS.getRcvAutoRead() == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "Rcv Auto read not present");
			
			if (Boolean.TRUE.equals(configFTS.getRcvAutoRead())) {
				processAutoread(configFTS, ftsRecv);
			} else {
				ftsRecv.setStatus(RecvStatusMQ.DELIVERED);
				ftsRecv.setFirstBaDlvTms(LocalDateTime.now());
				send1409FTS(ftsRecv, dto);
			}
			
			transactionTemplate.executeWithoutResult(t -> ftsRecvRepository.save(ftsRecv));
			
		} catch (ChcException e) {
			log.error("CSW FMS processing",e);
			ftsRecv.setStatusInfo("Errore Interno: "+e.getLocalizedMessage());
			updateWhenInError(ftsRecv);
			throw mapException(e);
			
		} catch (PersistenceException e) {
			log.error("CSW FMS database error ",e);
			throw mapException(e);
		} catch (Exception e) {
			log.error("CSW errror receving FTS ",e);
			ftsRecv.setStatusInfo("Errore Interno: "+e.getLocalizedMessage());
			updateWhenInError(ftsRecv);
			throw mapException(e);
			
		} 
	}

	private void processAutoread(ConfigurationFTSMQ configFTS, FTSRecvMQ ftsRecv)
			throws ChcException, NoSuchFieldException {
		ZonedDateTime tmsStartWritingFileInQueue = ZonedDateTime.now();
			
		if(configFTS.getUploadQName() == null)
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "queue name not valid");

		ZonedDateTime tmsEndWritingFileInQueue = ZonedDateTime.now();
		ftsRecv.setStartReadTms(tmsStartWritingFileInQueue.toLocalDateTime());
		ftsRecv.setEndReadTms(tmsEndWritingFileInQueue.toLocalDateTime());
		ftsRecv.setStatus(RecvStatusMQ.DELIVERED);
		
		String groupId = mqPrimitiveSenderService.generateGroupId();
		
		MQ1412SecReadFileind mq1412 = mqPrimitiveService.build1412PrimitiveFTS(ftsRecv, groupId, null, null);
		byte[] primitiveByte = flowioMapper1412.writeByte(mq1412);
		
		MQ1412SecReadFileind mq1412Negative = mqPrimitiveService.build1412PrimitiveFTS(ftsRecv, groupId, "ErrorMessage", null);
		byte[] primitiveNegative = flowioMapper1412.writeByte(mq1412Negative);
		
		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1412.getId());
		p.setFileName(ftsRecv.getFileName());
		p.setDestinationQueue(configFTS.getUploadQName());
		p.setFileGroupid(groupId);
		p.setServiceType(ServiceType.FTS);
		p.setEntityId(ftsRecv.getId());
		
		mqPrimitiveSenderService.storePrimitiveWithFile(primitiveByte, primitiveNegative, p);
		
		//configFTS.getUploadQName()
		
		if (configFTS.getRcvCompletionAlgo() != null && configFTS.getRcvCompletionAlgo()) {
			ftsRecv.setStatus(RecvStatusMQ.CLEANABLE);
			ftsRecv.setComplete(1);
		}
	}

	private void updateWhenInError(FTSRecvMQ ftsRecv){
		ftsRecv.setComplete(1); 
		ftsRecv.setCswStatus(ClientTaskStatus.FAILED);
		ftsRecv.setStatus(RecvStatusMQ.RECEIVE_ERROR);
		transactionTemplate.executeWithoutResult(t -> ftsRecvRepository.save(ftsRecv));
	}
	
	private String removeLastSlash(String str) {
		if(str != null) {
		return (str.charAt(str.length() - 1) == '/' || str.charAt(str.length() - 1) == '\\') ? str.substring(0, str.length() - 1) : str;
		}
		return str;
	}
	
}
