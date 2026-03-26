package com.cbi.ccr.csw.mq.inbound.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1405SecReceiveind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1412SecReadFileind;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQRepository;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import chc.framework.util.parsing.input.FlowioOutputMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CSWIndoundServiceFMSMQ
		extends CSWCommonFMSFTSInboundServiceMQ<FMSRecvMQ, FMSRecvMQRepository, FMSMessageDTO> {

	public CSWIndoundServiceFMSMQ(FMSRecvMQRepository dbRepository) {
		super(dbRepository, FMSRecvMQ.class);
	}

	@Autowired
	private FMSRecvMQRepository fmsRecvRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public void processFMSMessage(InputStream message, InputStream file, MessageWrapperDTO wrapper, Long id)
			throws ChcException, ChcStubException {
		if (wrapper.getType() != ServiceType.FMS)
			throw new ChcException(I18nService.INVALID_SERVICE_DATA);

		ZonedDateTime tmsEndSending = ZonedDateTime.now();

		FMSMessageDTO dto;
		try {
			dto = JSON.fromJson(encryptionUtil.decrypt(wrapper.getEncryptedBody(),
					getEncryptionServiceKey(wrapper.getWrapperKey())), FMSMessageDTO.class);
		} catch (ChcStubException e) {
			throw new ChcStubException(ErrorMessage.builder().errorCode(e.getError().getErrorCode()).httpStatus(503)
					.localizedMessage(e.getLocalizedMessage()).build());
		}

		ConfigurationFMSMQ configFMS = configurationService.loadFMSMQConfiguration(dto.getLocalBaId(),
				dto.getRemoteBaId());

		checkDuplicateUDR(dto.getUdr());

		LocalDateTime now = LocalDateTime.now();
		FMSRecvMQ fmsRecv = mapper.map(dto, FMSRecvMQ.class);
		fmsRecv.setCswInsertTimestamp(now);
		fmsRecv.setFerSubTime(dto.getTmsStartSending());
		fmsRecv.setFenSubTime(tmsEndSending.toLocalDateTime());
		fmsRecv.setHostFirstSubTms(dto.getTmsReceived());
		fmsRecv.setId(id);
		fmsRecv.setMessageLeng(dto.getMessageLeng());

		fmsRecv.setStatus(RecvStatusMQ.RECEIVING);

		fmsRecv.setTransferId(dto.getClientSwMessageId().toString());
		fmsRecv.setNetFileSize(dto.getFileSize());
		fmsRecv.setFileDigestLen((long) dto.getFileDigest().length());
		fmsRecv.setMsgDigestLen(dto.getMsgDigestAlg() == null ? 0L : new Long(dto.getMsgDigest().length()));
		fmsRecv.setUdrLen(dto.getUdr().length());

		// la property sndCharType e not null, quindi il controllo si deve fare prima di
		// salvare l'entity
		if (dto.getCodePage() == null) {
			if (configFMS.getHubCodePage() == null) {
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "missing hub code page");
			} else {
				fmsRecv.setSndCharType(configFMS.getHubCodePage());
			}
		} else {
			fmsRecv.setSndCharType(Enum.valueOf(CodePage.class, dto.getCodePage()));
		}

		List<FMSRecvMQ> list = new ArrayList<>(1);
		list.add(fmsRecv);
		final File decryptedMessage = new File(localStoragePathDecrypted, UUID.randomUUID().toString());

		transactionTemplate.executeWithoutResult(t -> list.set(0, fmsRecvRepository.save(list.get(0))));

		try {

			if (configFMS.getRcvAutoRead() == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "Rcv Auto read not present");

			encryptionUtil.decryptFile(message, decryptedMessage, getEncryptionServiceKey(wrapper.getMessageId()));

			try (FileInputStream fisMessage = new FileInputStream(decryptedMessage)) {
				if (fmsRecv.getMsgDigestAlg() != null)
					validationService.validateSha256(fisMessage, fmsRecv.getMsgDigest());
			}

			list.get(0).setFileName(removeLastSlash(configFMS.getRcvPath()) + File.separator + fmsRecv.getFileName());
			long newFileSize = handleFile(list.get(0), file, dto, configFMS, wrapper.getFileId());
			list.get(0).setNetFileSize(newFileSize);
			list.get(0).setFileSize(newFileSize);

			// il metodo handleFile imposta il CodePage se non presente nel dto
			list.get(0).setSndCharType(Enum.valueOf(CodePage.class, dto.getCodePage()));
			list.get(0).setRcvCharType(configFMS.getRcvCodePage());
			list.get(0).setLineSeparator(configFMS.getRcvLineSeparator());
			list.get(0).setRecordFormat(configFMS.getRcvRecordFormat());
			list.get(0).setMaxRecLen(dto.getMaxRecordLength());

			if (configFMS.getRcvAutoRead().booleanValue())
				list.get(0).setStatus(RecvStatusMQ.READING);
			else
				list.get(0).setStatus(RecvStatusMQ.RECEIVED);

			list.get(0).setCswStatus(ClientTaskStatus.SUCCESS);
			list.get(0).setComplete(0);

			transactionTemplate.executeWithoutResult(t -> list.set(0, fmsRecvRepository.save(list.get(0))));

			ZonedDateTime tmsCswSave = ZonedDateTime.now();
			list.get(0).setHostFirstDelTms(tmsCswSave.toLocalDateTime());

			Long idEntity = list.get(0).getId();
			transactionTemplate.executeWithoutResult(t -> {
				BlobHelper.saveBlob(BlobEntity.builder().file(decryptedMessage).key(idEntity).keyColum("ID")
						.tableName("FMS_RECV_MQI").blobColum("MESSAGE").build(), entityManager);
			});

			list.get(0).setFirstBaDlvTms(LocalDateTime.now());
			transactionTemplate.executeWithoutResult(t -> list.set(0, fmsRecvRepository.save(list.get(0))));

			ConfigurationFMSMQ configFmsMq = configurationService.loadFMSMQConfiguration(fmsRecv.getLocalBaId(),
					fmsRecv.getRemoteBaId());

			if (configFmsMq.getRcvAutoRead() == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "Rcv Auto read not present");


			if (Boolean.TRUE.equals(configFmsMq.getRcvAutoRead())) {
				processAutoRead(tmsEndSending, dto, list, decryptedMessage, tmsCswSave, configFmsMq);
			} else {
				send1409FMS(list.get(0), dto);
			}

			list.get(0).setFirstBaDlvTms(LocalDateTime.now());

			transactionTemplate.executeWithoutResult(t -> list.set(0, fmsRecvRepository.save(list.get(0))));
			CswLog.info(log, String.format("CSW-INBOUND %s message received id: %s", wrapper.getType(), list.get(0).getId()));
		} catch (ChcException e) {
			log.error("CSW FMS processing", e);
			list.get(0).setStatusInfo("Errore Interno: " + e.getMessage());
			updateWhenInError(list.get(0));
			throw mapException(e);

		} catch (PersistenceException e) {
			log.error("CSW FMS database error ", e);
			throw mapException(e);
		} catch (Exception e) {
			log.error("CSW errror receving FMS ", e);
			list.get(0).setStatusInfo("Errore Interno: " + e.getMessage());
			updateWhenInError(list.get(0));
			throw mapException(e);

		} finally {

			try {
				Files.delete(Paths.get(decryptedMessage.getAbsolutePath()));
			} catch (IOException e) {
				// ignored
			}
		}
	}

	private void processAutoRead(ZonedDateTime tmsEndSending, FMSMessageDTO dto, List<FMSRecvMQ> list,
			final File decryptedMessage, ZonedDateTime tmsCswSave, ConfigurationFMSMQ configFmsMq)
			throws ChcException, NoSuchFieldException, IOException {
		
		ZonedDateTime tmsStartWritingFileInQueue = ZonedDateTime.now();

		CswLog.getLogData().setLocalBaId(list.get(0).getLocalBaId());
		CswLog.getLogData().setRemoteBaId(list.get(0).getRemoteBaId());
		CswLog.getLogData().setUdr(list.get(0).getUdr());
		CswLog.getLogData().setVfn(list.get(0).getVfn());
		CswLog.getLogData().setId(list.get(0).getId().toString());

		ZonedDateTime tmsEndWritingFileInQueue = ZonedDateTime.now();
		ZonedDateTime acceptTms = ZonedDateTime.now();
		list.get(0).setStatus(RecvStatusMQ.DELIVERED);
		list.get(0).setStartReadTms(tmsStartWritingFileInQueue.toLocalDateTime());
		list.get(0).setEndReadTms(tmsEndWritingFileInQueue.toLocalDateTime());

		String groupId = mqPrimitiveSenderService.generateGroupId();

		MQ1405SecReceiveind mq1405 = mqPrimitiveService.build1405PrimitiveMqFMS(list.get(0), groupId, configFmsMq,
				tmsEndSending, tmsStartWritingFileInQueue, tmsEndWritingFileInQueue, tmsCswSave, acceptTms, dto,
				decryptedMessage);
		flowioMapper1405 = new FlowioOutputMapper<>(BinderFactory.binder1405(dto.getMessageLeng()));
		byte[] primitiveByte = flowioMapper1405.writeByte(mq1405);

		MQ1412SecReadFileind mq1412 = mqPrimitiveService.build1412PrimitiveFMS(list.get(0), groupId, "Messaggio di errore");
		byte[] primitiveNegative = flowioMapper1412.writeByte(mq1412);

		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1405.getId());
		p.setFileName(list.get(0).getFileName());
		p.setDestinationQueue(configFmsMq.getUploadQName());
		p.setFileGroupid(groupId);
		p.setServiceType(ServiceType.FMS);
		p.setEntityId(list.get(0).getId());
		
		mqPrimitiveSenderService.storePrimitiveWithFile(primitiveByte, primitiveNegative, p);

		if (configFmsMq.getRcvCompletionAlgo() != null && configFmsMq.getRcvCompletionAlgo()) {
			list.get(0).setStatus(RecvStatusMQ.CLEANABLE);
			list.get(0).setComplete(1);
		}
	}

	private void checkDuplicateUDR(String udr) throws ChcException {
		if (fmsRecvRepository.countUdr(udr) > 0)
			throw new ChcException(I18nService.ERR_DUPLICATE_UDR);
	}

	private void updateWhenInError(FMSRecvMQ fmsRecv) {
		fmsRecv.setComplete(1);
		fmsRecv.setCswStatus(ClientTaskStatus.FAILED);
		fmsRecv.setStatus(RecvStatusMQ.READ_ERROR);
		transactionTemplate.executeWithoutResult(t -> fmsRecvRepository.save(fmsRecv));
	}

	private String removeLastSlash(String str) {
		if (str != null) {
			return (str.charAt(str.length() - 1) == '/' || str.charAt(str.length() - 1) == '\\')
					? str.substring(0, str.length() - 1)
					: str;
		}
		return str;
	}
}
