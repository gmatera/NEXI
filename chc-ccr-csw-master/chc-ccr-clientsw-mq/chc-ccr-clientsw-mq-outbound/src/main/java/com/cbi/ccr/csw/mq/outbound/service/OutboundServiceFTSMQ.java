package com.cbi.ccr.csw.mq.outbound.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import com.cbi.ccr.csw.dto.fms.FTSMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1401SecSendFilecnf;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFTSMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQRepository;
import com.cbi.ccr.csw.service.common.FileRecordFormatValidator;
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
public class OutboundServiceFTSMQ
		extends CSWCommonFMSFTSOutboundServiceMQ<FTSSendMQ, FTSSendMQRepository, FTSMessageDTO, ConfigurationFTSMQ>{

	@Autowired
	private FileRecordFormatValidator fileRecordFormatValidator;
	
	public OutboundServiceFTSMQ(@NonNull FTSSendMQRepository dbRepository) {
		super(dbRepository, FTSSendMQ.class);
	}

	@Override
	protected String getCCRInboundControllerPath() {
		return InboundControllerPath.FTS_MESSAGE;
	}


	@Override
	protected FTSSendMQ findEntityForChekDuplicates(MQ1400SecSendFilereq messageDto) {
		// in base alla specifica funzionale, per FTS deve cercare per VFN
		return dbRepository.findOneByLocalBaIdAndRemoteBaIdAndVfn(messageDto.getBaLoc(), messageDto.getBaRem(),
				messageDto.getVfn());
	}
	
	@Override
	protected void performValidations(@NonNull MQ1400SecSendFilereq messageDto, ConfigurationFTSMQ conf) throws ChcException {
		CswLog.debug(log, "Starting FTS Validation");

//		ConfigurationFTSMQ conf = configurationService.loadFTSMQConfiguration(messageDto.getBaLoc(),
//				messageDto.getBaRem(), RouteInterface.MQ);

		FlowioOutputMapper<MQ1400SecSendFilereq> flowioMapper1400 = new FlowioOutputMapper<>(
				BinderFactory.binder1400(0));

		String primitive;
		try {
			primitive = new String(flowioMapper1400.writeByte(messageDto));
		} catch (NoSuchFieldException e) {

			throw new ChcException(I18nCommon.ERR_GENERIC, e.getLocalizedMessage());
		}

		lauService.checkLAU(messageDto.getLocalAuthInfo(), messageDto.getLocalAuthInfoAlg(), conf.getLauEnabled(),
				conf.getLauKey(), primitive);

	}

	@Override
	public FTSSendMQ createEntity(@NonNull MQ1400SecSendFilereq dto, SendStatusMQ status,
			LocalDateTime acceptTimestamp, ConfigurationFTSMQ configuration){

		
		LocalDateTime now = LocalDateTime.now();

		FTSSendMQ entity = new FTSSendMQ();
		entity.setAcceptTime(acceptTimestamp);
		entity.setStartCreateTimestamp(now);
		entity.setCswInsertTimestamp(now);
		
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
		entity.setFileSize(dto.getBaFileSize() == null ? 0 : dto.getBaFileSize().longValue());
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
//			ConfigurationFTSMQ conf = configurationService.loadFTSMQConfiguration(dto.getBaLoc(), dto.getBaRem(), RouteInterface.MQ);
//			chackConfiguration(dto, conf, entity);
//		} catch(Exception e) {
//			log.debug("Managed exception: {}", e.getLocalizedMessage());
//			entity.setCharType(CodePage.getEnumByValueMQ(dto.getCharType()));
//			entity.setLineSeparator(LineSeparator.getEnumByLabelMq(dto.getLineSeparator()));
//			entity.setRecordFormat(RecordFormat.getEnumByValueMQ(dto.getRecType()));
//			entity.setMaxRecLen(dto.getMaxRecLen());	
//		}
			
		entity.setAdfLen(dto.getAdfLen());
		entity.setAdf(dto.getAdf());
		entity.setMessageType(dto.getMsgType());
		entity.setLocalBaData(new String(dto.getLocalBaData()).replace("\0", ""));
		entity.setFileDigestAlg(dto.getSndBaFileDigestAlg());
		entity.setFileDigestLen(dto.getSndBaFileDigestLen());
		entity.setFileDigest(dto.getSndBaFileDigest());
		entity.setLocalAuthInfoAlg(dto.getLocalAuthInfoAlg());
		entity.setLocalAuthInfoLen(dto.getLocalAuthInfoLen());
		entity.setLocalAuthInfo(dto.getLocalAuthInfo());
		entity.setFileName(localTempFolderfileMq + File.separator + UUID.randomUUID());
		entity.setEndCreateTimestamp(now);
		
		if(entity.getRetryCounter() == null)
			entity.setRetryCounter(0);
		
		transactionTemplate.executeWithoutResult(t -> {
			entityManager.persist(entity);
			entityManager.flush();
		});

		return entity;
	}

	@Override
	protected List<AbstractPart> createParts(FTSSendMQ entity, FTSMessageDTO outboundDTO)
			throws ChcException, ChcStubException {
		UUID fileKey = UUID.randomUUID();
		
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
		
		return Arrays.asList(attch,
				getMessageWrapper(entity, ServiceType.FTS, outboundDTO, fileKey, null));
	}

	@Override
	protected void performFileValidation(@NonNull FTSSendMQ entity, ConfigurationFTSMQ conf) throws ChcException {
	
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
	protected ConfigurationFTSMQ getConfiguration(String localBa, String remoteBa) throws ChcException {
		return configurationService.loadFTSMQConfiguration(localBa, remoteBa);
	}
	
	@Override
	protected void send1401Confirm(@NonNull FTSSendMQ entity, @NonNull MQ1400SecSendFilereq primitiveDto, String message,	LocalDateTime acceptTms){
		mqPrimitiveSenderService.send1401Confirm(entity, primitiveDto, message, acceptTms);
	}

	@Override
	protected void send1402(FTSSendMQ entity, String message){
		mqPrimitiveSenderService.send1402(entity, message, null);
	}
	@Override
	protected void send1413(FTSSendMQ entity, MQ1400SecSendFilereq primitiveDto){
		mqPrimitiveSenderService.send1413(entity, primitiveDto);	
	}
	@Override
	protected void send1402or1941(FTSSendMQ entity, String message) {
		entity.setPrimitiveError("1402");		
		send1402(entity, message);
	}

	@Override
	protected File validateAndEncryptBlob(FTSSendMQ entity, UUID messageKey) throws ChcException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected FTSMessageDTO buildMessageDTO(@NonNull FTSSendMQ entity) {
		FTSMessageDTO bean = mapper.map(entity, FTSMessageDTO.class);
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
	protected FTSSendMQ savePrimitiveForRetry(FTSSendMQ serializableEntity, MQ1400SecSendFilereq messageDto) {
		return transactionTemplate.execute(t -> {

			try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos)){
				
				oos.writeObject(messageDto);
				
				BlobHelper
						.saveBlobMQ(
								BlobEntity.builder()
								.key(serializableEntity.getId())
								.keyColum(FTSSendMQ.ID)
								.tableName(FTSSendMQ.TABLE_NAME)
								.blobColum(FTSSendMQ.BLOB_PRIMITIVE_FIELD)
								.build(),
								bos.toByteArray(), entityManager);
	
				entityManager.flush();
				FTSSendMQ e = entityManager.merge(serializableEntity);
				entityManager.flush();
				return e;
				
			}catch (IOException e1) {
				throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e1, e1.toString());
			}
		});
	}
	
	protected MQ1400SecSendFilereq readOriginalPrimitive(FTSSendMQ entity) throws IOException, ClassNotFoundException {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			BlobHelper.readblobAsByte(BlobEntity.builder()
					.key(entity.getId())
					.keyColum(FTSSendMQ.ID)
					.tableName(FTSSendMQ.TABLE_NAME)
					.blobColum(FTSSendMQ.BLOB_PRIMITIVE_FIELD)
					.build(), entityManager, bos);
			
			try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))){
				return (MQ1400SecSendFilereq) ois.readObject();
			}
		}
	}

}
