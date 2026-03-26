package com.cbi.ccr.csw.mq.domain;

public enum RecvStatusMQ {
	
	RECEIVING("RECEIVING"),
	IN_ERROR("IN ERROR"),  // non usato
	DELIVERED ("DELIVERED"),
	CLEANABLE ("CLEANABLE"),
	
	RECEIVE_ERROR("RECEIVE ERROR"),
	RECEIVED ("RECEIVED"),
	READING("READING"),
	READ_ERROR ("READ ERROR");
	
	private String status;
	
	RecvStatusMQ(String status){
		this.status = status;
	}
	
	public String getStatus() { 
        return status;
    }
	
}
