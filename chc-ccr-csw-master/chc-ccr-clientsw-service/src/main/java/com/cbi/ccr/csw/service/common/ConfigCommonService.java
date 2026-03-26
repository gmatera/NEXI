package com.cbi.ccr.csw.service.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import com.cbi.ccr.csw.encryption.LocalEncryptionConfig;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.encryption.ConfigEncryption;
import com.cbi.frw.http.ConfigHttp;

@Configuration
@ComponentScan(basePackageClasses = {ConfigCommonService.class})
@Import(value = {ConfigEncryption.class, ConfigHttp.class, LocalEncryptionConfig.class})
@PropertySource({ "classpath:/csw_service.properties"})
public class ConfigCommonService {

	static {
		ConfigCommon.addMessageResource("i18n/csw-service");
	}
	
}
