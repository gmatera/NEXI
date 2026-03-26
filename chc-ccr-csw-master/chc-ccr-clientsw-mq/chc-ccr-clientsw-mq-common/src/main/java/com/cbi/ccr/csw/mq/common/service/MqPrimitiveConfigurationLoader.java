package com.cbi.ccr.csw.mq.common.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.mq.domain.config.ConfigurationMqPrimitive;
import com.cbi.ccr.csw.mq.domain.config.MqPrimitiveConfigurationRepo;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.frw.common.exception.ChcException;

import lombok.Getter;

@Service
public class MqPrimitiveConfigurationLoader {
	
	@Autowired
	private MqPrimitiveConfigurationRepo mqPrimitiveRepo;
	
	
	public List<ConfigurationMqPrimitive> findAllPrimitive() {
		return mqPrimitiveRepo.findAll();
	}
	
	public String getQueueByPrimitive(String primitive) throws ChcException {
		ConfigurationMqPrimitive queue = mqPrimitiveRepo.findConfigByPrimitiveContaining(primitive);
		if(queue == null)
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, String.format("Primitive %s not found", primitive));
		return queue.getQueueName();
	}
	
}
