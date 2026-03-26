package com.cbi.ccr.csw.dashboard.dto;

import com.cbi.ccr.csw.dashboard.dto.config.CommonConfigDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterDTO extends CommonConfigDTO{

	private String remoteBaID;
}
