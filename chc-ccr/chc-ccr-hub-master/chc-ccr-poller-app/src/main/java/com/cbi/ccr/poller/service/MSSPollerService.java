package com.cbi.ccr.poller.service;

import org.springframework.stereotype.Service;

import com.cbi.ccr.domain.MSSMessage;
import com.cbi.ccr.domain.MSSMessageRepository;
import com.cbi.ccr.orc.MSSOrchestratorService;
import com.cbi.ccr.outbound.service.MSSOutboundService;


//@Service
public class MSSPollerService extends PollerAbstractService<MSSMessage, MSSMessageRepository, MSSOrchestratorService, MSSOutboundService>{

	public MSSPollerService(MSSMessageRepository repo, MSSOrchestratorService orchestratorService,
			MSSOutboundService commonOutboundService) {
		super(repo, orchestratorService, commonOutboundService);
	}

//	@Autowired
//	CCRIndoundServiceMSS ccrInboundServiceMss;
//
//	@Autowired
//	MSSOutboundService mSSOutboundService;
//	
//	@Scheduled(fixedDelayString = "${fixedPollerDelay}")
//	public void pollMSS(){
//		super.pollerCommon();
//	}
	
//	@Override
//	protected List<MSSMessage> getMessagesList() {
//		return repo.findByStatus(ClientMessageStatus.TO_RETRY);
//	}
//	
//	@Override
//	protected void updateMessageAfterMaxRetry(MSSMessage message) {
//		if (message.getSubStatus() == ClientMessageSubStatus.SENDING_TO_REMOTE_BA) {
//			transactionTemplate.executeWithoutResult(
//					t -> repo.setSubStatus(ClientMessageSubStatus.NEG_NOTIFICATION_FROM_CSW, message.getId()));
//			mSSOutboundService.sendNotificationToOrchestrator(message, false);
//
//		} else {
//			transactionTemplate.executeWithoutResult(
//					t -> repo.setStatusAndLog(ClientMessageStatus.FAILED, "Retry Count exausted", message.getId()));
//		}
//	}
	
//	@Override
//	protected void getSubStatusAndProcessMessage(MSSMessage msg){
//		switch (msg.getSubStatus()) {
//		case SAVED_INTO_REPO:
//			log.info("Poller MSS send To Orchestrator");
//			ccrInboundServiceMss.sendToOrchestrator(msg);
//			break;
//		case SENDING_TO_REMOTE_BA:
//			log.info("Poller MSS send MSS TO Remote BA");
//			mSSOutboundService.sendMSSToRemoteBA(msg);
//			break;
//		case POS_NOTIFICATION_FROM_CSW:
//			log.info("Poller MSS send Positive Notification To Orchestrator");
//			mSSOutboundService.sendNotificationToOrchestrator(msg,true);
//			break;
//		case NEG_NOTIFICATION_FROM_CSW:
//			log.info("Poller MSS send Negative Notification To Orchestrator");
//			mSSOutboundService.sendNotificationToOrchestrator(msg,false);
//			break;
//		case POS_NOTIFICATION_FROM_ORCH:
//			log.info("Poller MSS send and process positive Notification");
//			mSSOutboundService.processAndForwardNotification(msg.getChcId(), true);
//			break;
//		case NEG_NOTIFICATION_FROM_ORCH:
//			log.info("Poller MSS send and process positive Notification");
//			mSSOutboundService.processAndForwardNotification(msg.getChcId(), false);
//			break;
//		default:
//			break;
//		}
//	}
//	
	
}
