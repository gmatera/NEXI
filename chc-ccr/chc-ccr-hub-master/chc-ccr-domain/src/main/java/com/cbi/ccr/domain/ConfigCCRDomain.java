package com.cbi.ccr.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cbi.frw.persistence.ConfigJpa;

@Configuration
@PropertySource({ "classpath:/ccr-domain.properties" })
@EnableJpaRepositories(basePackageClasses = ConfigCCRDomain.class, entityManagerFactoryRef = "entityManagerFactory")
@EntityScan(basePackageClasses = ConfigCCRDomain.class)
@Import(ConfigJpa.class)
public class ConfigCCRDomain {

}
