package com.cbi.ccr.csw.dto.api.gateway;

public class ApiGatewayPath {

	private ApiGatewayPath() {}
	
	private static final String CHC = "/chc";
	private static final String B2B = "/b2b";
	
	public static final String API_GATEWAY_BASE_PATH = CHC + B2B;
	
	public static final String CRYPTO_HUB = "/cryptohub/getkey";
}
