package com.cbi.ccr.csw.dashboard.femws.dto;

import com.cbi.ccr.csw.dashboard.dto.config.CommonConfigDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigFemwSDTO extends CommonConfigDTO {
	
	private String baId;
	private String wsSoapAction;
	private String webServerUrl;

}
