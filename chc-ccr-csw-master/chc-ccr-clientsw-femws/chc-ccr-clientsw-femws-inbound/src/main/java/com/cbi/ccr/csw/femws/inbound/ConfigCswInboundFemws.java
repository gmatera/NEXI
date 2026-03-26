package com.cbi.ccr.csw.femws.inbound;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.femws.service.ConfigCswFemws;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.frw.http.ConfigHttp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("SOAP_INBOUND_WORKER")
@Configuration
@PropertySource({ "classpath:/inbound-femws.properties" })
@Import(value = { ConfigCswDomain.class, ConfigCommonService.class, ConfigCswFemws.class, ConfigHttp.class })
@ComponentScan(basePackageClasses = { ConfigCswInboundFemws.class, HubEncryptionUtil.class })
public class ConfigCswInboundFemws {

	static {
		ConfigCommon.addMessageResource("i18n/inbound-femws");
	}

	protected ConfigCswInboundFemws() {
		ServiceRoles.getInstacne().addRole(ServiceRole.SOAP_INBOUND_WORKER);
	}
	
	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.SOAP_INBOUND_WORKER)));
	}

}
