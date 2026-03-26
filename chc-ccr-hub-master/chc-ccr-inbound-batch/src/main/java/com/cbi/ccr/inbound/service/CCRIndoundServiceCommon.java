package com.cbi.ccr.inbound.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.cbi.ccr.common.EventService;
import com.cbi.ccr.common.I18nCccrCommon;
import com.cbi.ccr.common.jwe.jwt.CCRJweJwtService;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.routing.BaUrlService;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.domain.BaUrl;
import com.cbi.ccr.domain.BaUrlRepository;
import com.cbi.ccr.domain.ClientMessageStatus;
import com.cbi.ccr.domain.ClientMessageSubStatus;
import com.cbi.ccr.domain.CommonEntity;
import com.cbi.ccr.domain.CommonEntity.Direction;
import com.cbi.ccr.domain.CommonMessageRepository;
import com.cbi.ccr.domain.FMSMessage;
import com.cbi.ccr.domain.FTSMessage;
import com.cbi.ccr.domain.MSSMessage;
import com.cbi.ccr.dto.mq.command.dashboard.ChcEvent;
import com.cbi.ccr.dto.mq.command.dashboard.ChcTrackInfo;
import com.cbi.ccr.inbound.I18nCcrInbound;
import com.cbi.ccr.orc.OrchestratorService;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.frw.encryption.service.EncryptionService;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.persistence.service.AbstractService;
import com.cbi.frw.persistence.service.GenericDAO;
import com.cbi.repo.stub.RepoStub;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CCRIndoundServiceCommon<T extends CommonEntity, R extends CommonMessageRepository<T>, D extends ClientMessageDTO, E extends EventService<T>, O extends OrchestratorService<T, E, R>>
		extends AbstractService {

	protected R repository;
	private Class<T> entityClass;
	private E eventService;
	private O orchestratorService;

	@Autowired
	protected RepoStub repoStub;

	@Autowired
	protected HubEncryptionUtil encryptionUtil;

	@Autowired
	protected EncryptionService encryptionService;

	@Autowired
	protected ModelMapper mapper;

	@Autowired
	private GenericDAO genericDAO;

	@Autowired
	private CCRIdServiceGeneratorStub idServiceGeneratorStub;

	@Autowired
	private BaUrlService baUrlRepo;

	@Autowired
	protected CCRJweJwtService ccrJweJwtService;

	protected CCRIndoundServiceCommon(R repository, Class<T> entityClass, E eventService, O orchestratorService) {
		this.repository = repository;
		this.entityClass = entityClass;
		this.eventService = eventService;
		this.orchestratorService = orchestratorService;
	}

	protected abstract boolean isServiceDataCorrect(MessageWrapperDTO wrapper);

	protected abstract T messageExists(D dto);
	protected abstract void deleteEntity(T entity);
	protected abstract void beforeSave(T message, MessageWrapperDTO wrapper);


	protected Long getPhyMsgId() throws ChcException {
		String id;
		try {
			
			id = idServiceGeneratorStub.getId();
		} catch (ChcStubException e) {
			throw new ChcException(I18nCccrCommon.CHC_ID_GENERATOR_SERVICE_UNAVAILABLE,
					e.getError().getLocalizedMessage(), true);
		}
		return new Long(id);
	}

	public void processMessage(MessageWrapperDTO wrapper, Class<D> dtoClass, MultipartFile... files) throws ChcException, ChcStubException {
		
		if (!isServiceDataCorrect(wrapper))
			throw new ChcException(I18nCcrInbound.ERR_INVALID_SERVICE_DATA);

		CcrLog.getLogData().setService(wrapper.getType().toString());
		
		D dto = decrypt(wrapper, dtoClass);
		
		CcrLog.getLogData().setMessage("Json decrypted");
		CcrLog.debug(log);
		
		T messageEntity = messageExists(dto);
		
		/*
		 * If the message already exists, it has to be deleted to start over again
		 * The hub does not manage duplicates.
		 */
		if (messageEntity != null) {
			CcrLog.getLogData().setMessage("Entity already exist, deleting existing entity to process message");
			CcrLog.info(log);
			deleteEntity(messageEntity);
		}

		
		messageEntity = mapper.map(dto, entityClass);
		messageEntity.setStatus(ClientMessageStatus.RECEIVED);
		messageEntity.setChcId(getPhyMsgId());
		messageEntity.setId(messageEntity.getChcId());
		messageEntity.setDirection(Direction.INBOUND);
//		messageEntity.setBaInsertTimestamp(LocalDateTime.now()); Ricevuto da CSW mittente

		CcrLog.getLogData().setId(messageEntity.getChcId().toString());
		
		beforeSave(messageEntity, wrapper);


		// send Event BatchFlowReceived: servono chcid e id file e messaggio
		ChcTrackInfo trackInfo = ChcTrackInfo.builder().type("T0")
				.ts(OffsetDateTime.of(messageEntity.getBaInsertTimestamp(), ZoneOffset.UTC).toString()).build();
		
		
		try {
			checksBaUrls(messageEntity);
		
			if (messageEntity instanceof FMSMessage) {
				((FMSMessage) messageEntity).setRepoMessageId(wrapper.getMessageId().toString());
				((FMSMessage) messageEntity).setRepoFileId(wrapper.getFileId().toString());
				saveToRepo(files[0].getInputStream(), wrapper.getMessageId().toString(), files[0].getSize());
				saveToRepo(files[1].getInputStream(), wrapper.getFileId().toString(),  files[1].getSize());
			} else if (messageEntity instanceof FTSMessage) {
				((FTSMessage) messageEntity).setRepoFileId(wrapper.getFileId().toString());
				saveToRepo(files[0].getInputStream(), wrapper.getFileId().toString(), files[0].getSize());
			} else if (messageEntity instanceof MSSMessage) {
				((MSSMessage) messageEntity).setRepoMessageId(wrapper.getMessageId().toString());
				saveToRepo(files[0].getInputStream(), wrapper.getMessageId().toString(), files[0].getSize());
			}
			
			eventService.sendEvent(messageEntity, ChcEvent.BATCH_FLOW_RECEIVED, ChcEvent.CSW, ChcEvent.CCR, ChcEvent.BEGIN,
					trackInfo, true, null, null);

			messageEntity.setSubStatus(ClientMessageSubStatus.SAVED_INTO_REPO);

			genericDAO.save(messageEntity);

			CcrLog.getLogData().setMessage("saved into the Repo");
			CcrLog.info(log);

			orchestratorService.inboundSendToOrchestrator(messageEntity);

		} catch (ChcException e) {
			messageEntity.setLog(e.getLocalizedMessage());
			eventService.sendEvent(messageEntity, ChcEvent.ERROR_ON_PROCESSING, ChcEvent.CSW, ChcEvent.CCR, null, null, false, e.getCode(), e.getLocalizedMessage());
			throw e;
		}catch (IOException e) {
			messageEntity.setLog(e.getLocalizedMessage());
			eventService.sendEvent(messageEntity, ChcEvent.ERROR_ON_PROCESSING, ChcEvent.CSW, ChcEvent.CCR, null, null, false, "ERR_IO", e.getLocalizedMessage());
			throw new ChcException(I18nCommon.ERR_NETWORK_ERROR, e); 
		}

	}

	/**
	 * non si deve fare il controllo della RemoteBa, perchè potrebbe essere un PA
	 * non centita del CCR
	 * 
	 * @param messageEntity
	 * @throws ChcException
	 */
	private void checksBaUrls(T messageEntity) throws ChcException {
		BaUrl localBaIdUrl = baUrlRepo.findFirstByBaId(messageEntity.getLocalBaId());

		if (localBaIdUrl == null)
			throw new ChcException(I18nCccrCommon.MISSING_BA_URL_CONFIGURATION, messageEntity.getLocalBaId());
		
	}

	protected void saveToRepo(InputStream file, String id, long fileSize) throws ChcStubException {
		
		
		String jwt;
		try {
			jwt = ccrJweJwtService.getJwt();
		} catch (ChcStubException e) {
			CcrLog.getLogData().setFunction(String.format("%s-%s", CcrLog.getLogData().getFunction(), "getJwt"));
			e.getError().setLocalizedMessage(String.format("Error calling JwtService at url:%s err: %s", ccrJweJwtService.getNexiOauthTokenUrl(), e.getError().getLocalizedMessage()));
			e.getError().setHttpStatus(503);
			throw e;
		}
		
		try {
			repoStub.upload(file, id, id, jwt, fileSize);
			
		} catch (ChcStubException e) {
			CcrLog.getLogData().setFunction(String.format("%s-%s", CcrLog.getLogData().getFunction(), "saveToRepo"));
			e.getError().setLocalizedMessage(String.format("Error calling Repository at url:%s err: %s", repoStub.getRepoEndpointUrl(), e.getError().getLocalizedMessage()));
			e.getError().setHttpStatus(503);
			throw e;
		}
	}

	public D decrypt(MessageWrapperDTO wrapper, Class<D> dtoClass) throws ChcException {
		byte[] encryptionServiceKey;
		try {
			encryptionServiceKey = encryptionService.getKey(wrapper.getWrapperKey(), 
					ccrJweJwtService.getCcrPublicKey(), 
					ccrJweJwtService.getCCRPrivateKey(), 
					ccrJweJwtService.getJwt());
		} catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| NoSuchAlgorithmException | ChcStubException | InvalidKeySpecException | DecoderException  e) {
			throw new ChcException(I18nCommon.ERR_DECRYPT, e.getLocalizedMessage());
		}
		return JSON.fromJson(
				encryptionUtil.decrypt(wrapper.getEncryptedBody(), encryptionServiceKey),
				dtoClass);
	}
}
