package com.cbi.ccr.csw.dashboard.mss.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.mss.dto.ConfigMSSDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSConfigFilterDTO;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMSSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMssMQRepository;

@RestController
@RequestMapping(ControllerPath.MSSMQ_PREFIX + ControllerPath.CONFIG)
public class MSSMQConfigController extends
		CommonConfigurationController<ConfigMSSDTO, MSSConfigFilterDTO, ConfigurationMssMQRepository, ConfigurationMSSMQ> {

	public MSSMQConfigController() {
		super(ConfigMSSDTO.class, ConfigurationMSSMQ.class);
	}

}
