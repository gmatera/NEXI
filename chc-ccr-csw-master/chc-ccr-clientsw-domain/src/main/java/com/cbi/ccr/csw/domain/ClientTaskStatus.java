package com.cbi.ccr.csw.domain;

/**
 * NUOVO
 * fare alterTable
 *
 */
public enum ClientTaskStatus {
	NEW,
	SENDING,          // record letto dal db, ma non ancora processato dall'outobuond service. Non sarà mai più prelevato dal poller.
	WAITING_FOR_RETRY,
	// Deprecated!! ACCEPTED,           // record validato daal'outbound service, ma non ancora inviato al CCR.
	ON_HUB,             // messaggio inviato ed in carico al CCR.
	SUCCESS,
	FAILED
}
