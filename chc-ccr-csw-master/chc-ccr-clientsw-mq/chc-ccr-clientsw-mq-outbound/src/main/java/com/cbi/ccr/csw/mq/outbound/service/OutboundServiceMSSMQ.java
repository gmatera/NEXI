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
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.dto.fms.MSSMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1911SecSendMsgreq;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1921SecSendMsgcnf;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1941SecSendMsgind;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMSSMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQRepository;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.ValidationService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import chc.framework.util.parsing.input.FlowioOutputMapper;
import encoding.EncodingUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class OutboundServiceMSSMQ
		extends CSWCommonOutboundServiceMQ<MSSSendMQ, MSSSendMQRepository, MSSMessageDTO, MQ1911SecSendMsgreq, ConfigurationMSSMQ>{


	protected FlowioOutputMapper<MQ1921SecSendMsgcnf> flowioMapper1921;
	protected FlowioOutputMapper<MQ1941SecSendMsgind> flowioMapper1941;

	public OutboundServiceMSSMQ(@NonNull MSSSendMQRepository dbRepository) {
		super(dbRepository, MSSSendMQ.class);
	}

	@PostConstruct
	private void initBinders() {
		flowioMapper1921 = new FlowioOutputMapper<>(BinderFactory.binder1921(null));
		flowioMapper1941 = new FlowioOutputMapper<>(BinderFactory.binder1941());
	}
	
	public void submitToHub(@NonNull MQ1911SecSendMsgreq primitiveDto) throws ChcUnrecoverableException {
		MSSSendMQ entity;
		
		CswLog.getLogData().setUdr(primitiveDto.getUdr());
		CswLog.debug(log, String.format("Performing validation of primitive: %s", primitiveDto.getId()));
		
		try {
			entity = checkDuplicates(primitiveDto);
		} catch (ChcException e) {
			mqPrimitiveSenderService.send1921ErrorWithPrimitive(0,build1921ErrorWithPrimitive(primitiveDto, e.getLocalizedMessage()));
			return;
		}

		ConfigurationMSSMQ conf = null;
		try {
			validationService.validateBaAndInterface(primitiveDto.getBaLoc(), primitiveDto.getBaRem(), ServiceType.MSS,
					RouteInterface.MQ);
			CswLog.debug(log, "MSS - validated Ba and Interface");
			validationService.validateMSSMQRegex(primitiveDto);
			CswLog.debug(log, "MSS - validated regex");
			
			conf = getConfiguration(primitiveDto.getBaLoc(),
					primitiveDto.getBaRem());
			performValidations(primitiveDto, conf);
		} catch (ChcException e) {
			if(entity == null) {
				entity = createEntity(primitiveDto, SendStatusMQ.REJECTED, LocalDateTime.now(), null);
			}
			tryToSaveOnValidationException(entity, primitiveDto, e);
			return;
		} 
		
		LocalDateTime acceptTms = LocalDateTime.now();
		if(entity == null) {
			entity = createEntity(primitiveDto, SendStatusMQ.CREATING, acceptTms, conf);
			CswLog.getLogData().setId(entity.getId().toString());
			CswLog.debug(log, String.format("Saving entity on DB from primitive: %s", primitiveDto.getId()));
		}
		
		mqPrimitiveSenderService.send1921Confirm(entity, primitiveDto, null, acceptTms);
		
		// Update entity and continue
		entity.setCswStatus(ClientTaskStatus.SENDING);
		entity = updateStatus(entity, SendStatusMQ.SENDING);
		
		List<AbstractPart> parts = null;
		try {
			parts = createPartsInternal(entity, buildMessageDTO(entity), primitiveDto);
		} catch (ChcStubException | ChcException e) {
			return;
		}
		
		entity.setSendTime(LocalDateTime.now());
		entity = genericDAO.update(entity);
		
		sendToHubInternal(entity, parts, primitiveDto);
	}
	
	
	@Override
	protected List<AbstractPart> createParts(MSSSendMQ entity, MSSMessageDTO outboundDTO)
			throws ChcException, ChcStubException {
		UUID messageKey = UUID.randomUUID();
		return Arrays.asList(getAttachmentMessage(entity, messageKey),
				getMessageWrapper(entity, ServiceType.MSS, outboundDTO, null, messageKey));
	}

	@Override
	protected String getCCRInboundControllerPath() {
		return InboundControllerPath.MSS_MESSAGE;
	}

	private void commonRequestRejected(MSSSendMQ msSend) {
		msSend.setStatus(SendStatusMQ.REJECTED);
		msSend.setCswStatus(ClientTaskStatus.FAILED);
	}

	@Override
	protected File validateAndEncryptBlob(MSSSendMQ mssSend, UUID messageKey) throws ChcException, ChcStubException {
		return super.validateAndEncryptBlob(mssSend, messageKey, BlobEntity.builder()
				.keyColum(MSSSendMQ.ID)
				.tableName(MSSSendMQ.TABLE_NAME)
				.blobColum(MSSSendMQ.BLOB_MESSAGE_FIELD)
				.key(mssSend.getId())
				.build(), entityManager);
	}

	protected void handleManagedException(MSSSendMQ mssSend, ChcException e, @NonNull MQ1911SecSendMsgreq primitive) {
		
		String code = e.getCode();
		String message = e.toString();
		mssSend.setPrimitiveError("1921");
		
		if (code.equals(I18nService.ERR_INVALID_BA.name())) {
			commonRequestRejected(mssSend);
			mssSend.setStatusInfo("BA pair not configured in the ClientSW");
			mssSend.setRejectReason(8);
			mqPrimitiveSenderService.send1921Confirm(mssSend, primitive, message, null);
		}else if(code.equals(I18nService.ERR_MISSING_CONFIGURATION.name())) {
			commonRequestRejected(mssSend);
			mssSend.setStatusInfo("Local BA not configured for MS service");
			mssSend.setRejectReason(6);
			mqPrimitiveSenderService.send1921Confirm(mssSend, primitive, message, null);
			CswLog.info(log, String.format("Sending 1921: %s", JSON.toJson(mssSend)));
		} else if (code.equals(I18nService.ERR_INVALID_INTERFACE.name())) {
			commonRequestRejected(mssSend);
			mssSend.setRejectReason(7);
			mssSend.setStatusInfo("Interface type not correct");
			mqPrimitiveSenderService.send1921Confirm(mssSend, primitive, message, null);
		} else if (code.equals(I18nCommon.ERR_VALIDATION_EXCEPTION.name())) {
			commonRequestRejected(mssSend);
			mssSend.setRejectReason(9);
			mssSend.setStatusInfo(message);
			mqPrimitiveSenderService.send1921Confirm(mssSend, primitive, message, null);
		} else if (code.equals(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION.name())) {
			commonRequestRejected(mssSend);
			mssSend.setRejectReason(17);
			mssSend.setStatusInfo(message);
//			mqPrimitiveSenderService.send1921Confirm(mssSend, primitive, message, null);
		} else if(code.equals(I18nService.ERR_INVOKING_CRYPTO_HUB.name())) {
			commonRequestRejected(mssSend);
			mssSend.setRejectReason(72);
			mssSend.setStatusInfo(message);
		} else if(code.equals(I18nService.ERR_INVOKING_HUB.name())) {
			commonRequestRejected(mssSend);
			mssSend.setRejectReason(71);
			mssSend.setStatusInfo(message);
		} else if(code.equals(I18nService.ERR_ENCRYPT.name())) {
			mssSend.setRejectReason(74);
			mssSend.setStatusInfo(message);
			}
	}

	/**
	 * MSS
	 * 
	 * All’arrivo di una primitiva 1911.Sec-Send-Msg_req il ClientSW verifica se
	 * sulla tabella MSS SEND_MQI è già presente un record avente lo stesso Mittente
	 * BA, Destinatario BA e UDR, in tal caso agisce come indicato di seguito. Se lo
	 * stato della precedente richiesta è SENDING, LOCALLY_CONFIRMED,
	 * REMOTELY_CONFIRMED, SENT o CLENABLE la nuova richiesta viene ritenuta un
	 * duplicato pertanto la primitiva 1911.Sec-Send-Msg_req viene rifiutata. Se lo
	 * stato della precedente richiesta è REJECTED, IN ERROR con sottostato FAILED,
	 * IN ERROR con sottostato ERROR_ON_HUB, il ClientSW elimina il precedente
	 * record e gestisce normalmente la nuova richiesta.
	 */
	@Override
	protected MSSSendMQ checkDuplicates(@NonNull MQ1911SecSendMsgreq messageDto) throws ChcException {
		
		MSSSendMQ entity = dbRepository.findOneByLocalBaIdAndRemoteBaIdAndUdr(messageDto.getBaLoc(), messageDto.getBaRem(),
				messageDto.getUdr());

		// not a duplicate
		if (entity == null)
			return null;

		// duplicate with invalid status
		if (!entity.getStatus().equals(SendStatusMQ.IN_ERROR) && !entity.getStatus().equals(SendStatusMQ.REJECTED)
				&& !entity.getCswStatus().equals(ClientTaskStatus.FAILED))
			throw new ChcException(I18nService.ERR_CHECKING_DUPLICATES, "Duplicate request rejected, invalid status");

		// duplicate with valid status
		dbRepository.delete(entity);

		return null;
	}

	@Override
	protected void performValidations(@NonNull MQ1911SecSendMsgreq messageDto, ConfigurationMSSMQ conf) throws ChcException {

		FlowioOutputMapper<MQ1911SecSendMsgreq> flowioMapper1911 = new FlowioOutputMapper<>(
				BinderFactory.binder1911(messageDto.getMabLen()));

		String primitive;
		try {
			primitive = new String(flowioMapper1911.writeByte(messageDto));
		} catch (NoSuchFieldException e) {

			throw new ChcException(I18nCommon.ERR_GENERIC, e.getLocalizedMessage());
		}

		lauService.checkLAU(messageDto.getLocalAuthInfo(), messageDto.getLocalAuthInfoAlg(), conf.getLauEnabled(),
				conf.getLauKey(), primitive);

		File mab = new File(localTempFolderfileMq, UUID.randomUUID() + "_mab");
		try (FileOutputStream fos = new FileOutputStream(mab)) {
			fos.write(messageDto.getMab().getBytes());
			validateBlob(messageDto, mab);
		}catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS, e.getLocalizedMessage());
		}
		
	}

	private void validateBlob(MQ1911SecSendMsgreq messageDto, File dec) throws ChcException, IOException {
		messageDto.setMabLen(validationService.validateSize(dec.getAbsolutePath(),
				(messageDto.getMabLen() != null) ? Long.valueOf(messageDto.getMabLen()) : null,
				ValidationService.MESSAGE_MAX_SIZE, true).intValue());
		try (FileInputStream fis = new FileInputStream(dec)) {
			messageDto.setMabDigest(validationService.validateSha256(fis, messageDto.getMabDigest()));
			if (messageDto.getMabDigestAlg() != null) {
				messageDto.setMabDigestAlg(ValidationService.SHA_256);
				messageDto.setMabDigestLen(messageDto.getMabDigest().length());
			}
		}
		CswLog.debug(log, String.format("Blob validated"));
	}

	@Override
	protected MSSMessageDTO buildMessageDTO(@NonNull MSSSendMQ entity) {
		MSSMessageDTO bean = mapper.map(entity, MSSMessageDTO.class);
		bean.setMsgId(entity.getId().toString());
		bean.setClientSwMessageId(entity.getId());
		bean.setBaInsertTimestamp(entity.getAcceptTime());
		return bean;

	}

	@Override
	public MSSSendMQ createEntity(@NonNull MQ1911SecSendMsgreq messageDto, SendStatusMQ status,
			LocalDateTime acceptTimestamp, ConfigurationMSSMQ configuration){

		MSSSendMQ entity = new MSSSendMQ();
		entity.setAcceptTime(acceptTimestamp);
		entity.setCswInsertTimestamp(LocalDateTime.now());
		if(status.equals(SendStatusMQ.REJECTED)) {
			entity.setCswStatus(ClientTaskStatus.FAILED);
			entity.setSendErrorTimestamp(LocalDateTime.now());
		} else
			entity.setCswStatus(ClientTaskStatus.NEW);
		entity.setStatus(status);
		entity.setLocalBaId(StringUtils.isEmpty(messageDto.getBaLoc()) ? " " : messageDto.getBaLoc());
		entity.setRemoteBaId(StringUtils.isEmpty(messageDto.getBaRem()) ? " " : messageDto.getBaRem());
		entity.setLocalBaData(EncodingUtils.encodeHexString(messageDto.getLocBaData()));
		entity.setPriority(messageDto.getPriority() == null ? 0 : messageDto.getPriority());
		entity.setTur(messageDto.getTur());
		entity.setMessageType(StringUtils.isEmpty(messageDto.getMsgType()) ? " " : messageDto.getMsgType());
		entity.setCatAppl(messageDto.getCatAppl());
		entity.setCorrelationId(messageDto.getCorrId());
		entity.setUdrLen(messageDto.getUdrLen());
		entity.setUdr(messageDto.getUdr());
		entity.setMsgDigestAlg(messageDto.getMabDigestAlg());
		entity.setMsgDigestLen(messageDto.getMabDigestLen().longValue());
		entity.setMsgDigest(messageDto.getMabDigest());
		entity.setMessageLeng(messageDto.getMabLen() == null ? 0 : messageDto.getMabLen());
		entity.setLocalAuthInfoAlg(messageDto.getLocalAuthInfoAlg());
		entity.setLocalAuthInfoLen(new Long(messageDto.getLocalAuthInfoLen()));
		entity.setLocalAuthInfo(messageDto.getLocalAuthInfo());
		entity.setBaReqTms(messageDto.getBaReqTms());
		
		if(status.equals(SendStatusMQ.REJECTED) && entity.getRejectReason() == null)
			entity.setStatusInfo("ERR_MQ_PRIMITIVE_LENGTH_MISMATCH Primitive length mismatch it is:3.615 should be:4.638");

		if(entity.getRetryCounter() == null)
			entity.setRetryCounter(0);
		
		if (entity.getMessageLeng() == null)
			entity.setMessageLeng(0);

		transactionTemplate.executeWithoutResult(t -> {
			entityManager.persist(entity);
			entityManager.flush();
		});

		if (entity.getMessageLeng() != 0) {
			return transactionTemplate.execute(t -> {
				BlobHelper.saveBlobMQ(BlobEntity.builder()
						.key(entity.getId())
						.keyColum(MSSSendMQ.ID)
						.tableName(MSSSendMQ.TABLE_NAME)
						.blobColum(MSSSendMQ.BLOB_MESSAGE_FIELD)
						.build(), messageDto.getMab().getBytes(),
						entityManager);

				entityManager.flush();
				MSSSendMQ e = entityManager.merge(entity);
				entityManager.flush();
				return e;
			});
		} 
		
		return entity;

	}

	@Override
	protected void performFileValidation(@NonNull MSSSendMQ entity, ConfigurationMSSMQ configuration) throws ChcException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void send1402or1941(MSSSendMQ entity, String message) {
		entity.setPrimitiveError("1941");
		mqPrimitiveSenderService.send1941(entity, message);
	}
	
	public MQ1921SecSendMsgcnf build1921ErrorWithPrimitive(MQ1911SecSendMsgreq primitive1911, String message) {

		MQ1921SecSendMsgcnf confirm = new MQ1921SecSendMsgcnf();
		confirm.setBaLoc(primitive1911.getBaLoc());
		confirm.setBaRem(primitive1911.getBaRem());
		confirm.setLocBaData(primitive1911.getLocBaData());
		confirm.setPriority(primitive1911.getPriority());
		confirm.setTur(primitive1911.getTur());
		confirm.setMsgType(primitive1911.getMsgType());
		confirm.setCatAppl(primitive1911.getCatAppl());
		confirm.setCorrId(primitive1911.getCorrId());
		confirm.setUdrLen(primitive1911.getUdrLen());
		confirm.setUdr(primitive1911.getUdr());
		confirm.setBaReqTms(primitive1911.getBaReqTms());
		confirm.setMabDigestAlg(primitive1911.getMabDigestAlg());
		confirm.setMabDigestLen(primitive1911.getMabDigestLen());
		confirm.setMabDigest(primitive1911.getMabDigest());
		confirm.setMabLen(primitive1911.getMabLen());
		confirm.setMab(primitive1911.getMab());
		confirm.setLocalAuthInfoAlg(primitive1911.getLocalAuthInfoAlg());
		confirm.setLocalAuthInfoLen(primitive1911.getLocalAuthInfoLen());
		confirm.setLocalAuthInfo(primitive1911.getLocalAuthInfo());
		confirm.setBaReqTms(LocalDateTime.now());
		confirm.setMsgId(0);

		if (StringUtils.isEmpty(message)) {
			confirm.setResult(0);
			confirm.setRejReason(0);
			confirm.setAcceptTms(ZonedDateTime.now());
		} else {
			confirm.setMab(primitive1911.getMab());
			confirm.setMabLen(primitive1911.getMabLen());
			confirm.setResult(1);
			confirm.setRejReason(36);
		}

		return confirm;
	}
	
	@Override
	protected MSSSendMQ savePrimitiveForRetry(MSSSendMQ serializableEntity, MQ1911SecSendMsgreq messageDto) {
		return transactionTemplate.execute(t -> {

			try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos)){
				
				oos.writeObject(messageDto);
				
				BlobHelper
						.saveBlobMQ(
								BlobEntity.builder()
								.key(serializableEntity.getId())
								.keyColum(MSSSendMQ.ID)
								.tableName(MSSSendMQ.TABLE_NAME)
								.blobColum(MSSSendMQ.BLOB_PRIMITIVE_FIELD)
								.build(),
								bos.toByteArray(), entityManager);
	
				entityManager.flush();
				MSSSendMQ e = entityManager.merge(serializableEntity);
				entityManager.flush();
				return e;
				
			}catch (IOException e1) {
				throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e1, e1.toString());
			}
		});
	}
	
	protected MQ1911SecSendMsgreq readOriginalPrimitive(MSSSendMQ entity) throws IOException, ClassNotFoundException {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			BlobHelper.readblobAsByte(BlobEntity.builder()
					.key(entity.getId())
					.keyColum(MSSSendMQ.ID)
					.tableName(MSSSendMQ.TABLE_NAME)
					.blobColum(MSSSendMQ.BLOB_PRIMITIVE_FIELD)
					.build(), entityManager, bos);
			
			try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))){
				return (MQ1911SecSendMsgreq) ois.readObject();
			}
		}
	}

	@Override
	protected ConfigurationMSSMQ getConfiguration(String localBa, String remoteBa) throws ChcException {
		return configurationService.loadMSSMQConfiguration(localBa,
				remoteBa);
	}
	
}
