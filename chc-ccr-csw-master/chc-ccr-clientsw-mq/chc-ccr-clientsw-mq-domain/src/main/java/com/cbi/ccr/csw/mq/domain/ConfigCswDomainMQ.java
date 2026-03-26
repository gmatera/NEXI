package com.cbi.ccr.csw.mq.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.ConfigCswDomainMssql;
import com.cbi.frw.persistence.ConfigJpa;

@Configuration
@EnableJpaRepositories(basePackageClasses = ConfigCswDomainMQ.class, entityManagerFactoryRef = "entityManagerFactory")
@EntityScan(basePackageClasses = ConfigCswDomainMQ.class)
@ComponentScan(basePackageClasses = ConfigCswDomainMQ.class)
@Import({ConfigCswDomainMssql.class, ConfigJpa.class})
public class ConfigCswDomainMQ{
	
	
}
