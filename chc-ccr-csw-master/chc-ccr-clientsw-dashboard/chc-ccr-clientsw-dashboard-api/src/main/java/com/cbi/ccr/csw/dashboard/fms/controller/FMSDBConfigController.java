package com.cbi.ccr.csw.dashboard.fms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.fms.dto.ConfigFMSDTO;
import com.cbi.ccr.csw.dashboard.fms.dto.FMSConfigFilterDTO;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFMSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFmsDBRepository;


@RestController
@RequestMapping(ControllerPath.FMSDB_PREFIX+ControllerPath.CONFIG)
public class FMSDBConfigController extends CommonConfigurationController<ConfigFMSDTO, FMSConfigFilterDTO, ConfigurationFmsDBRepository, 
	ConfigurationFMSDB> {
	
	
	public FMSDBConfigController() {
		super(ConfigFMSDTO.class, ConfigurationFMSDB.class);
	}

	
	
}