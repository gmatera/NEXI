package com.cbi.ccr.csw.dashboard.routeinterface.dto;

import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.frw.api.dto.GenericDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigRouteInterfaceDTO extends GenericDTO {

	private String id;
	private String localBaId;
	private String remoteBaId;
	private RouteInterface interFace;
	private ServiceType service;



}
