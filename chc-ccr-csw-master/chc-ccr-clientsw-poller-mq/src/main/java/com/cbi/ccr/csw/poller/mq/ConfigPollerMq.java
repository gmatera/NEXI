package com.cbi.ccr.csw.poller.mq;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.util.Color;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("MQ")
@Configuration
@Import(value = {ConfigCswDomain.class, ConfigCommonService.class})
@ComponentScan(basePackageClasses = ConfigPollerMq.class)
@EnableScheduling
@PropertySource({ "classpath:/pollermq.properties" })
public class ConfigPollerMq {

	public ConfigPollerMq() {
		ServiceRoles.getInstacne().addRole(ServiceRole.POLLER_MQ);
	}
	
	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.POLLER_MQ)));
	}
	
}
