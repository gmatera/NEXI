package com.cbi.ccr.csw.femws.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class FemsConfiguration {

//    // Constants
    public static final String TYPE_CLIENT = "client";
    public static final String TYPE_SERVER = "server";

	@Value("${femswsName}")
	private String femswsName; // FEMSWSID0002 
	
	@Value("${fengId}")
	private String fengId; // nomemacchina
	
	@Value("${femsWsId}")
	private String femsWsId; // FEMSWSID0002
	
	@Value("${femswsHeaderHandler}")
	private Boolean femswsHeaderHandler;
 
//	@Value("${proxyHost}")
//	private String proxyHost;
//	
//	@Value("${proxyPort}")
//	private String proxyPort;
	
	@Value("${traceBody}")
	private Boolean traceBody;
		
	@Value("${maxMessageSizeAllowed}")
	private Integer maxMessageSizeAllowed; // (dimensione massima in MB accettata per i messaggi SOAP)
}
