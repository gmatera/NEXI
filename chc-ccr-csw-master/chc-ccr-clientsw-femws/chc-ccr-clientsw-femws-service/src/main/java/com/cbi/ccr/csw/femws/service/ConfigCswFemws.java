package com.cbi.ccr.csw.femws.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.service.common.ConfigCommonService;

@Configuration
@PropertySource({ "classpath:/femws.properties" })
@Import(value = { ConfigCswDomain.class, ConfigCommonService.class })
@ComponentScan(basePackageClasses = { ConfigCswFemws.class })
public class ConfigCswFemws {



}
