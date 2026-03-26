package com.cbi.ccr.csw.poller.db;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.poller.db.repository.CSWCommonPollerRepositoryDB;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.util.Color;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Profile("POLLER")
@Configuration
@EnableScheduling
@PropertySource({ "classpath:/poller.properties" })
@Import(value = {ConfigCswDomain.class, ConfigCommonService.class})
@ComponentScan(basePackageClasses = ConfigCswPollerDB.class)
@EnableJpaRepositories(basePackageClasses = CSWCommonPollerRepositoryDB.class, entityManagerFactoryRef = "entityManagerFactory")
public class ConfigCswPollerDB{
	
	static {
		ConfigCommon.addMessageResource("i18n/csw-poller");
	}
	
	public ConfigCswPollerDB() {
		ServiceRoles.getInstacne().addRole(ServiceRole.POLLER);
		ServiceRoles.getInstacne().addRole(ServiceRole.POLLER_ADDON);
	}
	
	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.POLLER)));
	}
}
