package com.cbi.ccr.outbound.service;

import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.cbi.ccr.common.EventService;
import com.cbi.ccr.common.I18nCccrCommon;
import com.cbi.ccr.common.jwe.jwt.CCRJweJwtService;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.routing.BaUrlService;
import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.BaUrl;
import com.cbi.ccr.domain.BaUrlRepository;
import com.cbi.ccr.domain.ClientMessageStatus;
import com.cbi.ccr.domain.ClientMessageSubStatus;
import com.cbi.ccr.domain.CommonEntity;
import com.cbi.ccr.domain.CommonMessageRepository;
import com.cbi.ccr.dto.mq.command.dashboard.ChcEvent;
import com.cbi.ccr.dto.mq.command.dashboard.ChcTrackInfo;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;
import com.cbi.ccr.orc.OrchestratorService;
import com.cbi.ccr.outbound.I18nCcrOutboundBatch;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.frw.encryption.service.EncryptionService;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtilsProxy;
import com.cbi.frw.http.stream.LongProcessingResponseDTO;
import com.cbi.frw.persistence.service.AbstractService;
import com.cbi.repo.stub.RepoStub;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CommonOutboundService<T extends CommonEntity, R extends CommonMessageRepository<T>, E extends EventService<T>, O extends OrchestratorService<T, E, R>>
		extends AbstractService {

	protected R repository;
	private E eventService;
	private O orchestratorService;

	@Autowired
	private BaUrlService baUrlRepository;
	@Autowired
	protected RepoStub repoStub;

	@Autowired
	protected HubEncryptionUtil encryptionUtil;
	
	@Autowired
	protected EncryptionService encryptionService;
	
	@Autowired
	protected CCRJweJwtService ccrJweJwtService;

	@Autowired
	protected ModelMapper mapper;

	@Autowired
	private HttpUtilsProxy httpUtilsProxy;
	
	protected CommonOutboundService(R repository, E eventService, O orchestratorService) {
		this.repository = repository;
		this.eventService = eventService;
		this.orchestratorService = orchestratorService;
	}

	protected abstract List<AbstractPart> buildAttachments(T message) throws ChcStubException, ChcException;

	protected abstract T buildMessage(ChcCommand command);

	protected abstract String getUri();

	protected abstract ServiceType getServiceType();

	protected String getBaUrlFormNotification(String localBaId) throws ChcException {
		BaUrl localBaUrl = baUrlRepository.findFirstByBaId(localBaId);
		if (localBaUrl == null)
			throw new ChcException(I18nCccrCommon.MISSING_BA_URL_CONFIGURATION, localBaId);
		return localBaUrl.getUrl() + CSWInboundControllerPath.BASE + CSWInboundControllerPath.NOTIFICATION;
	}

	public void processOutboundMessage(ChcCommand command) {
		LocalDateTime tmsReceived = LocalDateTime.now();
		Optional<T> messageOptional = repository.findById(Long.valueOf(command.getPhyFileDescriptor().getChcPhyMsgId()));

		T message;
		

		if (!messageOptional.isPresent()) {
			message = buildMessage(command);
			transactionTemplate.executeWithoutResult(t -> repository.save(message));
			// Send event BatchFlowReceived
			eventService.sendEvent(message, ChcEvent.BATCH_FLOW_RECEIVED, ChcEvent.ORC, ChcEvent.CCR, null, null,
					false, null, null);
		} else {

			message = messageOptional.get();
			
			if(command.getPhyFileDescriptor() != null ) {
				if(command.getPhyFileDescriptor().getCsc() != null && message.getCsc() == null) 
					message.setCsc(command.getPhyFileDescriptor().getCsc());
				
				if(command.getPhyFileDescriptor().getQtm() != null && message.getQtm() == null) 
					message.setQtm(command.getPhyFileDescriptor().getQtm());
			}
			
			message.setSubStatus(ClientMessageSubStatus.SENDING_TO_REMOTE_BA);
			transactionTemplate.executeWithoutResult(
					t -> repository.setSubStatus(ClientMessageSubStatus.SENDING_TO_REMOTE_BA, message.getId()));
			// Send event BatchFlowReceived
			eventService.sendEvent(message, ChcEvent.BATCH_FLOW_RECEIVED, ChcEvent.ORC, ChcEvent.CCR, null, null,
					false, null, null);
		}
		message.setTmsReceived(tmsReceived);
		
		sendOrResendToRemoteBA(message);
	}

	public void sendOrResendToRemoteBA(T message) {
		BaUrl remoteBaUrl = baUrlRepository.findFirstByBaId(message.getRemoteBaId());
		
		if (remoteBaUrl == null) {
			message.setLog(String.format("%s RemoteBaId:%s", I18nCccrCommon.MISSING_BA_URL_CONFIGURATION, message.getRemoteBaId()));
			transactionTemplate.executeWithoutResult(t -> repository.save(message));
			eventService.sendEvent(message, ChcEvent.ERROR_ON_PROCESSING, ChcEvent.ORC, ChcEvent.CCR, null, null, false, ChcEvent.ERROR_ON_PROCESSING, message.getLog());
			return;
		}
		
		message.setTmsStartSending(LocalDateTime.now());
		transactionTemplate.executeWithoutResult(t -> repository.save(message));
		
		
		List<? extends AbstractPart> parts = null;
		try {
			
			parts = buildAttachments(message);
			
			Map<String, String> headers = new HashMap<>();
			checkSecurity(headers);
			
			
			// event BatchFlowSent
			eventService.sendEvent(message, ChcEvent.BATCH_FLOW_SENT, ChcEvent.CCR, ChcEvent.CSW, ChcEvent.END, null,
					true, null, null);
			
			DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
			ChcTrackInfo trackInfo = ChcTrackInfo.builder().type("T3").ts(formatter.format(Instant.now())).build();
			
			HttpResponse<LongProcessingResponseDTO> resp = httpUtilsProxy.postRequestMultiPart(
							String.format("%s%s/%s", remoteBaUrl.getUrl(),
									CSWInboundControllerPath.BASE + getUri(), message.getId()),
							null, headers, parts, LongProcessingResponseDTO.class, 5000, true);
			
			
			CcrLog.getLogData().setMessage(String.format("sent to remote BA %s:", remoteBaUrl.getUrl()));
			CcrLog.info(log);
			
			
			
			if(resp.getResponse().getError() == null) {
				
				transactionTemplate.executeWithoutResult(
						t -> repository.setStatusAndSubStatus(ClientMessageStatus.COMPLETE, ClientMessageSubStatus.POS_NOTIFICATION_FROM_CSW, message.getId()));

				eventService.sendEvent(message, ChcEvent.POSITIVE_NOTIFICATION_RECEIVED, ChcEvent.CSW, ChcEvent.CCR, null,
						trackInfo, false, null, null);

				// file sent to remote BA, sending positive notification to Orchestrator
				orchestratorService.outboundSendNotificationToOrchestrator(message, true);
			} else {
				
				eventService.sendEvent(message, ChcEvent.NEGATIVE_NOTIFICATION_RECEIVED, ChcEvent.CSW, ChcEvent.CCR, null,
						trackInfo, false, resp.getResponse().getError().getErrorCode(), resp.getResponse().getError().getLocalizedMessage());
				
				// invio negative notification received
				handleException(new ChcStubException(resp.getResponse().getError()), message);
			}


		} catch (Exception e) {
			handleException(e, message);

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

	private void checkSecurity(Map<String, String> headers) throws ChcStubException {
		if(ccrJweJwtService.isSecurityEnabled())
			headers.put(CCRJweJwtService.PROP_TOKEN_HEADER, ccrJweJwtService.getJwt());
	}

	public void processAndForwardNotification(Long id, String cmdTms, boolean esito) {
		commonProcessNotification(id, cmdTms, esito, getServiceType());
	}

	public void processAndForwardNotification(Long chcId, String cmdTms, boolean esito, ServiceType serviceType) {
			commonProcessNotification(chcId, cmdTms, esito, serviceType);
	}

	private void commonProcessNotification(Long id, String cmdTms, boolean esito, ServiceType serviceType) {
		try {
			Optional<T> optMessage = repository.findById(id);
			if (!optMessage.isPresent())
				throw new ChcException(I18nCcrOutboundBatch.MESSAGE_NOT_FOUND);

			// event POSNotificationReceived o event NEGNotificationReceived
			if (esito) {
				transactionTemplate.executeWithoutResult(
						t -> repository.setSubStatus(ClientMessageSubStatus.POS_NOTIFICATION_FROM_ORCH, id));
				eventService.sendEvent(optMessage.get(), ChcEvent.POSITIVE_NOTIFICATION_RECEIVED, ChcEvent.ORC,
						ChcEvent.CCR, null, null, false, null, null);
			} else {
				// event NEGNotificationSent
				transactionTemplate.executeWithoutResult(
						t -> repository.setSubStatus(ClientMessageSubStatus.NEG_NOTIFICATION_FROM_ORCH, id));
				eventService.sendEvent(optMessage.get(), ChcEvent.NEGATIVE_NOTIFICATION_RECEIVED, ChcEvent.ORC,
						ChcEvent.CCR, null, null, false, ChcEvent.NEGATIVE_NOTIFICATION_RECEIVED, optMessage.get().getLog());

			}

			T message = optMessage.get();

			NotificationDTO dto = new NotificationDTO(serviceType, message.getClientSwMessageId(),
					message.getLocalBaId(), message.getRemoteBaId(), esito, cmdTms);
			
			Map<String, String> headers = new HashMap<>();
			checkSecurity(headers);
			
			httpUtilsProxy.postRequestWithBody(getBaUrlFormNotification(message.getLocalBaId()), headers, dto, Void.class, 10000);

			if (esito) {
				transactionTemplate
						.executeWithoutResult(t -> repository.setStatusAndSubStatus(ClientMessageStatus.COMPLETE,
								ClientMessageSubStatus.POSITIVE_NOTIFICATION_SENT, id));
				
				CcrLog.getLogData().setMessage(String.format("POSITIVE_NOTIFY_FLOW Sent to sender BA %s:", getBaUrlFormNotification(message.getLocalBaId())));
				CcrLog.info(log);

				// event POSNotificationSent
				eventService.sendEvent(message, ChcEvent.POSITIVE_NOTIFICATION_SENT, ChcEvent.CCR, ChcEvent.CSW, null,
						null, false, null, null);

			} else {
				transactionTemplate
						.executeWithoutResult(t -> repository.setStatusAndSubStatus(ClientMessageStatus.FAILED,
								ClientMessageSubStatus.NEGATIVE_NOTIFICATION_SENT, id));
				
				CcrLog.getLogData().setMessage(String.format("NEGATIVE_NOTIFY_FLOW Sent to sender BA %s:", getBaUrlFormNotification(message.getLocalBaId())));
				CcrLog.info(log);
				
				// event NEGNotificationSent
				eventService.sendEvent(message, ChcEvent.NEGATIVE_NOTIFICATION_SENT, ChcEvent.CCR, ChcEvent.CSW, null,
						null, false, ChcEvent.NEGATIVE_NOTIFICATION_RECEIVED, message.getLog());


			}
		} catch (ChcStubException | ChcException e) {
			CcrLog.getLogData().setMessage(String.format("error while sending notification %s:", e.toString()));
			CcrLog.error(log);

			transactionTemplate.executeWithoutResult(t -> repository.setStatus(ClientMessageStatus.FAILED, id));
		}
	}

	private void handleException(Exception e, T message) {
		CcrLog.getLogData().setMessage(String.format("Error sending to remote BA %s:", e.toString()));
		CcrLog.error(log);

//		if (e instanceof ChcStubException && ((ChcStubException) e).isRetry()) {
//			transactionTemplate.executeWithoutResult(
//					t -> repository.setStatusAndLog(ClientMessageStatus.TO_RETRY, e.toString(), message.getId()));
//		} else {
		transactionTemplate.executeWithoutResult(t -> repository.setLog(e.toString(), message.getId()));
		orchestratorService.outboundSendNotificationToOrchestrator(message, false);
//		}
	}
	
	protected byte[] getEncryptionServiceKey(UUID key) throws ChcException{
		byte[] encryptionServiceKey;
		try {
			encryptionServiceKey = encryptionService.getKey(key, 
					ccrJweJwtService.getCcrPublicKey(), 
					ccrJweJwtService.getCCRPrivateKey(), 
					ccrJweJwtService.getJwt());
		} catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| NoSuchAlgorithmException | InvalidKeySpecException | ChcStubException | DecoderException  e) {
			throw new ChcException(I18nCommon.ERR_DECRYPT, e.getLocalizedMessage());
		}
		return encryptionServiceKey;
	}

}
