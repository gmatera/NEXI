package com.cbi.ccr.csw.dashboard.globalproperties.dto;

import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalPropertiesFilterDTO extends PageableFilterDTO{

	private String propertyName;
	private boolean orderBylocaRemoteBa = false;

}
