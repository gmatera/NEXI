package com.cbi.ccr.csw.domain.csw.config;

import java.util.stream.Stream;

public enum PropertiesEnum {
	
	
	UPLOAD_QUEUE_NAME_GROUP_SIZE("UPLOAD_QUEUE_NAME_GROUP_SIZE","512"), 
	FMSRCV_IND("FMSRCV_IND","FMS.RCV.IND"),
	
	SIGN_PUBLIC_KEY("SIGN_PUBLIC_KEY", ""), 
	SIGN_PRIVATE_KEY("SIGN_PRIVATE_KEY", ""), 
	ENC_PUBLIC_KEY("ENC_PUBLIC_KEY", ""), 
	ENC_PRIVATE_KEY("ENC_PRIVATE_KEY", ""),
	
	USE_LOCAL_API_GATEWAY("USE_LOCAL_API_GATEWAY", ""),
	RETENTION_FMS_DAYS("RETENTION_FMS_DAYS", "5"),
	RETENTION_START_AT("RETENTION_START_AT", "01:00");
	
	
	private String label;
	private String defaults;
	
	PropertiesEnum(String label, String defaults) {
		this.label=label;
		this.defaults=defaults;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getDefault() {
		return defaults;
	}
	
	public static PropertiesEnum getEnumByLabel(String label) {
		if(label == null) {
			return null;
		}
		return Stream.of(PropertiesEnum.values())
				.filter(s -> s.getLabel().equals(label))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
}
