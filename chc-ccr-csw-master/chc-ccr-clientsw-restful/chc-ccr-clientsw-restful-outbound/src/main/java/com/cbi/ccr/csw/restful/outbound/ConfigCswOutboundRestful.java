package com.cbi.ccr.csw.restful.outbound;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.frw.http.ConfigHttp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("REST_OUTBOUND_FACADE")
@Configuration
@PropertySource({ "classpath:/outbound-restful.properties" })
@Import(value = { ConfigCswDomain.class, ConfigCommonService.class, ConfigHttp.class})
@ComponentScan(basePackageClasses = { ConfigCswOutboundRestful.class, HubEncryptionUtil.class })
public class ConfigCswOutboundRestful {

	protected ConfigCswOutboundRestful() {
		ServiceRoles.getInstacne().addRole(ServiceRole.REST_OUTBOUND_FACADE);
	}

	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.REST_OUTBOUND_FACADE)));
	}
}
