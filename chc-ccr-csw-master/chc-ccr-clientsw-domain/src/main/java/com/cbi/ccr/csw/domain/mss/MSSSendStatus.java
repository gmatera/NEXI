package com.cbi.ccr.csw.domain.mss;

public enum MSSSendStatus {
	NEW_TRAFFIC(0, "NEW TRAFFIC"),
	INVALID_BA(1101, "INVALID BA"),
	INVALID_INTERFACE(1102, "INVALID INTERFACE"),
	MARSHALL_ERROR(1105, "MARSHALL ERROR"),
	MSG_SEND_REQUEST(1110, "MSG SEND REQUEST"),
	MSG_SEND_CONFIRM(1120, "MSG SEND CONFIRM"),
	MSG_SENT_CONFIRMED(1140, "MSG SENT CONFIRMED"),
	SENDING_ERROR(1190, "SENDING ERROR"),
	;

	private int stsCode;
	private String status;
	
	MSSSendStatus(int stsCode, String status) {
		this.stsCode = stsCode;
		this.status = status;
	}
	
	public int getStsCode() {
		return stsCode;
	}
	
	public String getStatus() {
		return status;
	}
	
}
