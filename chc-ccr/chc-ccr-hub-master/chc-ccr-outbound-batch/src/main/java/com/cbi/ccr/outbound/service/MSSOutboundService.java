package com.cbi.ccr.outbound.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.MSSEventService;
import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.dto.fms.MSSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.ClientMessageStatus;
import com.cbi.ccr.domain.ClientMessageSubStatus;
import com.cbi.ccr.domain.CommonEntity.Direction;
import com.cbi.ccr.domain.MSSMessage;
import com.cbi.ccr.domain.MSSMessageRepository;
import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;
import com.cbi.ccr.dto.mq.command.orch.PhyFileDescriptor;
import com.cbi.ccr.orc.MSSOrchestratorService;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.http.JsonPart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MSSOutboundService
		extends CommonOutboundService<MSSMessage, MSSMessageRepository, MSSEventService, MSSOrchestratorService> {

	protected MSSOutboundService(MSSMessageRepository repository, MSSEventService eventService,
			MSSOrchestratorService orchestratorService) {
		super(repository, eventService, orchestratorService);
	}

	public static String format(LocalDateTime ldt) {
		return ldt.format(DateTimeFormatter.ofPattern("yyMMddhhmmss+0000"));
	}

	public static String format2(LocalDateTime ldt) {
		return ldt.format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));
	}

	@Override
	protected String getUri() {
		return CSWInboundControllerPath.RECEIVE_MSS;
	}

	@Override
	protected ServiceType getServiceType() {
		return ServiceType.MSS;
	}

	public MSSMessage buildMessage(ChcCommand command) {
		PhyFileDescriptor descriptor = command.getPhyFileDescriptor();
		CCRtransportData ccRtransportData = command.getCcrTransportData();
		MSSMessage mssMessage = new MSSMessage();
		mssMessage.setInsertDate(LocalDateTime.now());
		mssMessage.setStatus(ClientMessageStatus.RECEIVED);
		mssMessage.setSubStatus(ClientMessageSubStatus.SENDING_TO_REMOTE_BA);
		mssMessage.setId(Long.valueOf(descriptor.getChcPhyMsgId()));
		mssMessage.setChcId(Long.valueOf(command.getChcIds().get(0).getId()));
		mssMessage.setDirection(Direction.INBOUND);
		mssMessage.setLocalBaId(ccRtransportData.getLocalBaId());
		mssMessage.setRemoteBaId(ccRtransportData.getRemoteBaId());
		mssMessage.setRepoMessageId(descriptor.getUris().get(0).getId());
		mssMessage.setMessageType(ccRtransportData.getMessageType());
		
		mssMessage.setCsc(descriptor.getCsc());
		mssMessage.setQtm(descriptor.getQtm());
		
		try {
			mssMessage.setMessageLeng(
					ccRtransportData.getMessageLength() != null ? Integer.valueOf(ccRtransportData.getMessageLength())
							: 0);
		} catch (Exception e) {
			log.error("Error getting  MessageLeng", e);
		}
		mssMessage.setUdr(ccRtransportData.getUserDataRemote());
		mssMessage.setTur(ccRtransportData.getTur());
		return mssMessage;
	}

	@Override
	protected List<AbstractPart> buildAttachments(MSSMessage message) throws ChcStubException, ChcException {
		File file = repoStub.download(message.getRepoMessageId(), ccrJweJwtService.getJwt());
		FilePart attachmentFile = new FilePart("message", file, "message.bin");
		UUID wrapperKey = UUID.randomUUID();

		LocalDateTime now = LocalDateTime.now();
		MSSInboundMessageDTO mssMessageDTO = mapper.map(message, MSSInboundMessageDTO.class);
		if (message.getMsgId() == null)
			mssMessageDTO.setMsgId(String.valueOf(System.currentTimeMillis()));
		// INVERT LOCALBA AND REMOTEBA
		mssMessageDTO.setLocalBaId(message.getRemoteBaId());
		mssMessageDTO.setRemoteBaId(message.getLocalBaId());
		if (message.getBaInsertTimestamp() != null)
			mssMessageDTO.setEasSubTime(format(message.getBaInsertTimestamp()));
		else
			mssMessageDTO.setEasSubTime(format(now));
		mssMessageDTO.setFerSubTime(format(now));
		mssMessageDTO.setFenDelTime(format(now));
		mssMessageDTO.setFerDelTime(format(now));
		String encryptedMsgBody = encryptionUtil.encrypt(JSON.toJson(mssMessageDTO),
				getEncryptionServiceKey(wrapperKey));

		MessageWrapperDTO wrapper = new MessageWrapperDTO();
		wrapper.setWrapperKey(wrapperKey);
		wrapper.setMessageId(UUID.fromString(message.getRepoMessageId()));
		wrapper.setFileId(null);
		wrapper.setEncryptedBody(encryptedMsgBody);
		wrapper.setType(ServiceType.MSS);

		JsonPart messageWrapper = new JsonPart("wrapper", wrapper);

		message.setTmsStartSending(LocalDateTime.now());
		mssMessageDTO.setTmsStartSending(message.getTmsStartSending());
		mssMessageDTO.setTmsReceived(message.getTmsReceived());
		
		return Arrays.asList(attachmentFile, messageWrapper);
	}

