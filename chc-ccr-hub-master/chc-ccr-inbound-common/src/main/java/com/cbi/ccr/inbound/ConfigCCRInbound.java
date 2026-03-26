package com.cbi.ccr.inbound;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.ccr.common.ConfigCCRCommon;
import com.cbi.ccr.domain.ConfigCCRDomain;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.http.ConfigHttp;
import com.cbi.frw.rabbitmq.ConfigRabbit;

@Configuration
@ComponentScan(basePackageClasses = {ConfigCCRInbound.class})
@Import(value = {ConfigCommon.class, ConfigCCRDomain.class, 
		ConfigRabbit.class, ConfigCCRCommon.class, ConfigHttp.class})
public class ConfigCCRInbound {
	
	protected ConfigCCRInbound() {
		// sonar 
	}
	
	static {
		ConfigCommon.addMessageResource("i18n/inbound");
	}
}
