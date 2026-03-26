package com.cbi.ccr.csw.domain.fms;

public enum FMSSendStatus {
	SUBMITTED(0,0, "SUBMITTED"),
	INVALID_BA(0,1, "INVALID BA"),
	INVALID_INTERFACE (0,2, "INVALID INTERFACE"),
	ACCEPTED (1,5, "ACCEPTED"),
	SENDING (2,8, "SENDING"),
	SENDING_FAILURE (3,6, "SENDING FAILURE"),
	REQUEST_SEND_REJECTED (3,21, "REQUEST SEND REJECTED"),
	NOTIFY (4,15, "NOTIFY"),
	;
	
	private int stCodeBA;
	private int stCodeSync;
	private String statusBA;
	
	FMSSendStatus(int stCodeBA, int stCodeSync, String statusBA){
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
