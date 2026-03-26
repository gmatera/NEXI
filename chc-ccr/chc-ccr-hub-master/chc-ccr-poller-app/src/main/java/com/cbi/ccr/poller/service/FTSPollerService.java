package com.cbi.ccr.poller.service;

import org.springframework.stereotype.Service;

import com.cbi.ccr.domain.FTSMessage;
import com.cbi.ccr.domain.FTSMessageRepository;
import com.cbi.ccr.orc.FTSOrchestratorService;
import com.cbi.ccr.outbound.service.FTSOutboundService;

import lombok.extern.slf4j.Slf4j;

//@Service
public class FTSPollerService extends PollerAbstractService<FTSMessage, FTSMessageRepository, FTSOrchestratorService, FTSOutboundService>{

		public FTSPollerService(FTSMessageRepository repo, FTSOrchestratorService orchestratorService,
				FTSOutboundService commonOutboundService) {
			super(repo, orchestratorService, commonOutboundService);
		}
		
//	
//	@Scheduled(fixedDelayString = "${fixedPollerDelay}")
//	public void poolFMS() {
//		super.pollerCommon();
//	}
	
//	@Override
//	protected List<FTSMessage> getMessagesList() {
//		return repo.findAllByStatus(ClientMessageStatus.TO_RETRY);
//	}
	
//	@Override
//	protected void updateMessageAfterMaxRetry(FTSMessage message) {
//		if (message.getSubStatus() == ClientMessageSubStatus.SENDING_TO_REMOTE_BA) {
//			transactionTemplate.executeWithoutResult(
//					t -> repo.setSubStatus(ClientMessageSubStatus.NEG_NOTIFICATION_FROM_CSW, message.getId()));
//			fTSOutboundService.sendNotificationToOrchestrator(message, false);
//
//		} else {
//			transactionTemplate.executeWithoutResult(
//					t -> repo.setStatusAndLog(ClientMessageStatus.FAILED, "Retry Count exausted", message.getId()));
//		}	
//	}
	
	
//	@Override
//	protected void getSubStatusAndProcessMessage(FTSMessage msg) {
//		switch (msg.getSubStatus()) {
//		case SAVED_INTO_REPO:
//			log.info("Poller FTS send To Orchestrator");
//			ccrInboundServiceFts.sendToOrchestrator(msg);
//			break;
//		case SENDING_TO_REMOTE_BA:
//			log.info("Poller FTS send FMS TO Remote BA");
//			fTSOutboundService.sendFTSToRemoteBA(msg);
//			break;
//		case POS_NOTIFICATION_FROM_CSW:  
//			log.info("Poller FTS send Positive Notification To Orchestrator");
//		    fTSOutboundService.sendNotificationToOrchestrator(msg, true);
//			break;
//		case NEG_NOTIFICATION_FROM_CSW:  
//			log.info("Poller FTS send Negative Notification To Orchestrator");
//		    fTSOutboundService.sendNotificationToOrchestrator(msg, false);
//			break;
//		case POS_NOTIFICATION_FROM_ORCH:  
//			log.info("Poller FTS send and process poitive Notification To Orchestrator");
//		    fTSOutboundService.processAndForwardNotification(msg.getChcId(), true);
//			break;
//		case NEG_NOTIFICATION_FROM_ORCH:
//			log.info("Poller FTS send and process negative Notification");
//			fTSOutboundService.processAndForwardNotification(msg.getChcId(), false);
//			break;
//		default:
//			break;
//		}
//	}



}
