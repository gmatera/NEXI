package com.cbi.ccr.csw.db.inbound;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.cbi.ccr.csw.db.common.CommonConfigurationServiceDB;
import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.inbound.common.ConfigCswInboundCommon;
import com.cbi.ccr.csw.mq.inbound.ConfigCswInboundMQ;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.HubEncryptionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("BATCH_INBOUND_WORKER")
@Configuration
@Import(value = {ConfigCommon.class, ConfigCswDomain.class, ConfigCswInboundCommon.class, ConfigCswInboundMQ.class, ConfigCommonService.class})
@ComponentScan(basePackageClasses = {ConfigCswInboundDB.class, CommonConfigurationServiceDB.class})
@PropertySource({ "classpath:/inbound-db.properties"})
public class ConfigCswInboundDB {
	
	protected ConfigCswInboundDB() {
		ServiceRoles.getInstacne().addRole(ServiceRole.BATCH_INBOUND_WORKER);
	}
	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.BATCH_INBOUND_WORKER)));
	}
}
