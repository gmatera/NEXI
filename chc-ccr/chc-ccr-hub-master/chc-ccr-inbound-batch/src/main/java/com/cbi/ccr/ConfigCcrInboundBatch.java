package com.cbi.ccr;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.ccr.inbound.ConfigCCRInbound;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.ConfigEncryption;
import com.cbi.repo.stub.ConfigRepoStub;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = ConfigCcrInboundBatch.class)
@Import(value = {ConfigCCRInbound.class, ConfigEncryption.class, ConfigRepoStub.class})
public class ConfigCcrInboundBatch{
	
	
	@PostConstruct
	public void init() throws Exception {
		log.info(Color.g("Inbound Batch controller started"));
	}
	
	
}
