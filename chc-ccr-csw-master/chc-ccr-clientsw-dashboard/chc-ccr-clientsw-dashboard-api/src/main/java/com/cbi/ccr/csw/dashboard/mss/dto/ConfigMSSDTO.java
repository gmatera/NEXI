package com.cbi.ccr.csw.dashboard.mss.dto;

import com.cbi.ccr.csw.dashboard.dto.config.CommonConfigDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigMSSDTO extends CommonConfigDTO {

	private Boolean sndCompletionAlgo;
	private Boolean rcvCompletionAlgo;
	private String rcvPrimConvFormat;

}
