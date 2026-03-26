package com.cbi.ccr.csw.db.outbound.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMSSDB;
import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.domain.mss.MSSSendDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSSendStatus;
import com.cbi.ccr.csw.dto.SubmitDTO;
import com.cbi.ccr.csw.dto.fms.MSSMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.ValidationService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.logging.LogLevel;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundServiceMSSDB extends CSWCommonOutboundServiceDB<MSSSend, MSSSendDBRepository, MSSMessageDTO>{

	@PersistenceContext
	private EntityManager entityManager;
	
	public OutboundServiceMSSDB(@NonNull MSSSendDBRepository dbRepository) {
		super(dbRepository, MSSSend.class);
	}

	@Override
	protected void takeIt(@NonNull SubmitDTO dto, MSSSend entity) throws ChcException, IOException {
		entity.setBaInsertTimestamp(LocalDateTime.now());
		entity.setStatus(MSSSendStatus.MSG_SEND_REQUEST);
		entity.setStsCode(MSSSendStatus.MSG_SEND_REQUEST.getStsCode());
		entity.setLoadTimestamp(LocalDateTime.now());
		entity.setCreateDate(format3(LocalDateTime.now()));
		entity.setBarAcqTime(format(LocalDateTime.now()));
		entity.setNetMsgId(createNetMsgId(entity.getLocalBaId(), entity.getId()));
		
	}

	@Override
	protected MSSMessageDTO performValidations(@NonNull SubmitDTO dto, MSSSend entity)
			throws ChcException, IOException {
		
		CswLog.getLogData().setUdr(entity.getRemoteRef());
		checkConfiguration(entity);
		validationService.validateBaAndInterface(entity.getLocalBaId(), entity.getRemoteBaId(), ServiceType.MSS, RouteInterface.DB);
		
		CswLog.debug(log, String.format("validateBaAndInterface DONE %s",entity));
		
		validationService.validateMSSRegex(entity);
		
		CswLog.debug(log, String.format("validateMSSRegex DONE %s", entity));
		
		ConfigurationMSSDB conf = configurationService.loadMSSConfiguration(entity.getLocalBaId(), entity.getRemoteBaId());
		
		CswLog.getLogData().setFunction("performValidations");
		CswLog.debug(log, String.format("loadMSSConfiguration DONE %s",entity));
		
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getLocalBaId()).append(entity.getRemoteBaId()).append(entity.getMsgId())
		.append(entity.getNetMsgId()).append(entity.getMsgDigestAlg()).append(entity.getMsgDigest())
		.append(entity.getRemoteRef()).append(entity.getMessageType())
		.append(entity.getCatAppl()).append(entity.getPriority()).append(entity.getTur());
		
		lauService.checkLAU(entity.getLocalAuthInfo(), entity.getLocalAuthInfoAlg(), conf.getLauEnabled(), conf.getLauKey(), sb.toString());
		
		MSSMessageDTO mssMessage = mapper.map(entity, MSSMessageDTO.class);
		// usato per indentificare le notifiche dal CCR
		mssMessage.setClientSwMessageId(entity.getId());
		mssMessage.setNetMsgId(entity.getNetMsgId());
		mssMessage.setUdr(entity.getRemoteRef());
		
		return mssMessage;
	}
	
	private void checkConfiguration(MSSSend mssSend) throws ChcException {
		configurationService.loadMSSConfiguration(mssSend.getLocalBaId(), mssSend.getRemoteBaId());
	}


	@Override
	protected List<AbstractPart> beforeSending(@NonNull SubmitDTO dto, MSSSend entity, MSSMessageDTO outboundDTO)
			throws ChcException, ChcStubException {

		entity.setFirstEasSubTime(format(LocalDateTime.now()));
		entity.setFerSubTime(format(LocalDateTime.now()));
		
		CswLog.getLogData().setFunction("beforeSending");
		CswLog.debug(log, String.format("DB update FirstEasSubTime and FerSubTime DONE %s",entity));
		
		UUID messageKey = UUID.randomUUID();
		
		return Arrays.asList(getAttachmentMessage(entity, messageKey), getMessageWrapper(entity, ServiceType.MSS, outboundDTO, null, messageKey));
	}


	@Override
	protected String getCCRInboundControllerPath() {
		return InboundControllerPath.MSS_MESSAGE;
	}
	
	@Override
	protected void onSuccess(MSSSend entity) {
		LocalDateTime now = LocalDateTime.now();
		entity.setStatus(MSSSendStatus.MSG_SEND_CONFIRM);
		entity.setStsCode(MSSSendStatus.MSG_SEND_CONFIRM.getStsCode());
		entity.setSendCnfTimestamp(now);
		entity.setSendReqTimestamp(now);
	}

	@Override
	protected void onError(MSSSend entity, Exception ex) {
		
		if(ex instanceof ChcException) {
			processException(((ChcException) ex).getCode(), entity, ex.toString());
		}else {
			
			if(entity.getStatusInfo() == null)
				entity.setStatusInfo(ex.toString());
			
			entity.setStatus(MSSSendStatus.SENDING_ERROR);
			entity.setStsCode(MSSSendStatus.SENDING_ERROR.getStsCode());
		}
		
		CswLog.getLogData().setUdr(entity.getRemoteRef());
		entity.setSendErrTimestamp(LocalDateTime.now());
		setCompleteForError(entity, ex);
	}
	
	@Override
	protected File validateAndEncryptBlob(MSSSend mssSend, UUID messageKey) throws ChcException, ChcStubException {
		return super.validateAndEncryptBlob(mssSend, messageKey, BlobEntity.builder()
				.blobColum("MAB")
				.tableName("FAS_MSG_SEND")
				.keyColum("FAS_SEQID")
				.key(mssSend.getId())
				.build(), entityManager);
		
	}
	
	@Override
	protected void validateBlob(MSSSend mssSend, File dec) throws IOException, ChcException {
		mssSend.setMessageLeng(validationService.validateSize(dec.getAbsolutePath(), 
				(mssSend.getMessageLeng() != null) ? Long.valueOf(mssSend.getMessageLeng()) : null,	ValidationService.MESSAGE_MAX_SIZE, true).intValue());
		try (FileInputStream fis = new FileInputStream(dec)) {
			mssSend.setMsgDigest(validationService.validateSha256(fis, mssSend.getMsgDigest()));
			if (mssSend.getMsgDigest() != null)
				mssSend.setMsgDigestAlg(ValidationService.SHA_256);
		}
	}
	
	private void processException(String code, MSSSend mssSend, String message) {
		if(code.equals(I18nService.ERR_INVALID_BA.name()) ||
				code.equals(I18nService.ERR_MISSING_CONFIGURATION.name())) {
			mssSend.setStatus(MSSSendStatus.INVALID_BA);
			mssSend.setStsCode(MSSSendStatus.INVALID_BA.getStsCode());
			mssSend.setStatusInfo("BA not configured");		
		}else if(code.equals(I18nService.ERR_INVALID_INTERFACE.name())) {
			mssSend.setStatus(MSSSendStatus.INVALID_INTERFACE);
			mssSend.setStsCode(MSSSendStatus.INVALID_INTERFACE.getStsCode());
			mssSend.setStatusInfo("Invalid interface");		

		}else if (code.equals(I18nCommon.ERR_VALIDATION_EXCEPTION.name()) || code.equals(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION.name())) {
			mssSend.setStatus(MSSSendStatus.MARSHALL_ERROR);
			mssSend.setStsCode(MSSSendStatus.MARSHALL_ERROR.getStsCode());
			mssSend.setStatusInfo(message);
		} else if(code.equals(I18nService.ERR_ENCRYPT.name())) {
			mssSend.setStatus(MSSSendStatus.SENDING_ERROR);
			mssSend.setStsCode(MSSSendStatus.SENDING_ERROR.getStsCode());
			mssSend.setStatusInfo("Failed to encrypt");
		}		
	}
	
	// is already in onSuccess
//	@Override
//	protected void insertCompleteTimestamps(MSSSend entity, LocalDateTime completeTms) {
//		entity.setSendReqTimestamp(completeTms);
//	}
	
}