//	public void processMSS(PhyFileDescriptor descriptor, CCRtransportData ccRtransportData) {
//
//		Optional<MSSMessage> mssMessageOptional = repository.findById(Long.valueOf(descriptor.getChcPhyMsgId()));
//		
//		MSSMessage mssMessage;
//		
//		if(!mssMessageOptional.isPresent()) {
//			mssMessage = new MSSMessage();
//			mssMessage.setInsertDate(LocalDateTime.now());
//			mssMessage.setStatus(ClientMessageStatus.RECEIVED);
//			mssMessage.setSubStatus(ClientMessageSubStatus.SENDING_TO_REMOTE_BA);
//			mssMessage.setId(Long.valueOf(descriptor.getChcPhyMsgId()));
//			mssMessage.setChcId(Long.valueOf(descriptor.getChcPhyMsgId()));
//			mssMessage.setDirection(Direction.INBOUND);
//			mssMessage.setLocalBaId(ccRtransportData.getLocalBaId());
//			mssMessage.setRemoteBaId(ccRtransportData.getRemoteBaId());
//			mssMessage.setRepoMessageId(descriptor.getURI().get(0).getId());
//			transactionTemplate.executeWithoutResult(t -> repository.save(mssMessage));
//			
//			// Send event BatchFlowReceived
//			sendEvent(mssMessage, ChcEvent.BATCH_FLOW_RECEIVED, ChcEvent.ORC, ChcEvent.CCR, null, null, false);
//			
//		}else{
//			mssMessage = mssMessageOptional.get();
//			mssMessage.setLocalBaId(ccRtransportData.getLocalBaId());
//			mssMessage.setRemoteBaId(ccRtransportData.getRemoteBaId());
//			mssMessage.setSubStatus(ClientMessageSubStatus.SENDING_TO_REMOTE_BA);
//			transactionTemplate.executeWithoutResult(t -> repository.setSubStatus(ClientMessageSubStatus.SENDING_TO_REMOTE_BA, mssMessage.getId()));
//			// Send event BatchFlowReceived
//			sendEvent(mssMessage, ChcEvent.BATCH_FLOW_RECEIVED, ChcEvent.ORC, ChcEvent.CCR, null, null, false);
//			
//		}
//		
//			sendMSSToRemoteBA(mssMessage);
//			
//	}

