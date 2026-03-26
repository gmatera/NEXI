package com.cbi.ccr.domain;

public enum ClientMessageSubStatus {

	NEW, 
   //devo rifare tutto: senza new riparte da CSW
	SAVED_INTO_REPO, // devo reinviare a orch - retry send to orchestrator 
	SENT_TO_ORCHESTRATOR, 
	SENDING_TO_REMOTE_BA, //devo reinviare a remoteBA - retry send to remote ba
	POS_NOTIFICATION_FROM_CSW,  //devo reinviare notifica positiva - retry send notify
	NEG_NOTIFICATION_FROM_CSW,
	POS_NOTIFICATION_FROM_ORCH, 
	NEG_NOTIFICATION_FROM_ORCH,
	POSITIVE_NOTIFICATION_SENT,
	NEGATIVE_NOTIFICATION_SENT,
	NOTIFICATION_SENT_TO_ORCHESTRATOR,    
}
