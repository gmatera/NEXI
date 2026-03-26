package com.cbi.ccr.csw.dashboard.mqprimitive.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.mqprimitive.dto.MQPrimitiveDTO;
import com.cbi.ccr.csw.dashboard.mqprimitive.dto.MQPrimitiveFilterDTO;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMqPrimitive;
import com.cbi.ccr.csw.mq.domain.config.MqPrimitiveConfigurationRepo;



@RestController
@RequestMapping(ControllerPath.MQ_PRIMITIVE_PREFIX+ControllerPath.CONFIG)
public class MqPrimitiveController extends CommonConfigurationController<MQPrimitiveDTO, MQPrimitiveFilterDTO, MqPrimitiveConfigurationRepo, 
ConfigurationMqPrimitive> {
	
public MqPrimitiveController() {
	super(MQPrimitiveDTO.class, ConfigurationMqPrimitive.class);
}

}