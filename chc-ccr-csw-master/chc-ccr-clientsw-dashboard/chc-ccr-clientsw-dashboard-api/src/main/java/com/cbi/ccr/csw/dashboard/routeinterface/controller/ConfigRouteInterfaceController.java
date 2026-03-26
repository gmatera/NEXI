package com.cbi.ccr.csw.dashboard.routeinterface.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.routeinterface.dto.ConfigRouteInterfaceDTO;
import com.cbi.ccr.csw.dashboard.routeinterface.dto.ConfigRouteInterfaceFilterDTO;
import com.cbi.ccr.csw.domain.ConfigRouteInterface;
import com.cbi.ccr.csw.domain.ConfigRouteInterfaceRepository;

@RestController
@RequestMapping(ControllerPath.ROUTE_INTERFACE_PREFIX + ControllerPath.CONFIG)
public class ConfigRouteInterfaceController extends
		CommonConfigurationController<ConfigRouteInterfaceDTO, ConfigRouteInterfaceFilterDTO, ConfigRouteInterfaceRepository, ConfigRouteInterface> {

	public ConfigRouteInterfaceController() {
		super(ConfigRouteInterfaceDTO.class, ConfigRouteInterface.class);
	}

}