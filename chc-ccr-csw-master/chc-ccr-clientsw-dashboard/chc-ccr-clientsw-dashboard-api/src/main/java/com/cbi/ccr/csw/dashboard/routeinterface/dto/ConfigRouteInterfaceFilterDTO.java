package com.cbi.ccr.csw.dashboard.routeinterface.dto;

import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigRouteInterfaceFilterDTO extends PageableFilterDTO{

	private String localBaId;
	private String remoteBaId;
}
