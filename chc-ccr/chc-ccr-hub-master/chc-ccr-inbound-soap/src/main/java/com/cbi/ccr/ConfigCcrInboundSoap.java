package com.cbi.ccr;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.ccr.inbound.ConfigCCRInbound;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.util.Color;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = ConfigCcrInboundSoap.class)
@Import(value = {ConfigCCRInbound.class})
public class ConfigCcrInboundSoap{
	
	static {
		ConfigCommon.addMessageResource("i18n/ccroutbound-soap");
	}
	
	@PostConstruct
	public void init() throws Exception {
		log.info(Color.g("Inbound SOAP controller started"));
	}
	
	
}
