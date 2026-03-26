package com.cbi.ccr.csw.dashboard.globalproperties.dto;

import com.cbi.frw.api.dto.GenericDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalPropertiesDTO extends GenericDTO {

	private String id;
	private String propertyName;
	private String value;
	private String type;
	private Boolean mandatory;
	private String requiredMsg;
	private Integer minLength;
	private Integer maxLength;
	private String displayName;
}
