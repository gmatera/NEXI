package com.cbi.ccr.csw.mq.domain;

public enum SendStatusMQ {
	
	REJECTED("REJECTED"),
	CREATING("CREATING"),         // non utilizzato da MSS perchè non crea file
	CREATE_ERROR("CREATE ERROR"), // non utilizzato da MSS perchè non crea file
	SENDING ("SENDING"),
	LOCALLY_CONFIRMED("LOCALLY CONFIRMED"),
	REMOTELY_CONFIRMED ("REMOTELY CONFIRMED"),
	IN_ERROR ("IN ERROR"),
	SENT ("SENT"),
	CLEANABLE ("CLEANABLE");
	
	private String status;
	
	SendStatusMQ(String status){
		this.status = status;
	}
	
	public String getStatus() { 
        return status;
    }
	
}
