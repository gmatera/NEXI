package com.cbi.ccr.csw.domain;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("MSSQL")
@Configuration
@PropertySource({ "classpath:/csw-domain-mssql.properties" })
public class ConfigCswDomainMssql {

	@PostConstruct
	public void init() {
		log.info("Added MS-SQl orm support");
	}
}
