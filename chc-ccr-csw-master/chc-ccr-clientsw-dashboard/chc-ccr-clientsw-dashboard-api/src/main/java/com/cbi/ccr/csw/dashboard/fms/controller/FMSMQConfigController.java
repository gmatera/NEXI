package com.cbi.ccr.csw.dashboard.fms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.fms.dto.ConfigFMSDTO;
import com.cbi.ccr.csw.dashboard.fms.dto.FMSConfigFilterDTO;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFmsMQRepository;

@RestController
@RequestMapping(ControllerPath.FMSMQ_PREFIX + ControllerPath.CONFIG)
public class FMSMQConfigController extends
		CommonConfigurationController<ConfigFMSDTO, FMSConfigFilterDTO, ConfigurationFmsMQRepository, ConfigurationFMSMQ> {

	public FMSMQConfigController() {
		super(ConfigFMSDTO.class, ConfigurationFMSMQ.class);
	}

}