package com.cbi.ccr.csw.mq.outbound.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.domain.i.CswEntityOutWithFile;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1401SecSendFilecnf;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQRepository;
import com.cbi.ccr.csw.service.common.FileRecordFormatValidator;
import com.cbi.ccr.csw.service.common.ValidationService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import chc.framework.util.parsing.input.FlowioOutputMapper;
import encoding.EncodingUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class OutboundServiceFMSMQ
		extends CSWCommonFMSFTSOutboundServiceMQ<FMSSendMQ, FMSSendMQRepository, FMSMessageDTO, ConfigurationFMSMQ>{
	
	@Autowired
	private FileRecordFormatValidator fileRecordFormatValidator;
	
	public OutboundServiceFMSMQ(FMSSendMQRepository dbRepository) {
		super(dbRepository, FMSSendMQ.class);
	}

	@Override
	protected String getCCRInboundControllerPath() {
		return InboundControllerPath.FMS_MESSAGE;
	}

	@Override
	protected FMSSendMQ findEntityForChekDuplicates(MQ1400SecSendFilereq messageDto) {
		// in base alla specifica funzionale, per FMS deve cercare sia per UDR che VFN
		return dbRepository.findOneByLocalBaIdAndRemoteBaIdAndVfnAndUdr(messageDto.getBaLoc(), messageDto.getBaRem(),
				messageDto.getVfn(), messageDto.getUdr());
	}
	
	@Override
	protected void performValidations(@NonNull MQ1400SecSendFilereq messageDto, ConfigurationFMSMQ conf) throws ChcException {
		CswLog.debug(log, "Starting FMS Validation");
		
		//CswLog.getLogData().setFunction("performValidations");
		
		FlowioOutputMapper<MQ1400SecSendFilereq> flowioMapper1400 = new FlowioOutputMapper<>(
				BinderFactory.binder1400(messageDto.getMabLen()));

		String primitive;
		try {
			primitive = new String(flowioMapper1400.writeByte(messageDto));
		} catch (NoSuchFieldException e) {

			throw new ChcException(I18nCommon.ERR_GENERIC, e.getLocalizedMessage());
		}

		lauService.checkLAU(messageDto.getLocalAuthInfo(), messageDto.getLocalAuthInfoAlg(), conf.getLauEnabled(),
				conf.getLauKey(), primitive);

		File mab = new File(localTempFolderfileMq, UUID.randomUUID() + "_mab");
		try (FileOutputStream fos = new FileOutputStream(mab)) {
			fos.write(messageDto.getMab().getBytes());
			validateBlob(messageDto, mab);
		}catch(IOException e) {
			throw new ChcException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS, e.getLocalizedMessage());
		}
	}

	@Override
	protected File validateAndEncryptBlob(FMSSendMQ fmsSend, UUID messageKey) throws ChcException, ChcStubException {
		return super.validateAndEncryptBlob(fmsSend, messageKey, BlobEntity.builder()
				.keyColum(FMSSendMQ.ID)
				.tableName(FMSSendMQ.TABLE_NAME)
				.blobColum(FMSSendMQ.BLOB_MESSAGE_FIELD)
				.key(fmsSend.getId())
				.build(), entityManager);
	}

	private void validateBlob(MQ1400SecSendFilereq messageDto, File dec) throws ChcException, IOException {
		messageDto.setMabLen(validationService.validateSize(dec.getAbsolutePath(),
				(messageDto.getMabLen() != null) ? Long.valueOf(messageDto.getMabLen()) : null,
				ValidationService.MESSAGE_MAX_SIZE, true).intValue());
		try (FileInputStream fis = new FileInputStream(dec)) {
			messageDto.setMabDigest(validationService.validateSha256(fis, messageDto.getMabDigest()));
			if (messageDto.getMabDigestAlg() != null) {
				messageDto.setMabDigestAlg(ValidationService.SHA_256);
				messageDto.setMabDigestLen(Long.valueOf(messageDto.getMabDigest().length()));
			}
		}
		CswLog.debug(log, "Blob validated");
	}


	@Override
	public FMSSendMQ createEntity(@NonNull MQ1400SecSendFilereq dto, SendStatusMQ status,
			LocalDateTime acceptTimestamp, ConfigurationFMSMQ configuration) {

		LocalDateTime now = LocalDateTime.now();
		FMSSendMQ entity = new FMSSendMQ();
		entity.setAcceptTime(acceptTimestamp);
		entity.setCswInsertTimestamp(now);
		entity.setStartCreateTimestamp(now);
		if (status.equals(SendStatusMQ.REJECTED)) {
			entity.setCswStatus(ClientTaskStatus.FAILED);
			entity.setSendErrorTimestamp(LocalDateTime.now());
		} else
			entity.setCswStatus(ClientTaskStatus.NEW);
		entity.setStatus(status);
		
		entity.setLocalBaId(StringUtils.isEmpty(dto.getBaLoc()) ? " ": dto.getBaLoc());
		entity.setRemoteBaId(StringUtils.isEmpty(dto.getBaRem()) ? " ": dto.getBaRem());
		
		entity.setSendType(dto.getSendType());
		entity.setCorrelationId(dto.getCorrId());
		if (StringUtils.isEmpty(dto.getVfn()))
			entity.setVfn(createVfn(dto));
		else
			entity.setVfn(dto.getVfn());
		entity.setFileSize((dto.getBaFileSize() == null) ? 0: dto.getBaFileSize().longValue());
		entity.setQuequeFileName(StringUtils.isEmpty(dto.getQueueFileName()) ? " " : dto.getQueueFileName());
		entity.setGroupId(dto.getGroupId() == null ? " " : EncodingUtils.encodeHexString(dto.getGroupId()));
		
		if(configuration == null) {
			// proviamo a valorizzare campi che sono not null nel database
			// prendendoli dalla primitiva
			// TODO togliere tutti i not null dal DB
			entity.setCharType(CodePage.getEnumByValueMQ(dto.getCharType()));
			entity.setLineSeparator(LineSeparator.getEnumByLabelMq(dto.getLineSeparator()));
			entity.setRecordFormat(RecordFormat.getEnumByValueMQ(dto.getRecType()));
			entity.setMaxRecLen(dto.getMaxRecLen());		
		}else {
			updateEntityOnConfiguration(dto, configuration, entity);
		}
		// sotituito dal codice precedente, blocco da eliminare
//		try {
//			ConfigurationFMSMQ conf = configurationService.loadFMSMQConfiguration(dto.getBaLoc(), dto.getBaRem());
//			chackConfiguration(dto, conf, entity);
//		} catch(ChcException e) {
//			log.debug("Managed exception: {}", e.getLocalizedMessage());
//			entity.setCharType(CodePage.getEnumByValueMQ(dto.getCharType()));
//			entity.setLineSeparator(LineSeparator.getEnumByLabelMq(dto.getLineSeparator()));
//			entity.setRecordFormat(RecordFormat.getEnumByValueMQ(dto.getRecType()));
//			entity.setMaxRecLen(dto.getMaxRecLen());
//		}

		entity.setAdfLen(dto.getAdfLen());
		entity.setAdf(dto.getAdf());
		entity.setUdrLen(dto.getUdrLen() == null ? 0 : dto.getUdrLen());
		entity.setUdr(StringUtils.isEmpty(dto.getUdr()) ? " " : dto.getUdr());
		entity.setTur(dto.getTur());
		entity.setMessageType(StringUtils.isEmpty(dto.getMsgType()) ? " " : dto.getMsgType());
		entity.setCatAppl(dto.getCatAppl());
		entity.setLocalBaData(new String(dto.getLocalBaData()).replace("\0", ""));
		entity.setFileDigestAlg(dto.getSndBaFileDigestAlg());
		entity.setFileDigestLen(dto.getSndBaFileDigestLen() == null ? null : dto.getSndBaFileDigestLen().longValue());
		entity.setFileDigest(dto.getSndBaFileDigest());
		entity.setMsgDigestAlg(dto.getMabDigestAlg());
		entity.setMsgDigestLen(dto.getMabDigestLen()  == null ? null : dto.getMabDigestLen().longValue());
		entity.setMsgDigest(dto.getMabDigest());
		entity.setMessageLeng(dto.getMabLen() == null ? 0 : dto.getMabLen());
		entity.setLocalAuthInfoAlg(dto.getLocalAuthInfoAlg());
		entity.setLocalAuthInfoLen(dto.getLocalAuthInfoLen()  == null ? null : dto.getLocalAuthInfoLen());
		entity.setLocalAuthInfo(dto.getLocalAuthInfo());
		
		if(status.equals(SendStatusMQ.REJECTED) && entity.getRejectReason() == null)
			entity.setStatusInfo("ERR_MQ_PRIMITIVE_LENGTH_MISMATCH Primitive length mismatch it is:3.938 should be:4.437");

		// NON SI PUO' USARE L'ID
		// se usi l'entity manager, non puoi utilizzare il repository o genericDao
		entity.setFileName(localTempFolderfileMq + File.separator + UUID.randomUUID());
		entity.setEndCreateTimestamp(LocalDateTime.now());

		if(entity.getRetryCounter() == null)
			entity.setRetryCounter(1);
		
		if (entity.getMessageLeng() == null)
			entity.setMessageLeng(0);

		transactionTemplate.executeWithoutResult(t -> {
			entityManager.persist(entity);
			entityManager.flush();
		});
		// transactionTemplate.executeWithoutResult(t -> );

		// Long id = dbRepository.saveAndFlush(entity).getId();

		if (dto.getMab() != null) {
			return transactionTemplate.execute(t -> {

				BlobHelper
						.saveBlobMQ(
								BlobEntity.builder()
								.key(entity.getId())
								.keyColum(FMSSendMQ.ID)
								.tableName(FMSSendMQ.TABLE_NAME)
								.blobColum(FMSSendMQ.BLOB_MESSAGE_FIELD)
								.build(),
								dto.getMab().getBytes(), entityManager);

				entityManager.flush();
				FMSSendMQ e = entityManager.merge(entity);
				entityManager.flush();
				return e;
			});
		} else {
			return entity;
		}
	}

	@Override
	protected List<AbstractPart> createParts(FMSSendMQ entity, FMSMessageDTO outboundDTO) throws ChcStubException, ChcException{

		UUID fileKey = UUID.randomUUID();
		UUID messageKey = UUID.randomUUID();

		File converted = convertFileToHubFormatMQ(entity);
		// non deve prendere la dimensione del file crittografato
		outboundDTO.setFileSize(converted.length());
		
		FilePart attch = buildAttachmentFile(entity, fileKey, converted);
		outboundDTO.setFileName(attch.getFileName());
		
		// se non è binario, sun CCR deve sempre arrivare un file ASCII con LineSeparator.CRLF_0X0D0A, 
		// il metodo getAttachmentFile converte EBCDIC in ASCI
		if(entity.getCharType() != CodePage.BINARY) {
			outboundDTO.setLineSeparator(LineSeparator.CRLF_0X0D0A.name());
			outboundDTO.setCodePage(CodePage.ASCII.name());
		}
		// non più necessaria
//		if(outboundDTO.getCodePage().equals(CodePage.ASCII.name())) {
//			outboundDTO.setLineSeparator(LineSeparator.CRLF_0X0D0A.name());
//		}
		
		return Arrays.asList(getAttachmentMessage(entity, messageKey),
				attch,
				getMessageWrapper(entity, ServiceType.FMS, outboundDTO, fileKey, messageKey));
	}

	@Override
	protected FMSMessageDTO buildMessageDTO(@NonNull FMSSendMQ entity) {
		FMSMessageDTO bean = mapper.map(entity, FMSMessageDTO.class);
		bean.setFileName(new File(entity.getFileName()).getName());
		bean.setCodePage(entity.getCharType().name());
		bean.setClientSwMessageId(entity.getId());

		bean.setMaxRecordLength(entity.getMaxRecLen());
		bean.setLineSeparator(entity.getLineSeparator().name());
		bean.setCodePage(entity.getCharType().name());
		bean.setRecordFormat(entity.getRecordFormat().name());
		
		bean.setBaInsertTimestamp(entity.getAcceptTime());

		return bean;
	}

	@Override
	protected void performFileValidation(@NonNull FMSSendMQ entity, ConfigurationFMSMQ conf) throws ChcException {
		CswLog.debug(log,"Starting FMS File Validation");

		try {
			validateFileDigestAndSize(entity, conf);
			// per MQ la validazione deve usare l'entity, che è stata rimappata con la config
			fileRecordFormatValidator.processFileValidation(entity.getFileName(), 
					entity.getCharType(), entity.getLineSeparator(), 
					entity.getMaxRecLen(), entity.getRecordFormat());
		} catch (ChcException e) {
			if (e.getCode().equals(I18nCommon.ERR_VALIDATION_EXCEPTION.name())) {
				throw new ChcException(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION, e.getMessage());
			} else
				throw e;
		}

	}
	
	@Override
	protected void validateFileMD5(CswEntityOutWithFile fmsSend, FileInputStream is1) throws ChcException {
		// must not do MD5 for MQ
	}

	@Override
	protected ConfigurationFMSMQ getConfiguration(String localBa, String remoteBa) throws ChcException {
		return configurationService.loadFMSMQConfiguration(localBa, remoteBa);
	}
	
	@Override
	protected void send1401Confirm(@NonNull FMSSendMQ entity, @NonNull MQ1400SecSendFilereq primitiveDto, String message,	LocalDateTime acceptTms){
		mqPrimitiveSenderService.send1401Confirm(entity, primitiveDto, message, acceptTms);
	}

	@Override
	protected void send1402(FMSSendMQ entity, String message){
		mqPrimitiveSenderService.send1402(entity, message, null); 
	}
	@Override
	protected void send1413(FMSSendMQ entity, MQ1400SecSendFilereq primitive){
		mqPrimitiveSenderService.send1413(entity, primitive);	
	}
	@Override
	protected void send1402or1941(FMSSendMQ entity, String message) {
		entity.setPrimitiveError("1402");
		send1402(entity, message);
	}
	
	public byte[] getBytes(String str) {
		char[] chars = str.toCharArray();
		byte[] bytes = new byte[chars.length];
		for (int i = 0; i < chars.length; i++) {
			bytes[i] = (byte) (chars[i]);

		}

		return bytes;
	}

	@Override
	protected FMSSendMQ savePrimitiveForRetry(FMSSendMQ entity, MQ1400SecSendFilereq serializablePrimitive) {
		return transactionTemplate.execute(t -> {

			try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos)){
				
				oos.writeObject(serializablePrimitive);
				
				BlobHelper
						.saveBlobMQ(
								BlobEntity.builder()
								.key(entity.getId())
								.keyColum(FMSSendMQ.ID)
								.tableName(FMSSendMQ.TABLE_NAME)
								.blobColum(FMSSendMQ.BLOB_PRIMITIVE_FIELD)
								.build(),
								bos.toByteArray(), entityManager);
	
				entityManager.flush();
				return entityManager.merge(entity);
				
			}catch (IOException e1) {
				throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e1, e1.toString());
			}
		});
	}
	
	protected MQ1400SecSendFilereq readOriginalPrimitive(FMSSendMQ entity) throws IOException, ClassNotFoundException {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			BlobHelper.readblobAsByte(BlobEntity.builder()
					.key(entity.getId())
					.keyColum(FMSSendMQ.ID)
					.tableName(FMSSendMQ.TABLE_NAME)
					.blobColum(FMSSendMQ.BLOB_PRIMITIVE_FIELD)
					.build(), entityManager, bos);
			
			try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))){
				return (MQ1400SecSendFilereq) ois.readObject();
			}
		}
	}
}
