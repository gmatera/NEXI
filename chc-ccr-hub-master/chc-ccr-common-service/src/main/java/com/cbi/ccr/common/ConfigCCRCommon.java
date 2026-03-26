package com.cbi.ccr.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.http.ConfigHttp;

@Configuration
@ComponentScan(basePackageClasses = {ConfigCCRCommon.class})
@Import(value = {ConfigCommon.class, ConfigHttp.class})
@PropertySource({ "classpath:/ccr-common-service.properties" })
public class ConfigCCRCommon {
	
	protected ConfigCCRCommon() {
		// sonar
	}
	static {
		ConfigCommon.addMessageResource("i18n/ccr-common");
	}

}
