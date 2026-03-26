package com.cbi.ccr.csw.mq.outbound.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.cbi.ccr.csw.domain.CSWCommonOutboundRepository;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationCommon;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;
import com.cbi.ccr.csw.dto.SubmitDTO;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.mq.common.dto.PrimitiveMQ;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.service.CommonConfigurationServiceMQ;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveConfigurationLoader;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.common.service.ValidationServiceMQ;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.i.CswEntityMq;
import com.cbi.ccr.csw.mq.domain.i.CswEntityOutMqWithFile;
import com.cbi.ccr.csw.outbound.common.service.CSWCommonOutboundService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.Msg;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.exception.ChcSystemException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;
import com.cbi.frw.common.util.FileUtils;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.http.stream.LongProcessingResponseDTO;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CSWCommonOutboundServiceMQ<E extends CswEntityMq, 
	R extends CSWCommonOutboundRepository<E, Long>, 
	D extends ClientMessageDTO, 
	P extends PrimitiveMQ,
	C extends ConfigurationCommon>
		extends CSWCommonOutboundService<E, R, D> {

	@Value("${local_temp_folder_file_mq}")
	protected String localTempFolderfileMq;

	@Autowired
	protected MqPrimitiveConfigurationLoader mqConf;
	
	@Autowired
	protected CommonConfigurationServiceMQ configurationService;
	
	@Getter
	@Autowired
	protected ValidationServiceMQ validationService;
	@Autowired
	protected MqPrimitiveSenderService mqPrimitiveSenderService;
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	protected abstract List<AbstractPart> createParts(E entity, D outboundDTO) throws ChcStubException, ChcException;
	
	protected abstract E savePrimitiveForRetry(E serializableEntity, P messageDto);
	protected abstract P readOriginalPrimitive(E entity) throws IOException, ClassNotFoundException;

	protected abstract E checkDuplicates(@NonNull P messageDto) throws ChcException;

	protected abstract void performValidations(@NonNull P messageDto, C configuration) throws ChcException;

	protected abstract C getConfiguration(String localBa, String remoteBa) throws ChcException;
	
	protected abstract D buildMessageDTO(@NonNull E entity);

	protected abstract E createEntity(@NonNull P messageDto, SendStatusMQ status, LocalDateTime acceptTimestamp, C configuration);

	protected abstract void handleManagedException(E entity, ChcException e, @NonNull P primitive);
	
	protected abstract void performFileValidation(@NonNull E entity, C configuration) throws ChcException;

	public abstract void submitToHub(@NonNull P primitiveDto) throws ChcUnrecoverableException;
	
	protected abstract void send1402or1941(E entity, String message);
	
	protected CSWCommonOutboundServiceMQ(@NonNull R dbRepository, @NonNull Class<E> entityClass) {
		super(dbRepository, entityClass);
	}

	@Override
	@PostConstruct
	public void init() {
		super.init();
		try {
			FileUtils.createFolders(localTempFolderfileMq);
		} catch (IOException e) {
			throw new ChcSystemException(e.getMessage());
		}
	}

	protected <C extends ConfigurationFMSFTSCommon> boolean canConvertMQFileToEBCDIC(E entity) throws ChcException{
		
		if ( entity instanceof CswEntityOutMqWithFile) {
			CswEntityOutMqWithFile wf = (CswEntityOutMqWithFile) entity;
			/**
			 * TODO aggiungere motivazione rimozione O
			 */
//			if( ( wf.getCharType().equals(CodePage.O) && conf.getSndCodePage().equals(CodePage.EBCDIC)) || wf.getCharType().equals(CodePage.EBCDIC)){
				if(wf.getCharType().equals(CodePage.EBCDIC)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * metodo usato dal Poller, per i RETRY
	 * @param dto
	 */
	public void submitToHub(@NonNull SubmitDTO dto) {
		
		Optional<E> entityOpt = dbRepository.findById(dto.getId());
		if(!entityOpt.isPresent()) return;
		
		E entity = entityOpt.get();
		P primitive = null;
		try {
			entity.setCswStatus(ClientTaskStatus.SENDING);
			entity = genericDAO.update(entity);
			primitive = readOriginalPrimitive(entity);
			
			List<AbstractPart> parts;
			
			parts = createPartsInternal(entity, buildMessageDTO(entity), primitive);
			
			sendToHubInternal(entityOpt.get(), parts, primitive);

		}catch (ChcStubException | ChcException e) {
			// ignorata percè gestita nelle precedenti chiamate
				
		} catch (Exception e) {
			// unico errore possibile: lettura dal DB con deserializzazione Java
			// non c'è niente da fare
			CswLog.error(log, String.format("Error occurred while resending the entity to the CCR. Operation FAILED", e.toString()));
			performFailedActions(entity, 71, e.toString(), I18nService.ERR_PROCESSING_MESSAGE.name());
		}
		CswLog.setLogData(null);
	}
	

	protected void sendToHubInternal(E entity, List<AbstractPart> parts, @NonNull P primitiveDto) {
		
		int rejectReason;
		String statusInfo;
		
		try {
			//sendToHub(entity, parts, getCCRInboundControllerPath());
			
			// se si tratta di retry devo resettare, altrimenti se va a buon fine
			// mi ritroverei i precedenti valori
			entity.setStatusInfo(null);
			entity.setRejectReason(null); 
			
			LongProcessingResponseDTO resp = sendToHub2(entity, parts, getCCRInboundControllerPath());
			
			if(resp.getError() == null) {
				entity.setCompleteTime(LocalDateTime.now());
				entity.setStatusInfo(null);
				entity.setCswStatus(ClientTaskStatus.ON_HUB);
				CswLog.debug(log, "Sent to HUB DONE");
				updateStatus(entity, SendStatusMQ.LOCALLY_CONFIRMED);
			}else {
				// errore su HUB con reponse 200 
				if(!resp.getError().isRetry()) {
					rejectReason = 70;
					statusInfo = I18nService.ERROR_ON_HUB.name();
				}else {
					rejectReason = 71;
					statusInfo = I18nService.ERR_INVOKING_HUB.name();
				}
				
				handleStubException(new ChcStubException(resp.getError()), entity, primitiveDto, rejectReason, 
						resp.getError().getLocalizedMessage(), Msg.getMessage(statusInfo));
			}

			
		} catch (ChcStubException e) {
			// error calling CCR hub
			
			rejectReason = 71;
			statusInfo = I18nService.ERR_INVOKING_HUB.name();
			// se  HttpStatus >= 300, sono tutti errori client side
			// quindi forziamo 503 per il retry
			e.getError().setHttpStatus(503);
			handleStubException(e, entity, primitiveDto, rejectReason, e.getLocalizedMessage(), Msg.getMessage(statusInfo));
		}finally {
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
	
	/* nel caso di primitiva non corretta, può capitare che l'entity ottenuta non sia
	 * perfettamente compatibile con l tabella del database, per cui il salvataggio va in errore
	 * Bisogna intercettare quindi l'errore di persistenza che non sia dovuto ad 
	 * un error fisico del database
	 */
	protected void tryToSaveOnValidationException(@NonNull E entity, @NonNull P primitiveDto, ChcException e) throws ChcUnrecoverableException {
		try {
			entity.setStatus(SendStatusMQ.REJECTED);
			entity.setAcceptTime(LocalDateTime.now());
			handleManagedException(entity, e, primitiveDto);
			setCompleteForErrorMQ(entity, e);
			dbRepository.save(entity);
			CswLog.getLogData().setId(entity.getId().toString());
			CswLog.error(log, String.format("Entity created with status info: %s and rejected reason: %s", entity.getStatusInfo(), entity.getRejectReason()));
		} catch (PersistenceException ex) {
			// con errore di scrittura del record
			
			throw new ChcUnrecoverableException(e.getCode(), ex.toString());
		} catch(org.springframework.transaction.CannotCreateTransactionException ex) {
			// con database spento
			throw new ChcRollbackException(I18nCommon.ERR_DATABASE_CONNECTION, ex.toString());
		}
	}
	
	protected E updateStatus(@NonNull E entity, SendStatusMQ status) {

		entity.setStatus(status);
		entity = genericDAO.update(entity);
		
		CswLog.info(log, String.format("Message updated with status: %s", status));
		return entity;
	}

	
	private E performFailedActions(E entity, int rejectReason, String message, String statusInfo) {
		entity.setCswStatus(ClientTaskStatus.FAILED);
		entity.setStatusInfo(statusInfo);
		entity.setRejectReason(rejectReason);
		entity.setStatus(SendStatusMQ.IN_ERROR);

		send1402or1941(entity, message);
		
		entity = genericDAO.update(entity);

		return entity;
	}
	
	private E handleStubException(ChcStubException e, E entity, P primitiveDto, int rejectReason, String message, String statusInfo) {
		if (e.isRetry() && entity.getRetryCounter() < maxRetryAttempts) {
			
			CswLog.getLogData().setRetryCount(entity.getRetryCounter());
			CswLog.error(log, String.format("Error sending to CCR %s. Changing to WAITING_FOR_RETRY id: %s err:%s",
					entity.getClass().getSimpleName(), entity.getId(), e.toString()));
			entity = savePrimitiveForRetry(entity, primitiveDto);
			
			entity.setCswStatus(ClientTaskStatus.WAITING_FOR_RETRY);
			entity.setStatusInfo(statusInfo);
			return genericDAO.update(entity);
		} else {
			entity.setSendErrorTimestamp(LocalDateTime.now());
			setCompleteForErrorMQ(entity, e);
			CswLog.getLogData().setRetryCount(entity.getRetryCounter());
			CswLog.error(log, String.format("Error sending to CCR id: %s err:%s",
					entity.getId(), e.toString()));
			return performFailedActions(entity, rejectReason, message, statusInfo);
		}
	}
	
	protected List<AbstractPart> createPartsInternal(E entity, D outboundDTO, P primitiveDto) throws ChcStubException, ChcException{
		try {
			// call overridden createParts, implements for FSM and FTS
			return createParts(entity, outboundDTO);
			
		} catch (ChcStubException e) {
			// error calling crypto hub
			handleStubException(e, entity, primitiveDto, 72, e.getLocalizedMessage(), Msg.getMessage(I18nService.ERR_INVOKING_CRYPTO_HUB.name()));
			throw e;
		}catch (ChcException e) {
			// errore gestito causato da encription validazine ed atri fattori
			// impossibile eseguire retry
			handleManagedException(entity, e, primitiveDto);
			entity.setStatus(SendStatusMQ.IN_ERROR);
//			entity.setComplete(1);
			genericDAO.update(entity);
			throw e;
		}
	}
	
	
	

	protected String createVfn(@NonNull MQ1400SecSendFilereq dto) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyMMdd");
		return dto.getBaLoc() + dto.getBaRem() + LocalDate.now().format(df) + RandomStringUtils.random(2, true, true).toUpperCase();
	}
	
	@Override
	protected void validateBlob(E entity, File dec) throws ChcException, IOException {
		// not used for MQ
	}
}
