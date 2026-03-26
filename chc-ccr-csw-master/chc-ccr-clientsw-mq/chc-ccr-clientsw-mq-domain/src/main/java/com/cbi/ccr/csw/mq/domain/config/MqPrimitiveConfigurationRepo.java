package com.cbi.ccr.csw.mq.domain.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MqPrimitiveConfigurationRepo extends JpaRepository<ConfigurationMqPrimitive, Long>, JpaSpecificationExecutor<ConfigurationMqPrimitive>{

	
	ConfigurationMqPrimitive findConfigByPrimitiveContaining(String primitive);
}
