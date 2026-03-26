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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFMSDB;
import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fms.FMSSendDBRepository;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.dto.SubmitDTO;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.ValidationService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundServiceFMSDB extends CSWCommonOutboundServiceDB<FMSSend, FMSSendDBRepository, FMSMessageDTO>{

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private FileFileRecordFormatValidatorDB fileFileRecordFormatValidator;
	
	public OutboundServiceFMSDB(FMSSendDBRepository dbRepository) {
		super(dbRepository, FMSSend.class);
	}

	@Override
	protected void takeIt(@NonNull SubmitDTO dto, FMSSend entity) throws ChcException, IOException {
		entity.setBaInsertTimestamp(LocalDateTime.now());
	}

	@Override
	protected FMSMessageDTO performValidations(@NonNull SubmitDTO dto, FMSSend entity)
			throws ChcException, IOException {

		ConfigurationFMSDB conf = getConfiguration(entity);
		
		CswLog.getLogData().setFunction("performValidations");
		CswLog.getLogData().setUdr(entity.getUdr());
		CswLog.getLogData().setVfn(entity.getVfn());
		CswLog.debug(log, String.format("loadFMSConfiguration DONE %s",entity));
		
		validationService.validateBaAndInterface(entity.getLocalBaId(), entity.getRemoteBaId(), ServiceType.FMS,
				RouteInterface.DB);
		
		CswLog.debug(log,String.format("validateBaAndInterface DONE %s",entity));
		
		validationService.validateFMSRegex(entity);
		
		CswLog.debug(log, String.format("validateFMSRegex DONE %s", entity));
		
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getLocalBaId()).append(entity.getRemoteBaId())
		.append(entity.getVfn()).append(entity.getUdr()).append(entity.getFileName())
		.append(entity.getMessageType()).append(entity.getMsgDigestAlg())
		.append(entity.getMsgDigest()).append(entity.getFileDigestAlg())
		.append(entity.getFileDigest()).append(entity.getTur()).append(entity.getCatAppl());
		
		lauService.checkLAU(entity.getLocalAuthInfo(), entity.getLocalAuthInfoAlg(), conf.getLauEnabled(), conf.getLauKey(), sb.toString());

		validateFileDigestAndSize(entity, conf);
		// per DB la validazione usa la config
		fileFileRecordFormatValidator.processFileValidation(conf, entity.getFileName());

		FMSMessageDTO fmsMessage = mapper.map(entity, FMSMessageDTO.class);
		// usato per indentificare le notifiche dal CCR
		fmsMessage.setClientSwMessageId(entity.getId());
		fmsMessage.setFileName(new File(entity.getFileName()).getName());

		fmsMessage.setCodePage(conf.getSndCodePage().name());
		if (!fmsMessage.getCodePage().equals(CodePage.BINARY.name())) {
			fmsMessage.setRecordFormat(conf.getSndRecordFormat().name());
			fmsMessage.setLineSeparator(conf.getSndLineSeparator().name());
			fmsMessage.setMaxRecordLength(conf.getSndMaxRecLength());
		}
		return fmsMessage;
	}

	private ConfigurationFMSDB getConfiguration(FMSSend entity) throws ChcException {
		return configurationService.loadFMSConfiguration(entity.getLocalBaId(), entity.getRemoteBaId());
	}

	@Override
	protected List<AbstractPart> beforeSending(@NonNull SubmitDTO dto, FMSSend entity, FMSMessageDTO outboundDTO)
			throws ChcException, ChcStubException {

		LocalDateTime now = LocalDateTime.now();
		entity.setStatusCodeBA(FMSSendStatus.ACCEPTED.getStCodeBA());
		entity.setStatusCodeSync(FMSSendStatus.ACCEPTED.getStCodeSync());
		entity.setStatus(FMSSendStatus.ACCEPTED);
		entity.setAcceptTime(now);

		now = LocalDateTime.now();
		entity.setFtsSendTime(now);
		entity.setCrtSubMssTmp(now);
		entity.setSendAcceptedTimestamp(now);
		
		CswLog.getLogData().setFunction("beforeSending");
		CswLog.debug(log, String.format("DB update FtsSendTime and CrtSubMssTmp DONE %s",entity));
//		log(entity, "DB update FtsSendTime and CrtSubMssTmp DONE", LogLevel.DEBUG);

		UUID fileKey = UUID.randomUUID();
		UUID messageKey = UUID.randomUUID();
		
		ConfigurationFMSDB config = getConfiguration(entity);
		
		File converted = convertFileToHubFormat(entity, getConfiguration(entity), config.getSndLineSeparator(),config.getSndCodePage());
		// non deve prendere la dimensione del file crittografato
		outboundDTO.setFileSize(converted.length());
		
		FilePart attch = buildAttachmentFile(entity, fileKey, converted);
		outboundDTO.setFileName(attch.getFileName());
		
		// se non è binario, sun CCR deve sempre arrivare un file ASCII con LineSeparator.CRLF_0X0D0A, 
		// il metodo getAttachmentFile converte EBCDIC in ASCI
		if(config.getSndCodePage() != CodePage.BINARY) {
			outboundDTO.setLineSeparator(LineSeparator.CRLF_0X0D0A.name());
			outboundDTO.setCodePage(CodePage.ASCII.name());
		}
		
		// TBDelete
//		FilePart attch = getAttachmentFile(entity, fileKey, config, config.getSndLineSeparator(),config.getSndCodePage());
//		
//		// non deve prendere la dimensione del file cryttografato
//		//outboundDTO.setFileSize(Files.size(attch.getFile().toPath()));
//		outboundDTO.setFileSize(entity.getFileSize());
//		outboundDTO.setFileName(FilenameUtils.getName(entity.getFileName()));
//		
//		if (!outboundDTO.getCodePage().equals(CodePage.BINARY.name()))
//			outboundDTO.setCodePage(CodePage.ASCII.name());
//		if(outboundDTO.getCodePage().equals(CodePage.ASCII.name())) {
//			outboundDTO.setLineSeparator(LineSeparator.CRLF_0X0D0A.name());
//		}
		
		return Arrays.asList(getAttachmentMessage(entity, messageKey), 
				attch, 
				getMessageWrapper(entity, ServiceType.FMS, outboundDTO, fileKey, messageKey));
	}

	@Override
	protected String getCCRInboundControllerPath() {
		return InboundControllerPath.FMS_MESSAGE;
	}


	@Override
	protected void onSuccess(FMSSend entity) {
		LocalDateTime now = LocalDateTime.now();
		entity.setStatusCodeBA(FMSSendStatus.SENDING.getStCodeBA());
		entity.setStatusCodeSync(FMSSendStatus.SENDING.getStCodeSync());
		entity.setStatus(FMSSendStatus.SENDING);
		entity.setFtsCompleteTime(now);
		entity.setFerSubLstFtsTmp(now);
		entity.setFenSubMssTmp(now);
		entity.setSendConfirmedTimestamp(now);
	}

	@Override
	protected void onError(FMSSend entity, Exception ex) {
		if (ex instanceof ChcException) {
			processException(((ChcException) ex).getCode(), entity, ex.toString());
		} else {
			commonRequestRejected(entity);
			if(entity.getStatusInfo() == null)
				entity.setStatusInfo(ex.toString());
		}
		CswLog.getLogData().setUdr(entity.getUdr());
		CswLog.getLogData().setVfn(entity.getVfn());
		entity.setSendErrorTimestamp(LocalDateTime.now());
		setCompleteForError(entity, ex);
	}
	
	@Override
	protected File validateAndEncryptBlob(FMSSend fmsSend, UUID messageKey) throws ChcException, ChcStubException {
		return super.validateAndEncryptBlob(fmsSend, messageKey, BlobEntity.builder()
				.blobColum("message")
				.tableName("sync_send")
				.keyColum("id_sync_send")
				.key(fmsSend.getId())
				.build(), entityManager);
	}
	
	@Override
	protected void validateBlob(FMSSend fmsSend, File dec) throws ChcException, IOException {
		fmsSend.setMessageLeng(validationService.validateSize(dec.getAbsolutePath(),
				(fmsSend.getMessageLeng() != null) ? Long.valueOf(fmsSend.getMessageLeng()) : null,
				ValidationService.MESSAGE_MAX_SIZE, true).intValue());
		try (FileInputStream fis = new FileInputStream(dec)) {
			fmsSend.setMsgDigest(validationService.validateSha256(fis, fmsSend.getMsgDigest()));
			if (fmsSend.getMsgDigest() != null)
				fmsSend.setMsgDigestAlg(ValidationService.SHA_256);
		}

	}

	private void processException(String code, FMSSend fmsSend, String message) {
		if (code.equals(I18nService.ERR_INVALID_BA.name())
				|| code.equals(I18nService.ERR_MISSING_CONFIGURATION.name())) {
			fmsSend.setStatusCodeBA(FMSSendStatus.INVALID_BA.getStCodeBA());
			fmsSend.setStatusCodeSync(FMSSendStatus.INVALID_BA.getStCodeSync());
			fmsSend.setStatus(FMSSendStatus.INVALID_BA);
			fmsSend.setStatusInfo("BA not configured");
		} else if (code.equals(I18nService.ERR_INVALID_INTERFACE.name())) {
			fmsSend.setStatusCodeBA(FMSSendStatus.INVALID_INTERFACE.getStCodeBA());
			fmsSend.setStatusCodeSync(FMSSendStatus.INVALID_INTERFACE.getStCodeSync());
			fmsSend.setStatus(FMSSendStatus.INVALID_INTERFACE);
			fmsSend.setStatusInfo("Invalid interface");
		} else if (code.equals(I18nCommon.ERR_VALIDATION_EXCEPTION.name()) || 
					code.equals(I18nCommon.ERR_FILE_SIZE_VALIDATION_EXCEPTION.name()) ||
					code.equals(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION.name()) || 
					code.equals(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION.name())) {
			commonRequestRejected(fmsSend);
			fmsSend.setStatusInfo(message);
		} else if (code.equals(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS.name())) {
			commonRequestRejected(fmsSend);
			fmsSend.setStatusInfo("IOException all'accesso a un file");
		} else if (code.equals(I18nCommon.ERR_FILE_NOT_FOUND.name())) {
			commonRequestRejected(fmsSend);
			fmsSend.setStatusInfo("Could not find the file");
		} else if (code.equals(I18nService.CSW_ERR_ZERO_FILE_LENGTH.name())) {
			commonRequestRejected(fmsSend);
			fmsSend.setStatusInfo("File size is 0");
		} else if (code.equals(I18nService.CSW_ERR_FILE_LENGTH_EXCEEDED.name())) {
			commonRequestRejected(fmsSend);
			fmsSend.setStatusInfo("File length exceeded");
		} else if(code.equals(I18nService.ERR_CONVERTING_FILE.name())) {
			commonRequestRejected(fmsSend);
			fmsSend.setStatusInfo("Failed to convert file");
		} else if(code.equals(I18nService.ERR_ENCRYPT.name())) {
			commonRequestRejected(fmsSend);
			fmsSend.setStatusInfo("Failed to encrypt");
		} else{
			commonRequestRejected(fmsSend);
			fmsSend.setStatusInfo(String.format("system error code:%s message:%s",code, message));
		}

	}

	private void commonRequestRejected(FMSSend fmsSend) {
		fmsSend.setStatusCodeBA(FMSSendStatus.REQUEST_SEND_REJECTED.getStCodeBA());
		fmsSend.setStatusCodeSync(FMSSendStatus.REQUEST_SEND_REJECTED.getStCodeSync());
		fmsSend.setStatus(FMSSendStatus.REQUEST_SEND_REJECTED);
	}
	
	// moved to onSuccess
//	@Override
//	protected void insertCompleteTimestamps(FMSSend entity, LocalDateTime completeTms) {
//		entity.setSendCompletedTimestamp(completeTms);
//		entity.setFerSubLstFtsTmp(completeTms);
//		entity.setSendConfirmedTimestamp(completeTms);
//	}

}
