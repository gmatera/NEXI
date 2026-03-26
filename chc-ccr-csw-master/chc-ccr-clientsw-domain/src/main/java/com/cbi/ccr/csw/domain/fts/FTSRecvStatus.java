package com.cbi.ccr.csw.domain.fts;

public enum FTSRecvStatus {
	
	GFT_ERROR(130, "GFT ERROR"),
	READ_REQUEST(500, "READ REQUEST"),
	READ_ERROR(502, "READ ERROR"),
	READ_FILE_DELIVERED(511, "READ FILE DELIVERED"),
	;
	
	private int stsCode;
	private String status;
	
	FTSRecvStatus(int stsCode, String status) {
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
