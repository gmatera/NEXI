package com.cbi.ccr.csw.femws.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FemsWsOutboundRequestDTO implements Serializable{

	private static final long serialVersionUID = 5789161583330599127L;
	public static final String SESSION_WS = "sessionWs";
	private Map<String, String> headers;
	private String payload;
	
	private String clientNetCode;
	private String serverNetCode;
	private String applCode;
	private String env;
	private String udr;
}
