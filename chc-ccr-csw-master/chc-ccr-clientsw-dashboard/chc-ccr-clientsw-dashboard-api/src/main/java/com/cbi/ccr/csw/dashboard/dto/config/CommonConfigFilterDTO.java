package com.cbi.ccr.csw.dashboard.dto.config;

import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonConfigFilterDTO extends PageableFilterDTO{

	// use the same property name of the entity for all fields
	
	private String id;
	private String interfaceType;
	private String localBaId;
	private String remoteBaId;
	
	private Boolean lauEnabled;
	
}
