package com.cbi.ccr.csw.dashboard.fts.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.fts.dto.ConfigFTSDTO;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSConfigFilterDTO;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFTSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFtsDBRepository;

@RestController
@RequestMapping(ControllerPath.FTSDB_PREFIX+ControllerPath.CONFIG)
public class FTSDBConfigController extends CommonConfigurationController<ConfigFTSDTO, FTSConfigFilterDTO, ConfigurationFtsDBRepository, ConfigurationFTSDB>  {

	public FTSDBConfigController() {
		super(ConfigFTSDTO.class, ConfigurationFTSDB.class);
	}	
}
