package com.cbi.ccr.csw.dto;

public class CSWInboundControllerPath {
	public static final String BASE = "/csw/inbound/internal/api/v1";
	
	public static final String RECEIVE_FMS = "/receive/fms";
	public static final String RECEIVE_FTS = "/receive/fts";
	public static final String RECEIVE_MSS = "/receive/mss";

	
	public static final String FMS_RECEIVE_ID = RECEIVE_FMS + "/{phyMsgId}";
	public static final String FTS_RECEIVE_ID = RECEIVE_FTS + "/{phyMsgId}";
	public static final String MSS_RECEIVE_ID = RECEIVE_MSS + "/{phyMsgId}";
	
	public static final String NOTIFICATION =  "/notify";
	
	public static final String SOAP_INBOUND =  "/soap";
	
	private CSWInboundControllerPath() {
		// sonar not used
	}
}
