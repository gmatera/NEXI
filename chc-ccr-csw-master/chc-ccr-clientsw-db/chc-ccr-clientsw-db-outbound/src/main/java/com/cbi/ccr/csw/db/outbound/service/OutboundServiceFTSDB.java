package com.cbi.ccr.csw.db.outbound.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.csw.config.AddOnConfigurationFTSRepository;
import com.cbi.ccr.csw.domain.csw.config.AddOnFTSConfiguration;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFTSDB;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.fts.FTSSendDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSSendStatus;
import com.cbi.ccr.csw.dto.SubmitDTO;
import com.cbi.ccr.csw.dto.fms.FTSMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundServiceFTSDB extends CSWCommonOutboundServiceDB<FTSSend, FTSSendDBRepository, FTSMessageDTO>{

	@Autowired
	private AddOnConfigurationFTSRepository addOnConfigurationRepository;
	
	@Autowired
	private FileFileRecordFormatValidatorDB fileFileRecordFormatValidator;
	
	public OutboundServiceFTSDB(@NonNull FTSSendDBRepository dbRepository) {
		super(dbRepository, FTSSend.class);
	}
	
	protected String getCCRInboundControllerPath() {
		return InboundControllerPath.FTS_MESSAGE;
	}
	
	@Override
	protected void takeIt(@NonNull SubmitDTO dto, FTSSend entity) throws ChcException, IOException {
		LocalDateTime now = LocalDateTime.now();
		entity.setStatus(FTSSendStatus.FILE_TO_BE_PROCESSED);
		entity.setStsCode(FTSSendStatus.FILE_TO_BE_PROCESSED.getStsCode());
		entity.setRefDate(format4(now));
		entity.setBaInsertTimestamp(now);
	}

	@Override
	protected FTSMessageDTO performValidations(@NonNull SubmitDTO dto, FTSSend entity) throws ChcException, IOException {
		// FTS and ADDON 
		
		ServiceType serviceType = ServiceType.FTS;
		if(entity.getFtsInterface().equals(RouteInterface.FS))
			serviceType = ServiceType.AON;
		
		ConfigurationFTSDB conf = getConfiguration(entity);
		CswLog.getLogData().setFunction("performValidations");
		CswLog.getLogData().setVfn(entity.getVfn());
		if(entity.getFtsInterface() == RouteInterface.FS)
			CswLog.getLogData().setService(CswLogData.ServiceEnum.AON.name());
		CswLog.getLogData().setService(CswLogData.ServiceEnum.FTS.name());
		CswLog.debug(log, String.format("loadFTSConfiguration DONE %s",entity));
		
		validationService.validateBaAndInterface(entity.getLocalBaId(), entity.getRemoteBaId(),
				serviceType, RouteInterface.valueOf(entity.getFtsInterface().name()));
		
		CswLog.debug(log, String.format("validateBaAndInterface DONE %s",entity));
		
		validationService.validateFTSRegex(entity);
		
		CswLog.debug(log, String.format("validateFTSRegex DONE %s", entity));
		
		StringBuilder sb = new StringBuilder();
		sb.append(entity.getLocalBaId()).append(entity.getRemoteBaId()).append(entity.getVfn())
		.append(entity.getFileName()).append(entity.getFileDigestAlg())
		.append(entity.getFileDigest()).append(entity.getApplicativeDataField());
		
		lauService.checkLAU(entity.getLocalAuthInfo(), entity.getLocalAuthInfoAlg(), conf.getLauEnabled(), conf.getLauKey(), sb.toString());
		
		//only for ADDON
		if (entity.getFtsInterface() == RouteInterface.FS) {
			String orignalFileName = entity.getFileName();
			addPrefixFilenameInEntity(entity);
			validateFileDigestAndSize(entity, conf);
			// per DB la validazione usa la config
			fileFileRecordFormatValidator.processFileValidation(conf, entity.getFileName());
			entity.setFileName(orignalFileName);
			
		} else {			
			validateFileDigestAndSize(entity, conf);
			// per DB la validazione usa la config
			fileFileRecordFormatValidator.processFileValidation(conf, entity.getFileName());
		}
		
		FTSMessageDTO ftsMessage = mapper.map(entity, FTSMessageDTO.class);
		// usato per indentificare le notifiche dal CCR
		ftsMessage.setClientSwMessageId(entity.getId());
		ftsMessage.setFileName(new File(entity.getFileName()).getName());

		ftsMessage.setCodePage(conf.getSndCodePage().name());
		if (!ftsMessage.getCodePage().equals(CodePage.BINARY.name())) {
			ftsMessage.setRecordFormat(conf.getSndRecordFormat().name());
			ftsMessage.setLineSeparator(conf.getSndLineSeparator().name());
			ftsMessage.setMaxRecordLength(conf.getSndMaxRecLength());
		}
		ftsMessage.setUdr(entity.getApplicativeDataField());
		return ftsMessage;
		
	}

	@Override
	protected List<AbstractPart> beforeSending(@NonNull SubmitDTO dto, FTSSend entity, FTSMessageDTO outboundDTO) throws ChcException, ChcStubException {
		entity.setStatus(FTSSendStatus.GFT_SENDING);
		entity.setStsCode(FTSSendStatus.GFT_SENDING.getStsCode());
		
		LocalDateTime now = LocalDateTime.now();
		entity.setCreateDate(format3(now));
		entity.setStartTime(format(now));
		entity.setSendAcceptedTimestamp(now);
		
		CswLog.getLogData().setFunction("beforeSending");
		CswLog.debug(log, String.format("DB update CreateDate and StartTime DONE %s",entity));
		
//		entity.setSendGftRequestTimestamp(LocalDateTime.now()); NON VALORIZABILE QUI
		
		String originalFileName = entity.getFileName();
		//only for ADDON
		if (entity.getFtsInterface() == RouteInterface.FS) {
			addPrefixFilenameInEntity(entity);
		}
		
		UUID fileKey = UUID.randomUUID();
		
		ConfigurationFTSDB config = getConfiguration(entity);
		
		File converted = convertFileToHubFormat(entity, getConfiguration(entity), config.getSndLineSeparator(),config.getSndCodePage());
		// non deve prendere la dimensione del file crittografato
		outboundDTO.setFileSize(converted.length());
		
		FilePart attch = buildAttachmentFile(entity, fileKey, converted);
		outboundDTO.setFileName(originalFileName);
		
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
//		outboundDTO.setFileName(FilenameUtils.getName(originalFileName));
//
//		
//		if(outboundDTO.getCodePage().equals(CodePage.ASCII.name())) {
//			outboundDTO.setLineSeparator(LineSeparator.CRLF_0X0D0A.name());
//		}
		
		List<AbstractPart> parts = Arrays.asList(attch, (entity.getFtsInterface() == RouteInterface.DB)
				? getMessageWrapper(entity, ServiceType.FTS, outboundDTO, fileKey, null) : getMessageWrapper(entity, ServiceType.AON, outboundDTO, fileKey, null));
		
		entity.setFileName(originalFileName);

		return parts;
	}
	
	private void addPrefixFilenameInEntity(FTSSend entity) {
		
		AddOnFTSConfiguration confAddon = addOnConfigurationRepository
				.findByLocalBaIdAndRemoteBaId(entity.getLocalBaId(), entity.getRemoteBaId());
		
		String prefix = "";
		prefix = confAddon.getSendingPrefix();
		if(entity.getFileName().contains(prefix)) {
			return;
		}
		entity.setFileName(File.separator +
				FilenameUtils.getPath(entity.getFileName()) +
				prefix + 
				FilenameUtils.getName(entity.getFileName()));
	}

	private ConfigurationFTSDB getConfiguration(FTSSend ftsSend) throws ChcException {
		ConfigurationFTSDB conf;
		if (ftsSend.getFtsInterface() == RouteInterface.DB) {
			// FTS
			conf = configurationService.loadFTSConfiguration(ftsSend.getLocalBaId(), ftsSend.getRemoteBaId());
		}else {
			// ADD-ON only binary
			conf = new ConfigurationFTSDB();
			conf.setSndCodePage(CodePage.BINARY);
			conf.setLauEnabled(false);
		}
		return conf;
	}

	@Override
	protected void onSuccess(FTSSend entity) {
		entity.setStatus(FTSSendStatus.EXPORT_REQUEST);
		entity.setStsCode(FTSSendStatus.EXPORT_REQUEST.getStsCode());
		LocalDateTime now = LocalDateTime.now();
		entity.setEasComplTime(format(now));
		entity.setSendGftRequestTimestamp(LocalDateTime.now()); 
	}
	
	@Override
	protected void onError(FTSSend entity, Exception ex) {
		
		if (ex instanceof ChcException) {
//			log.error("validation errors {}", ex.getMessage());
			processException(((ChcException) ex).getCode(), entity, ex.getMessage());
		} else {
			
			if(entity.getStatusInfo() == null)
				entity.setStatusInfo(ex.toString());
			
			entity.setStatus(FTSSendStatus.EXPORT_ERROR);
			entity.setCswStatus(ClientTaskStatus.FAILED);
		}
		CswLog.getLogData().setVfn(entity.getVfn());
		entity.setSendErrorTimestamp(LocalDateTime.now());

		try {
			
			if (entity.getFtsInterface() == RouteInterface.FS) {
				AddOnFTSConfiguration confAddon = null;
				confAddon = addOnConfigurationRepository.findByLocalBaIdAndRemoteBaId(entity.getLocalBaId(), entity.getRemoteBaId());
				
				String fileName =  FilenameUtils.getName(entity.getFileName()).replace(confAddon.getSendingPrefix(), "");
				Path source = Paths.get(File.separator + FilenameUtils.getPath(entity.getFileName()) + FilenameUtils.getName(entity.getFileName()));
				
				if(!Files.exists(source)) {
					// CHC-26, ChcStubException, il entity.getFileName() non contiene il prefisso
					source = Paths.get(File.separator, FilenameUtils.getPath(entity.getFileName()) , confAddon.getSendingPrefix() + fileName);
				}
					
				String prefix = confAddon.getErrorDeliverPrefix();
				
				if(ex instanceof ChcException)
					prefix = confAddon.getErrorPrefix();
				
				Path newFile = Paths.get(File.separator + FilenameUtils.getPath(entity.getFileName()) + prefix + fileName);
				
				if(Files.exists(source))
					Files.move(source, newFile);

				// lo rimette come era all'origine
				entity.setFileName(File.separator + FilenameUtils.getPath(entity.getFileName()) + fileName);
			}
			
		} catch (IOException e2) {
			CswLog.getLogData().setFunction("onError");
			CswLog.error(log, e2.getLocalizedMessage());
		}
		
		setCompleteForError(entity, ex);
	}



	private void processException(String code, FTSSend ftsSend, String message) {
		if (code.equals(I18nService.ERR_INVALID_BA.name()) ||
				code.equals(I18nService.ERR_MISSING_CONFIGURATION.name())) {
			ftsSend.setStatus(FTSSendStatus.INVALID_BA);
			ftsSend.setStsCode(FTSSendStatus.INVALID_BA.getStsCode());
			ftsSend.setStatusInfo("BA not configured");
		}else if(code.equals(I18nService.ERR_INVALID_INTERFACE.name())) {
			ftsSend.setStatus(FTSSendStatus.INVALID_INTERFACE);
			ftsSend.setStsCode(FTSSendStatus.INVALID_INTERFACE.getStsCode());
			ftsSend.setStatusInfo("Invalid interface");
		}else if (code.equals(I18nCommon.ERR_VALIDATION_EXCEPTION.name()) ||
				code.equals(I18nCommon.ERR_FILE_SIZE_VALIDATION_EXCEPTION.name()) ||
			    code.equals(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION.name())) {
			ftsSend.setStatus(FTSSendStatus.MARSHALL_ERROR);
			ftsSend.setStsCode(FTSSendStatus.MARSHALL_ERROR.getStsCode());
			ftsSend.setStatusInfo(String.format("Marshall error: %s", message));
		} else if (code.equals(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION.name())) {
			ftsSend.setStatus(FTSSendStatus.CREATE_ERROR);
			ftsSend.setStsCode(FTSSendStatus.CREATE_ERROR.getStsCode());
			ftsSend.setStatusInfo(String.format("Create error: %s", message));
		} else if (code.equals(I18nService.CSW_ERR_ZERO_FILE_LENGTH.name())) {
			ftsSend.setStatus(FTSSendStatus.FILE_LOAD_EMPTY);
			ftsSend.setStsCode(FTSSendStatus.FILE_LOAD_EMPTY.getStsCode());
			ftsSend.setStatusInfo("File has zero length");
		}else if (code.equals(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS.name())
				|| code.equals(I18nCommon.ERR_FILE_NOT_FOUND.name())) {
			ftsSend.setStatus(FTSSendStatus.FILE_LOAD_ERROR);
			ftsSend.setStsCode(FTSSendStatus.FILE_LOAD_ERROR.getStsCode());
			ftsSend.setStatusInfo("Could not find the file");
		} else if(code.equals(I18nService.ERR_CONVERTING_FILE.name())) {
			ftsSend.setStatus(FTSSendStatus.FILE_LOAD_ERROR);
			ftsSend.setStsCode(FTSSendStatus.FILE_LOAD_ERROR.getStsCode());
			ftsSend.setStatusInfo("Failed to convert file");
		} else if(code.equals(I18nService.ERR_ENCRYPT.name())) {
			ftsSend.setStatus(FTSSendStatus.FILE_LOAD_ERROR);
			ftsSend.setStsCode(FTSSendStatus.FILE_LOAD_ERROR.getStsCode());
			ftsSend.setStatusInfo("Failed to encrypt");
		} else {
			ftsSend.setStatus(FTSSendStatus.FILE_LOAD_ERROR);
			ftsSend.setStsCode(FTSSendStatus.FILE_LOAD_ERROR.getStsCode());
			ftsSend.setStatusInfo("Generic error" + message);
		}
	}

	@Override
	protected File validateAndEncryptBlob(FTSSend entity, UUID messageKey) throws ChcException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void validateBlob(FTSSend entity, File dec) throws ChcException, IOException {
		throw new UnsupportedOperationException();
	}

	// is already in onSuccess
//	@Override
//	protected void insertCompleteTimestamps(FTSSend entity, LocalDateTime completeTms) {
//		entity.setSendCompletedTimestamp(completeTms);
//		entity.setEasComplTime(completeTms.toString());
//		entity.setSendConfirmedTimestamp(completeTms);
//	}

}
