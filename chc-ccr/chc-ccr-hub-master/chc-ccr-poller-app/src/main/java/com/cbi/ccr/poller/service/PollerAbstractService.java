package com.cbi.ccr.poller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.cbi.ccr.domain.ClientMessageStatus;
import com.cbi.ccr.domain.ClientMessageSubStatus;
import com.cbi.ccr.domain.CommonEntity;
import com.cbi.ccr.domain.CommonMessageRepository;
import com.cbi.ccr.orc.OrchestratorService;
import com.cbi.ccr.outbound.service.CommonOutboundService;
import com.cbi.frw.persistence.service.AbstractService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PollerAbstractService<E extends CommonEntity, R extends CommonMessageRepository<E>, 
	O extends OrchestratorService<E,?,R>, S extends CommonOutboundService<E,R,?,O>> extends AbstractService{

	protected R repo;
	protected O orchestratorService;
	private S commonOutboundService;
	
	@Value("${max_retry_attempts}")
	protected int maxRetryAttempts;
	
	protected PollerAbstractService(R repo, O orchestratorService, S commonOutboundService) {
		this.repo = repo;
		this.orchestratorService = orchestratorService;
		this.commonOutboundService = commonOutboundService;
	}

//	@Scheduled(fixedDelayString = "${fixedPollerDelay}")
	public void pollMSS(){
		pollerCommon();
	}
	
	private void pollerCommon(){

		List<E> messages = getMessagesList();
		
		for (E msg : messages) {
			if(msg.getRetryCounter() != null && msg.getRetryCounter() >= maxRetryAttempts) {
				updateMessageAfterMaxRetry(msg);
			}else {
				int retryCounter = (msg.getRetryCounter() == null) ? 1 : msg.getRetryCounter() + 1;
				
				transactionTemplate.executeWithoutResult(t -> {
					msg.setRetryCounter(retryCounter); 
					repo.save(msg);
				});
			    getSubStatusAndProcessMessage(msg);
			}

		}

	}
	
	private void updateMessageAfterMaxRetry(E message) {
		if (message.getSubStatus() == ClientMessageSubStatus.SENDING_TO_REMOTE_BA) {
			transactionTemplate.executeWithoutResult(
					t -> repo.setSubStatus(ClientMessageSubStatus.NEG_NOTIFICATION_FROM_CSW, message.getId()));
			orchestratorService.outboundSendNotificationToOrchestrator(message, false);

		} else {
			transactionTemplate.executeWithoutResult(
					t -> repo.setStatusAndLog(ClientMessageStatus.FAILED, "Retry Count exausted", message.getId()));
		}
	}
	
	private List<E> getMessagesList() {
		// TODO limit the number of rows
		return repo.findAllByStatus(ClientMessageStatus.COMPLETE);
	}
	
	private void getSubStatusAndProcessMessage(E msg){
		switch (msg.getSubStatus()) {
		case SAVED_INTO_REPO:
			log.info("Poller FMS send To Orchestrator");
			orchestratorService.inboundSendToOrchestrator(msg);
			break;
		case SENDING_TO_REMOTE_BA:
			log.info("Poller FMS send FMS TO Remote BA");
			commonOutboundService.sendOrResendToRemoteBA(msg);
			break;
		case POS_NOTIFICATION_FROM_CSW:
			log.info("Poller FMS send Positive Notification To Orchestrator");
			orchestratorService.outboundSendNotificationToOrchestrator(msg, false);
			break;
		case NEG_NOTIFICATION_FROM_CSW:
			log.info("Poller FMS send Negative Notification To Orchestrator");
			orchestratorService.outboundSendNotificationToOrchestrator(msg, false);
			break;
		case POS_NOTIFICATION_FROM_ORCH:
			log.info("Poller FMS send and Process Positive Notification To Orchestrator");
			commonOutboundService.processAndForwardNotification(msg.getChcId(), null, true);
			break;
		case NEG_NOTIFICATION_FROM_ORCH:
			log.info("Poller FMS send and process negative Notification");
			commonOutboundService.processAndForwardNotification(msg.getChcId(), null, false);
			break;
		default:
			break;
		}
	}

}
