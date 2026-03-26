package com.cbi.ccr.csw.dashboard.dto.config;

import com.cbi.frw.api.dto.GenericDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonConfigDTO extends GenericDTO{

	// use the same property name of the entity for all fields
	
	private String id;
	private String interfaceType;
	private String localBaId;
	private String remoteBaId;
	
	private Boolean lauEnabled;
	private String lauFormat;
	private String leftLauKey;
	private String rightLauKey;
	private String lauKey;

	
}
