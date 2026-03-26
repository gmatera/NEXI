package com.cbi.ccr.csw.dashboard.femws.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.femws.dto.ConfigFemwSDTO;
import com.cbi.ccr.csw.dashboard.femws.dto.FemwsConfigFilterDTO;
import com.cbi.ccr.csw.domain.csw.config.femws.ConfigurationFemsWs;
import com.cbi.ccr.csw.domain.csw.config.femws.ConfigurationFemsWsRepository;

@RestController
@RequestMapping(ControllerPath.FEMWS_PREFIX + ControllerPath.CONFIG)
public class FemwsConfigController extends
		CommonConfigurationController<ConfigFemwSDTO, FemwsConfigFilterDTO, ConfigurationFemsWsRepository, ConfigurationFemsWs> {

	public FemwsConfigController() {
		super(ConfigFemwSDTO.class, ConfigurationFemsWs.class);
	}

}