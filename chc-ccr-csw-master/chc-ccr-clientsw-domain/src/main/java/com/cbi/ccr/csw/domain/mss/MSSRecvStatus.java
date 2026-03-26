package com.cbi.ccr.csw.domain.mss;


public enum MSSRecvStatus {
	MSG_CONFIRMED(1220, "MSG CONFIRMED"),
	MSG_RECEIVE_ERROR(1290, "MSG RECEIVE ERROR"),
	;

	private int stsCode;
	private String status;
	
	MSSRecvStatus(int stsCode, String status) {
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
