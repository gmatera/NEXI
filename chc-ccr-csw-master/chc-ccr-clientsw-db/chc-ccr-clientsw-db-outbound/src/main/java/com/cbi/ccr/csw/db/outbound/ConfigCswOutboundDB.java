package com.cbi.ccr.csw.db.outbound;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.outbound.common.ConfigCswOutboundCommon;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.HubEncryptionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("BATCH_OUTBOUND_WORKER")
@Configuration
@Import(value = { ConfigCswDomain.class, ConfigCommonService.class, ConfigCswOutboundCommon.class })
@ComponentScan(basePackageClasses = { ConfigCswOutboundDB.class, HubEncryptionUtil.class })
public class ConfigCswOutboundDB{

	protected ConfigCswOutboundDB() {
		ServiceRoles.getInstacne().addRole(ServiceRole.BATCH_OUTBOUND_WORKER);
	}
	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.BATCH_OUTBOUND_WORKER)));
	}
}
