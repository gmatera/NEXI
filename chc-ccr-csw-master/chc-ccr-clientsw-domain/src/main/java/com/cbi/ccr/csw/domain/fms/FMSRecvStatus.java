package com.cbi.ccr.csw.domain.fms;

public enum FMSRecvStatus {
	
	RECEIVING(0,0, "RECEIVING"),
	RECEIVED(1,6, "RECEIVED"),
	RECEPTION_FAILED (3,0, "RECEPTION FAILED"),
	;
	
	private int stCodeBA;
	private int stCodeSync;
	private String statusBA;
	
	FMSRecvStatus(int stCodeBA, int stCodeSync, String statusBA){
		this.stCodeBA = stCodeBA;
		this.stCodeSync = stCodeSync;
		this.statusBA = statusBA;
	}
	
	public int getStCodeBA() { 
        return stCodeBA;
    }
	
	public int getStCodeSync() { 
        return stCodeSync;
    }
	public String getStatusBA() { 
        return statusBA;
    }
	
}
