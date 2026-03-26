package com.cbi.ccr.csw.dashboard.mss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.mss.dto.ConfigMSSDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSConfigFilterDTO;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMSSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMssDbRepository;
import com.cbi.ccr.csw.domain.mss.MSSSendDBRepository;

@RestController
@RequestMapping(ControllerPath.MSSDB_PREFIX+ControllerPath.CONFIG)
public class MSSDBConfigController extends CommonConfigurationController<ConfigMSSDTO, MSSConfigFilterDTO, ConfigurationMssDbRepository, ConfigurationMSSDB> {
	
	public MSSDBConfigController() {
		super(ConfigMSSDTO.class, ConfigurationMSSDB.class);
	}
	
}
