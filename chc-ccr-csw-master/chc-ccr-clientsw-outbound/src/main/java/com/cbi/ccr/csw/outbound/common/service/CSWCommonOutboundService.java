package com.cbi.ccr.csw.outbound.common.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.cbi.ccr.csw.domain.CSWCommonOutboundRepository;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;
import com.cbi.ccr.csw.domain.i.CswEntityOutWithFile;
import com.cbi.ccr.csw.domain.i.CswOutboundEntity;
import com.cbi.ccr.csw.domain.i.HasFile;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.CSWCommonService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.JweJwtService;
import com.cbi.ccr.csw.service.common.ValidationService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.exception.ChcSystemException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.logging.LogLevel;
import com.cbi.frw.common.util.FileUtils;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtilsProxy;
import com.cbi.frw.http.JsonPart;
import com.cbi.frw.http.stream.LongProcessingResponseDTO;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class CSWCommonOutboundService<E extends CswOutboundEntity, R extends CSWCommonOutboundRepository<E, Long>, D extends ClientMessageDTO>
		extends CSWCommonService {
	
	@Value("${ccr_host_batch}")
	protected String ccrHost;

	@Value("${local_temp_folder_encrypted}")
	protected String localTempFolderEncrypted;
	
	@Value("${conversion-folder}")
	protected String localConversionFolder;

	@Value("${ccr_http_timeout}")
	protected Integer ccrHttpTimeout;

	@Value("${integration_test_active}")
	protected Boolean integrationTestActive;

	@Value("${max_retry_attempts}")
	protected int maxRetryAttempts;

	@NonNull
	protected R dbRepository;

	@NonNull
	protected Class<E> entityClass;
	
	@Autowired
	private HttpUtilsProxy httpUtilsProxy;
	
	@Autowired
	private JweJwtService jweJwtService;
	
	@Autowired
	private ApplicationContext appContext;

	protected abstract String getCCRInboundControllerPath();

	protected static final String LOG_MSG = "%s %s id:%s localBA:%s remoteBA:%s";

	protected abstract File validateAndEncryptBlob(E entity, UUID messageKey) throws ChcException, ChcStubException;

		
	public abstract ValidationService getValidationService();
	
	@Override
	@PostConstruct
	public void init() {
		
		try {
			new URL(ccrHost).toURI();
		} catch (MalformedURLException | URISyntaxException e) {
			log.error("CCR HOST not valid:{}", ccrHost);
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}
		
		super.init();
		try {
			FileUtils.createFolders(localTempFolderEncrypted);
			FileUtils.createFolders(localConversionFolder);
		} catch (IOException e) {
			throw new ChcSystemException(e.getMessage());
		}
	}

	protected void log(E entity, String message, Exception e) {
		if (e instanceof ChcStubException || e instanceof ChcException || e instanceof ChcRollbackException) {
			CswLog.error(log, String.format("%s %s id:%s localBA:%s remoteBA:%s err:%s", getType(entity), message, entity.getId(),
					entity.getLocalBaId(), entity.getRemoteBaId(), e.toString()));
		} else {
			CswLog.error(log, String.format(LOG_MSG, getType(entity), message, entity.getId(), entity.getLocalBaId(), entity.getRemoteBaId(), e));
		}

	}

	protected void log(E entity, String message, LogLevel level) {
		switch (level) {
		case DEBUG:
			CswLog.debug(log, String.format(LOG_MSG, getType(entity), message, entity.getId(), entity.getLocalBaId(), entity.getRemoteBaId()));
			break;
		case INFO:
			CswLog.info(log, String.format(LOG_MSG, getType(entity), message, entity.getId(), entity.getLocalBaId(), entity.getRemoteBaId()));
			break;
		default:
			break;
		}
	}

	private String getType(E entity) {
		return entity.getClass().getSimpleName();
	}

	/**
	 * CHC-82 Tutti i servizi - Campo COMPLETE = 1 quando c'è un errore permanente
	 * (ad esclusione dei errore INVALID_BA e INVALID_INTERFACE)
	 */
	protected void setCompleteForError(E entity, Exception e) {
		if(!(e instanceof ChcException)) {
			entity.setComplete(1);
		} else {
			ChcException chcException = (ChcException) e;
			if (!chcException.getCode().equals(I18nService.ERR_INVALID_BA.name()) && !chcException.getCode().equals(I18nService.ERR_INVALID_INTERFACE.name())) {
				entity.setComplete(1);
			} else {
				entity.setComplete(0);
			}
		}
	}
	
	/**
	 * CHC-475 Tutti i servizi MQ - Campo COMPLETE = 1 quando c'è un errore permanente
	 */
	protected void setCompleteForErrorMQ(E entity, Exception e) {
		if(!(e instanceof ChcException)) {
			entity.setComplete(1);
		} else {
			entity.setComplete(1);
		}
	}

	/**
	 * 
	 * @param entity
	 * @param attachments
	 * @param endpoint
	 * @return
	 * @throws ChcStubException 
	 * @throws ChcException 
	 */
	protected LongProcessingResponseDTO sendToHub2(@NonNull E entity, @NonNull List<AbstractPart> attachments, @NonNull String endpoint) throws ChcStubException {
		
		Map<String, String> headersToSend = new HashMap<>();
		if (jweJwtService.isSecurityEnabled()) { 
			headersToSend.put(JweJwtService.PROP_TOKEN_HEADER, jweJwtService.getJwt());
		}
		
		HttpResponse<LongProcessingResponseDTO> resp = httpUtilsProxy.postRequestMultiPart(
				String.format("%s%s/%s", ccrHost, InboundControllerPath.BASE + endpoint, entity.getId()), headersToSend,
				null, attachments, LongProcessingResponseDTO.class, ccrHttpTimeout, true);
		
		/**
		 * non fare altre operazioni in questo punto 
		 */
		return resp.getResponse();
	}
	
	// overwriten for MQ, must not be executed
	protected void validateFileMD5(CswEntityOutWithFile fmsSend, FileInputStream is1) throws ChcException {
		fmsSend.setFileMD5(getValidationService().validateMD5(is1, fmsSend.getFileMD5()));
	}
	
	// MEK MEK
	protected void validateFileDigestAndSize(CswEntityOutWithFile fmsSend, ConfigurationFMSFTSCommon conf) throws ChcException {
		try (FileInputStream is1 = new FileInputStream(fmsSend.getFileName())) {
			if (fmsSend.getFileDigestAlg() == null & fmsSend.getFileMD5()!=null)
				fmsSend.setFileMD5(getValidationService().validateMD5(is1, fmsSend.getFileMD5()));
			else {

				fmsSend.setFileDigest(getValidationService().validateSha256(is1, fmsSend.getFileDigest()));
				if (fmsSend.getFileDigest() != null) {
					fmsSend.setFileDigestAlg(ValidationService.SHA_256);

					//if (fmsSend instanceof CswEntityOutMqWithFile)
					if(fmsSend.getFileDigest() != null)
						fmsSend.setFileDigestLen(Long.valueOf(fmsSend.getFileDigest().length()));
				}
			}
			fmsSend.setFileSize(getValidationService().validateSize(fmsSend.getFileName(), fmsSend.getFileSize(),
					ValidationService.FILE_MAX_SIZE, false));

			

		} catch (FileNotFoundException e) {
			CswLog.error(log, String.format("File Not Found: %s", fmsSend.getFileName()));
			throw new ChcException(I18nCommon.ERR_FILE_NOT_FOUND);
		} catch (IOException e) {
			CswLog.error(log, String.format("error copying file from path %s %s", fmsSend.getFileName(), e.toString()));
			throw new ChcException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS);
		}

	}

	protected File encryptFile(File file, UUID fileId) throws ChcException, ChcStubException {
		try (FileInputStream is2 = new FileInputStream(file.getPath())) {

			if (Files.size(Paths.get(file.getPath())) > file.getParentFile().getFreeSpace()) {
				CswLog.error(log, "no disk space is available");
				throw new ChcException(I18nCommon.ERR_NO_DISKSPACE_AVAILABLE);
			}

			if (Boolean.TRUE.equals(integrationTestActive) && file.getName().indexOf("IO_EXCEPTION") != -1) {
				// simulate IO_EXCEPTION
				localTempFolderEncrypted = "/zaza"; // non existing folder
			}
			File enc = new File(localTempFolderEncrypted,
					String.format("%s_file_enc", file.getName()));
			encryptionUtil.encryptFile(is2, enc, getEncryptionServiceKey(fileId));
			return enc;
		} catch (IOException e) {
			CswLog.error(log, String.format("error copying file from path %s %s", file.getName(), e.toString()));
			throw new ChcException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS);
		}

	}

	protected String createNetMsgId(String localBa, Long seqId) {
		StringBuilder sb = new StringBuilder();
		return sb.append(localBa.substring(0, 5)).append(seqId).toString();
	}


	protected <C extends ConfigurationFMSFTSCommon> FilePart buildAttachmentFile(E entity, UUID fileKey, File filetoSend)
			throws ChcException, ChcStubException {
		
		File encryptFile = encryptFile(filetoSend, fileKey);
		String originalFileName = FilenameUtils.getName(((HasFile) entity).getFileName());
		return new FilePart("file", encryptFile, originalFileName);
	}

	protected FilePart getAttachmentMessage(E entity, UUID messageKey) throws ChcException, ChcStubException {
		File encryptedMessage = validateAndEncryptBlob(entity, messageKey);
		return new FilePart("message", encryptedMessage, "message.bin");
	}

	protected JsonPart getMessageWrapper(E entity, ServiceType serviceType, D messageDTO, UUID fileKey, UUID messageKey)
			throws ChcException, ChcStubException {
		UUID wrapperKey = UUID.randomUUID();
		
		String encryptedMsgBody = encryptionUtil.encrypt(JSON.toJson(messageDTO), getEncryptionServiceKey(wrapperKey));
		
		MessageWrapperDTO wrapper = new MessageWrapperDTO();
		wrapper.setEncryptedBody(encryptedMsgBody);
		wrapper.setType(serviceType);
		wrapper.setWrapperKey(wrapperKey);
		wrapper.setFileId(fileKey);
		wrapper.setMessageId(messageKey);
		return new JsonPart("wrapper", wrapper);
	}	

	protected abstract void validateBlob(E entity, File dec) throws ChcException, IOException;
	
	protected File validateAndEncryptBlob(E entity, UUID messageKey, BlobEntity blobEntity, EntityManager entityManager) throws ChcException, ChcStubException {
		File enc = new File(localTempFolderEncrypted, String.format("%s_%s_message_enc", entity.getClass().getSimpleName(), entity.getId()));
		File dec = null;
		try {
			dec = new File(localTempFolderEncrypted, String.format("%s_%s_message_dec", entity.getClass().getSimpleName(), entity.getId()));
			BlobHelper.readblobAsFile(blobEntity, entityManager, dec);

			validateBlob(entity, dec);

			try (FileInputStream inpuStreamEncrypt = new FileInputStream(dec)) {
				encryptionUtil.encryptFile(inpuStreamEncrypt, enc, getEncryptionServiceKey(messageKey));
			}
			return enc;
		} catch (ChcException | ChcStubException e) {
			throw e;
		} catch (IOException e) {
			CswLog.getLogData().setFunction("validateAndEncryptBlob");
			CswLog.error(log, String.format("error encrypting blob: %s", e.toString()));
			CswLog.getLogData().setFunction(null);
			throw new ChcException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS, e.toString());
		} catch (Exception e) {
			CswLog.getLogData().setFunction("validateAndEncryptBlob");
			CswLog.error(log, String.format("generic error encrypting blob: %s", e.toString()));
			CswLog.getLogData().setFunction(null);
			throw new ChcException(I18nCommon.ERR_GENERIC, e.toString());
		} finally {
			try {
				if (dec != null)
					Files.delete(dec.toPath());
			} catch (IOException e) {
				// ignored
			}
		}
	}

}
