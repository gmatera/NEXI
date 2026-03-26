package com.cbi.ccr.dto;

public class InboundControllerPath {
	public static final String BASE = "/ccr/api/v1";
	
	public static final String MESSAGE = "/message";
	//public static final String MESSAGE_ID = MESSAGE+"/{id}";
	
	public static final String FMS_MESSAGE = MESSAGE+"/fms";
	public static final String FMS_MESSAGE_ID = MESSAGE+"/fms/{id}";
	
	public static final String FTS_MESSAGE = MESSAGE+"/fts";
	public static final String FTS_MESSAGE_ID = MESSAGE+"/fts/{id}";
	
	public static final String MSS_MESSAGE = MESSAGE+"/mss";
	public static final String MSS_MESSAGE_ID = MESSAGE+"/mss/{id}";
	
	public static final String SERVICE_STATUS = "/serviceStatus";
	
	
	
	
	private InboundControllerPath() {
		// sonar not used
	}
}
