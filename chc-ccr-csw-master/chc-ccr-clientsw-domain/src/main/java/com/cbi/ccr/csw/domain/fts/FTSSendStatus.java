package com.cbi.ccr.csw.domain.fts;

public enum FTSSendStatus {
	
	FILE_TO_BE_PROCESSED(90, "FILE TO BE PROCESSED"),
	INVALID_BA(91, "INVALID BA"),
	INVALID_INTERFACE(92, "INVALID INTERFACE"),
	MARSHALL_ERROR(95, "MARSHALL ERROR"),
	GFT_SENDING(103, "GFT SENDING"),
	FILE_LOAD_ERROR(106, "FILE LOAD ERROR"),
	FILE_LOAD_EMPTY(107, "FILE LOAD EMPTY"),
	CREATE_ERROR(203, "CREATE ERROR"),
	EXPORT_REQUEST(300, "EXPORT REQUEST"),
	EXPORT_ERROR(303, "EXPORT ERROR"),
	EXPORT_COMPLETE(305, "EXPORT COMPLETE"),
	;
	
	private int stsCode;
	private String status;
	
	FTSSendStatus(int stsCode, String status) {
		this.stsCode = stsCode;
		this.status = status;
	}

	public int getStsCode() {
		return  stsCode;
	}
	
	public String getStatus() {
		return  status;
	}
	
}