//	public void sendMSSToRemoteBA(MSSMessage mssMessage) {
//		
//		try {
//		Optional<BaUrl> remoteBaUrl = baUrlRepository.findFirstByBaIdAndActive(mssMessage.getRemoteBaId(), true);
//		if(!remoteBaUrl.isPresent()) {
//			mssMessage.setStatus(ClientMessageStatus.TO_RETRY);
//			mssMessage.setLog("Unable to find an active RemoteBA");
//			transactionTemplate.executeWithoutResult(t -> repository.save(mssMessage));
//			return;
//		}
//		
//		File message = repoStub.download(mssMessage.getRepoMessageId());
//		InputStreamPart attachmentMessage = new InputStreamPart("message", new FileInputStream(message), "message.bin");
//		
//		LocalDateTime now = LocalDateTime.now();
//		MSSMessageDTO mssMessageDTO = mapper.map(mssMessage, MSSMessageDTO.class);
//		//INVERT LOCALBA AND REMOTEBA
//		mssMessageDTO.setLocalBaId(mssMessage.getRemoteBaId());
//		mssMessageDTO.setRemoteBaId(mssMessage.getLocalBaId());
//		mssMessageDTO.setEasSubTime(format(mssMessage.getBaInsertTimestamp()));
//		mssMessageDTO.setFerSubTime(format(now));
//		mssMessageDTO.setFenDelTime(format(now));
//		mssMessageDTO.setFerDelTime(format(now));
//		String encryptedMsgBody = encryptionUtil.encrypt(JSON.toJson(mssMessageDTO), EncryptionKey.key);
//		
//		MessageWrapperDTO wrapper = new MessageWrapperDTO();
//		wrapper.setEncryptedBody(encryptedMsgBody);
//		wrapper.setType(ServiceType.MSS);
//		
//		JsonPart messageWrapper = new JsonPart("wrapper", wrapper);
//		HttpUtils.postRequestMultiPart(String.format("%s%s/%s", remoteBaUrl.get().getUrl(), CSWInboundControllerPath.BASE+CSWInboundControllerPath.RECEIVE_MSS, mssMessage.getId()), null, null, 
//				Arrays.asList(attachmentMessage, messageWrapper), Void.class, 5000, true);
//		
//		transactionTemplate.executeWithoutResult( t -> repository.setSubStatus(ClientMessageSubStatus.POS_NOTIFICATION_FROM_CSW, mssMessage.getId()));
//		
//		// event BatchFlowSent
//		sendEvent(mssMessage, ChcEvent.BATCH_FLOW_SENT, ChcEvent.ORC, ChcEvent.CSW, ChcEvent.END, null, true);
//		ChcTrackInfo trackInfo = ChcTrackInfo.builder()
//				.type("T3")
//				.ts(LocalDateTime.now())
//				.build();
//		sendEvent(mssMessage, ChcEvent.POSITIVE_NOTIFICATION_RECEIVED, ChcEvent.CSW, ChcEvent.CCR, null, trackInfo, false);
//		
//		// message sent to remote BA, sending positive notification to Orchestrator
//		sendNotificationToOrchestrator(mssMessage,true);
//		
//		}catch (FileNotFoundException e) {
//			log.error("unable to find downloaded files from the repository", e);
//			transactionTemplate.executeWithoutResult(t -> repository.setStatusAndLog(ClientMessageStatus.TO_RETRY, e.getMessage(), mssMessage.getId()));
//		}catch (ChcStubException e) {
//			log.error("error sending file to thw BA", e);
//			transactionTemplate.executeWithoutResult(t -> repository.setStatusAndLog(ClientMessageStatus.TO_RETRY, e.getLocalizedMessage(), mssMessage.getId()));
//		}catch (ChcException e) {
//			log.error("generic error occurred", e);
//			transactionTemplate.executeWithoutResult(t -> repository.setStatusAndLog(ClientMessageStatus.FAILED, e.getLocalizedMessage(), mssMessage.getId()));
//		}
//	}
//	
//	
//	public void sendNotificationToOrchestrator(MSSMessage mssMessage, boolean success) {
//
//		try {
//			ChcCommand notifyCmd = buildMSSCommand(mssMessage);
//			if(success) {
//				notifyCmd.setCmdName(CommandName.POSITIVE_NOTIFY_FLOW);
//			}else {
//				notifyCmd.setCmdName(CommandName.NEGATIVE_NOTIFY_FLOW);
//			}
//			notifyCmd.setWfSenderSt(ChcCommand.CCR);	
//			notifyCmd.setWfReceiverSt(ChcCommand.ORC);
//			
//			rabbitTemplate.convertAndSend(orchestratorExchange, "ccr", JSON.toJson(notifyCmd));
//			// event POSNotificationSent
//			if(success) {
//				sendEvent(mssMessage, ChcEvent.POSITIVE_NOTIFICATION_SENT, ChcEvent.CSW, ChcEvent.ORC, null, null, false);
//			}else {
//				sendEvent(mssMessage, ChcEvent.NEGATIVE_NOTIFICATION_SENT, ChcEvent.CSW, ChcEvent.ORC, null, null, false);
//			}
//
//			transactionTemplate.executeWithoutResult(t -> repository
//					.setSubStatus(ClientMessageSubStatus.NOTIFICATION_SENT_TO_ORCHESTRATOR, mssMessage.getId()));
//
//		} catch (AmqpException e) {
//
//			transactionTemplate.executeWithoutResult(
//					t -> repository.setStatusAndLog(ClientMessageStatus.TO_RETRY, e.getMessage(), mssMessage.getId()));
//		}
//
//	}
//	
//	
//	public void processAndForwardNotification(Long chcId, boolean esito) {
//		
//		try {
//			Optional<MSSMessage> optmssMessage = repository.findFirstByChcId(chcId);
//			if (!optmssMessage.isPresent())
//				throw new ChcException(I18nCcrOutbound.MESSAGE_NOT_FOUND);
//			
//			// event POSNotificationReceived o event NEGNotificationReceived
//			if(esito) {
//				transactionTemplate.executeWithoutResult( t -> repository.setSubStatus(ClientMessageSubStatus.POS_NOTIFICATION_FROM_ORCH, chcId));
//				sendEvent(optmssMessage.get(), ChcEvent.POSITIVE_NOTIFICATION_RECEIVED, ChcEvent.ORC, ChcEvent.CCR, null, null, false);
//			} else {
//				// TODO popolare oggetto error descriptor
//				transactionTemplate.executeWithoutResult( t -> repository.setSubStatus(ClientMessageSubStatus.NEG_NOTIFICATION_FROM_ORCH, chcId));
//				sendEvent(optmssMessage.get(), ChcEvent.NEGATIVE_NOTIFICATION_RECEIVED, ChcEvent.ORC, ChcEvent.CCR, null, null, false);
//			}
//			
//			MSSMessage mssMessage = optmssMessage.get();
//			NotificationDTO dto = new NotificationDTO(ServiceType.MSS, mssMessage.getClientSwMessageId(), esito);
//			
//			HttpUtils.postRequestWithBody(getBaUrl(mssMessage.getLocalBaId()), null, dto, Void.class, 10000);
//			if(esito) {
//				transactionTemplate.executeWithoutResult( t ->  repository.setStatusAndSubStatus(ClientMessageStatus.COMPLETE, ClientMessageSubStatus.POSITIVE_NOTIFICATION_SENT, chcId));
//				log.debug("CCR-OUTBOUND FTS POSITIVE_NOTIFY_FLOW sent to sender BA {}", getBaUrl(mssMessage.getLocalBaId()));
//				// event POSNotificationSent
//				sendEvent(mssMessage, ChcEvent.POSITIVE_NOTIFICATION_SENT, ChcEvent.ORC, ChcEvent.CSW, null, null, false);
//				
//			}else {
//				transactionTemplate.executeWithoutResult( t -> repository.setStatusAndSubStatus(ClientMessageStatus.COMPLETE, ClientMessageSubStatus.NEGATIVE_NOTIFICATION_SENT, chcId));
//				log.debug("CCR-OUTBOUND FTS NEGATIVE_NOTIFY_FLOW sent to sender BA {}", getBaUrl(mssMessage.getLocalBaId()));
//				// event NEGNotificationSent
//				sendEvent(mssMessage, ChcEvent.NEGATIVE_NOTIFICATION_SENT, ChcEvent.CCR, ChcEvent.CSW, null, null, false);
//				
//				// TODO popolare oggetto error descriptor
//				
//			}
//		}catch (ChcStubException | ChcException e) {
//			transactionTemplate.executeWithoutResult( t -> repository.setStatus(ClientMessageStatus.TO_RETRY, chcId));
//
//		}
//	}
//	
}
