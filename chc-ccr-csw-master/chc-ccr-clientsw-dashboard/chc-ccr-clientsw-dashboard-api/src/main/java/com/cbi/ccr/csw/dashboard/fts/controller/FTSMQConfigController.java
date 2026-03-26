package com.cbi.ccr.csw.dashboard.fts.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.fts.dto.ConfigFTSDTO;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSConfigFilterDTO;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFTSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFtsMQRepository;

@RestController
@RequestMapping(ControllerPath.FTSMQ_PREFIX + ControllerPath.CONFIG)
public class FTSMQConfigController extends
		CommonConfigurationController<ConfigFTSDTO, FTSConfigFilterDTO, ConfigurationFtsMQRepository, ConfigurationFTSMQ> {

	public FTSMQConfigController() {
		super(ConfigFTSDTO.class, ConfigurationFTSMQ.class);
	}
}
