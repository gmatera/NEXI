package com.cbi.ccr.csw.retention;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.service.common.ConfigCommonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("POLLER")
@Configuration
@EnableScheduling
@PropertySource({ "classpath:/retention.properties" })
@Import(value = {ConfigCswDomain.class, ConfigCommonService.class})
@ComponentScan(basePackageClasses = ConfigRetention.class)
//@EnableJpaRepositories(basePackageClasses = CSWCommonPollerRepositoryDB.class, entityManagerFactoryRef = "entityManagerFactory")
public class ConfigRetention {

}
