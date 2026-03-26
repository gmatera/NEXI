package com.cbi.ccr.csw.dto;


public class CSWOutboundControllerPath {
	public static final String BASE = "/csw/outbound/internal/api/v1";

	public static final String BASE_MQ = "/csw/outbound/internal/api/v1/mq";
	
	public static final String SUBMIT_FMS = "/submit-fms";
	public static final String SUBMIT_FTS = "/submit-fts";
	public static final String SUBMIT_MSS = "/submit-mss";
		
//	public static final String SUBMIT = "/submit";
	public static final String LIVENESS = "/liveness";
	
	
	public static final String SOAP_OUTBOUND =  "/soap";
	public static final String REST_OUTBOUND =  "/restful/**";
	
	private CSWOutboundControllerPath() {
		// sonar not used
	}
}
