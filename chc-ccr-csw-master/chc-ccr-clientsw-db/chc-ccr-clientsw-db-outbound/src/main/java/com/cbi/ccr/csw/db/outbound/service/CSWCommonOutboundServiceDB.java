package com.cbi.ccr.csw.db.outbound.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.cbi.ccr.csw.db.common.CommonConfigurationServiceDB;
import com.cbi.ccr.csw.db.common.ValidationServiceDB;
import com.cbi.ccr.csw.domain.CSWCommonRepositoryDB;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.domain.i.CswEntityOutWithFile;
import com.cbi.ccr.csw.domain.i.CswOutboundEntity;
import com.cbi.ccr.csw.domain.i.HasFile;
import com.cbi.ccr.csw.dto.SubmitDTO;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.outbound.common.service.CSWCommonOutboundService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.Msg;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.logging.LogLevel;
import com.cbi.frw.common.util.FileConversionUtils;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.http.stream.LongProcessingResponseDTO;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CSWCommonOutboundServiceDB<E extends CswOutboundEntity, R extends CSWCommonRepositoryDB<E, Long>, D extends ClientMessageDTO>
		extends CSWCommonOutboundService<E, R, D> {

	protected CSWCommonOutboundServiceDB(@NonNull R dbRepository, @NonNull Class<E> entityClass) {
		super(dbRepository, entityClass);
	}
	
	protected abstract void onSuccess(E entity);

	protected abstract void onError(E entity, Exception ex);
	
	protected abstract void takeIt(@NonNull SubmitDTO dto, E entity) throws ChcException, IOException;
	protected abstract D performValidations(@NonNull SubmitDTO dto, E entity) throws ChcException, IOException;
	//protected abstract void insertCompleteTimestamps(E entity, LocalDateTime completeTms);
	protected abstract List<AbstractPart> beforeSending(@NonNull SubmitDTO dto, E entity, D outboundDTO) throws ChcException, ChcStubException;
//	protected abstract <C extends ConfigurationFMSFTSCommon> boolean canConvertFileToEBCDIC(C conf, E entity) throws ChcException;
	
	@Getter
	@Autowired
	protected CommonConfigurationServiceDB configurationService;
	
	@Getter
	@Autowired
	protected ValidationServiceDB validationService;
	
	protected void handleError(E entity, Exception e) {
		
		CswLog.getLogData().setFunction("handleError");
		CswLog.error(log, e.getLocalizedMessage());
		entity.setCswStatus(ClientTaskStatus.FAILED);
		onError(entity, e);
		transactionTemplate.executeWithoutResult(t -> dbRepository.save(entity));

	}
	
//	@Override
	protected <C extends ConfigurationFMSFTSCommon> boolean canConvertFileToEBCDIC(C conf, E entity)
			throws ChcException {
		
		if (conf.getSndCodePage().equals(CodePage.EBCDIC))
			return true;
		
		return false;
	}
	
	protected E checkMessage(@NonNull SubmitDTO dto) {
		Optional<E> fmsSendOpt = dbRepository.findById(dto.getId());

		// non si devono lanciare eccezioni verso il poller
		if (!fmsSendOpt.isPresent()) {
			CswLog.getLogData().setFunction("checkMessage");
			CswLog.getLogData().setLocalBaId(fmsSendOpt.get().getLocalBaId());
			CswLog.getLogData().setRemoteBaId(fmsSendOpt.get().getRemoteBaId());
			CswLog.getLogData().setId(fmsSendOpt.get().getId().toString());
			CswLog.error(log, Msg.getMessage(I18nService.CSW_MESSAGE_NOT_FOUND.name(),(Object) dto.getId()));
			return null;
		}
		E entity = fmsSendOpt.get();

		// non si devono lanciare eccezioni verso il poller
		if (entity.getCswStatus() != ClientTaskStatus.NEW) {
			CswLog.getLogData().setFunction("checkMessage");
			CswLog.getLogData().setLocalBaId(fmsSendOpt.get().getLocalBaId());
			CswLog.getLogData().setRemoteBaId(fmsSendOpt.get().getRemoteBaId());
			CswLog.getLogData().setId(fmsSendOpt.get().getId().toString());	
			CswLog.error(log, Msg.getMessage(I18nService.CSW_WRONG_STATUS.name(),entity.getCswStatus()));
			return null;
		}
		return entity;
	}
	
	public void submitToHub(@NonNull SubmitDTO dto) {
		E entity = checkMessage(dto);
		if (entity == null)
			return;

		CswLog.getLogData().setFunction(String.format("submitToHub%s", entity.getRetryCounter() > 1 ? "-retry" : ""));
		CswLog.getLogData().setLocalBaId(entity.getLocalBaId());
		CswLog.getLogData().setRemoteBaId(entity.getRemoteBaId());
		CswLog.getLogData().setId(entity.getId().toString());
		CswLog.getLogData().setRetryCount(entity.getRetryCounter());
		CswLog.info(log, "Start submiting to Hub");
		
		List<AbstractPart> parts = null;
		try {
			takeIt(dto, entity);
			entity.setStatusInfo(null); // reset nel caso fosse un retry
			entity.setCswStatus(ClientTaskStatus.SENDING);
			entity = genericDAO.update(entity);
			
			D outboundDto = performValidations(dto, entity);
			
			CswLog.info(log, "perform validation DONE");
			
			String fileName = null;
			if(entity instanceof HasFile)
				fileName = ((HasFile) entity).getFileName();
			
			parts = beforeSending(dto, entity, outboundDto);
			
			CswLog.info(log, "conversion file DONE");
			entity = genericDAO.update(entity);
			
			submitToHubInternal(entity, parts, fileName);
			
		} catch (ChcStubException e) {
			handleStuException(entity, e, Msg.getMessage(I18nService.ERR_INVOKING_CRYPTO_HUB.name()));
		} catch (Exception e) {
			handleError(entity, e);

		} finally {
			if (parts != null) {
				parts.forEach(p -> {
					if (p instanceof FilePart) {
						try {
							Files.delete(((FilePart) p).getFile().toPath());
						} catch (IOException e) {
							// ignored
						}
					}
				});
			}
		}
	}
	
	private void submitToHubInternal (E entity, List<AbstractPart> parts, String fileName) throws ChcStubException {
		try {
			LongProcessingResponseDTO resp = sendToHub2(entity, parts, getCCRInboundControllerPath());
		
			if(resp.getError() == null) {

				entity.setCswStatus(ClientTaskStatus.ON_HUB);
				onSuccess(entity);
				
				if(fileName != null && !((HasFile) entity).getFileName().equals(fileName)) {
					((HasFile) entity).setFileName(fileName);
				}
				genericDAO.update(entity);
				
				CswLog.info(log, String.format("DB update CswStatus to ON_HUB DONE. Id: %s", entity.getId()));

			}else{
				// se il CCR va in errore HttpStatus è 200, e la response contiene il vero http status
				if(resp.getError().isRetry()) {
					handleStuException(entity, new ChcStubException(resp.getError()), Msg.getMessage(I18nService.ERR_INVOKING_HUB.name()));	
				} else {
					handleStuException(entity, new ChcStubException(resp.getError()), Msg.getMessage(I18nService.ERROR_ON_HUB.name()));
				}
			}
		
		} catch (ChcStubException e) {
			// se  HttpStatus >= 300, sono tutti errori client side
			// quindi forziamo 503 per il retry
			e.getError().setHttpStatus(503);
			handleStuException(entity, new ChcStubException(e.getError()), Msg.getMessage(I18nService.ERR_INVOKING_HUB.name()));
		}
		
	}

	protected E handleStuException(@NonNull final E entity, ChcStubException e, String statusInfo) {
		
		CswLog.getLogData().setFunction("handleStuException");
		CswLog.getLogData().setRetryCount(entity.getRetryCounter());
		CswLog.error(log, String.format("error while sending to HUB: %s", e.getLocalizedMessage()));

		return transactionTemplate.execute(t -> {
			if (entity.getCswStatus() == ClientTaskStatus.SENDING) {
				// CHC-26
				entity.setStatusInfo(statusInfo);
				if (e.isRetry() && entity.getRetryCounter() < maxRetryAttempts) {
					entity.setCswStatus(ClientTaskStatus.WAITING_FOR_RETRY);
					//entity.setStatusInfo(Msg.getMessage(I18nService.ERR_INVOKING_HUB.name()));
				} else {
					entity.setCswStatus(ClientTaskStatus.FAILED);
					//entity.setStatusInfo(Msg.getMessage(I18nService.ERR_INVOKING_HUB.name()));
					onError(entity, e);
				}
				CswLog.debug(log, String.format("update CswStatus to %s", entity.getCswStatus()));
				return dbRepository.save(entity);
			} else {
				// notification has been received
				CswLog.debug(log, "The record was updated by another transaction, In this particular case is safe to continue.");
			}
			return entity;
		});

	}
	
	protected <C extends ConfigurationFMSFTSCommon> File convertFileToHubFormat(E entity, C config, LineSeparator lineSeparator, CodePage codepage)
			throws ChcException {
		
		File fileAsciiOrBinary = convertFileifEBCDIC(config, entity);
		File filetoSend;
		
		if(codepage!=CodePage.BINARY) {
			filetoSend = convertLineSeparatorToCRLF(config, fileAsciiOrBinary, entity, lineSeparator);
		}else {
			 filetoSend = fileAsciiOrBinary;
		}
		return filetoSend;
	}
	
	protected <C extends ConfigurationFMSFTSCommon> File convertFileifEBCDIC(C conf, E entity) throws ChcException {

		if (!(entity instanceof CswEntityOutWithFile))
			return null;

		if (!canConvertFileToEBCDIC(conf, entity))
			return new File(((CswEntityOutWithFile) entity).getFileName());

		CswEntityOutWithFile entityWithFile = (CswEntityOutWithFile) entity;
		File ebcdic = new File(entityWithFile.getFileName());
		File ascii = new File(localConversionFolder + File.separator + "converted-to-ascii-" + UUID.randomUUID().toString());
		
		CswLog.getLogData().setFunction("convertFileifEBCDIC");
		CswLog.info(log, String.format("Converting EBCDIC (%s) file to ASCII (%s) before sending...", entityWithFile.getFileName(), ascii.getAbsolutePath()));

		try {
			FileConversionUtils.convertFileFromEBCDICtoASCII(ebcdic, ascii);
			return ascii;
			//NON MODIFICARE ENTITA
//			entityWithFile.setFileName(ascii.getAbsolutePath());
		} catch (ChcException e) {
			
			CswLog.getLogData().setFunction("convertFileifEBCDIC");
			CswLog.error(log, e.getLocalizedMessage());
			
			throw new ChcException(I18nService.ERR_CONVERTING_FILE, e.getLocalizedMessage());
		}
	}

	protected <C extends ConfigurationFMSFTSCommon> File convertLineSeparatorToCRLF(C config,File file, E entity, LineSeparator lineSeparator)
			throws ChcException {

		if (!(entity instanceof HasFile))
			return null;


		if (lineSeparator == null)
			throw new ChcException(I18nService.ERR_CONVERTING_FILE, "LineSeparator not found for conversion");

		if (!lineSeparator.equals(LineSeparator.LF_0X0A) && !lineSeparator.equals(LineSeparator.NONE)) {
			return file;
		}

		File fileConvertedLineSeparator = new File(localConversionFolder + File.separator + "converted-lineSeparator-" + UUID.randomUUID().toString());
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file));
				PrintWriter writer = new PrintWriter(
						new BufferedOutputStream(new FileOutputStream(fileConvertedLineSeparator)));) {

			if (config.getSndRecordFormat().equals(RecordFormat.FIXED) & lineSeparator.equals(LineSeparator.NONE)) {
				int recordLenght = config.getSndMaxRecLength();
				char[] buffer = new char[recordLenght];
				while ((reader.read(buffer)) > 0 ) {
					writer.print(String.format("%s%s",new String(buffer), LineSeparator.CRLF_0X0D0A.getFullHexCode()));
				}

			} else {
				String str;
				while ((str = reader.readLine()) != null) {
					writer.print(String.format("%s%s", str, LineSeparator.CRLF_0X0D0A.getFullHexCode()));
				}
			}
			writer.flush();
			return fileConvertedLineSeparator;
		} catch (IOException e) {
			throw new ChcException(I18nService.ERR_CONVERTING_FILE, e.getLocalizedMessage());
		}
	}
	
}
