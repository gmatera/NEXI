package com.cbi.ccr.csw.inbound.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.encryption.HubEncryptionUtil;

@Configuration
@Import(value = {ConfigCommon.class, ConfigCswDomain.class})
@ComponentScan(basePackageClasses = {ConfigCswInboundCommon.class, HubEncryptionUtil.class})
@PropertySource({ "classpath:/inbound-common.properties" })
public class ConfigCswInboundCommon {
	static {
		ConfigCommon.addMessageResource("i18n/cswinbound");
	}
	
	protected ConfigCswInboundCommon() {
		ServiceRoles.getInstacne().addRole(ServiceRole.BATCH_INBOUND_WORKER);
	}
}
