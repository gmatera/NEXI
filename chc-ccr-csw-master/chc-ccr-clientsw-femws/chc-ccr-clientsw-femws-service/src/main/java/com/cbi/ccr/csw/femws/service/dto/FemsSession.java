package com.cbi.ccr.csw.femws.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FemsSession {

	//ID
	private String id;
	private String prefixEnvelope;
	private String localNameEnvelope;
	
	//SoapVersion11
	private boolean soapVersion11;
	// HeaderWs
	private FemsWsHeaderIn headerWs;
	private byte[] stream;
	// CONTENT_TYPE
	private String contentType;
	// RequestPath
	private String requestPath;

	private String soapAction;
	;
	// UdrAlgo
	private String udrAlgo;
	// AB_HOST_NAME
	private String abHostName;
	//CLIENT_FEMS_WS
	private String clientFemWs;
	//CLIENT_FENG_ID
	private String clientFengId;
	// START_GMT_CHAR
	private String startGmtChar;
	//START_TZ
	private String startTz;
	//REQST_GMT_CHAR
	private String requestGMTChar;
	// REQ_ATTACHMENTS_SZ
	private Integer requestAttachmentSize;
	// SUID
	private String  suid;
}
