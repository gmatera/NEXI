package com.cbi.ccr.csw.femws.outbound;

/**
 * Stato della chiusura di una sessione
 * 
 * @author TFSCostabileMichele
 *
 */
public enum CloseOutcome {
	CLI_BADLAU(2), SRV_BADLAU(12), OTHER(7);

	private int code;

	CloseOutcome(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

//	/**
//	 * estrae il codice di ritorno da un messaggio e lo traduce
//	 * 
//	 * @param msg
//	 *            il messaggio di cui decodificare il codice
//	 * @return il codice decodificato, oppure OTHER
//	 */
//	public static int getCode(FemsRequestCompatibility msg) {
//		CloseOutcome outcome = msg.getFemsSession().getCLOSE_OUTCOME();
//		if (outcome != null) {
//			return outcome.getCode();
//		} else {
//			return OTHER.getCode();
//		}
//	}
}
