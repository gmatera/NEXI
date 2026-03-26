package com.cbi.ccr;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.ConfigEncryption;
import com.cbi.repo.stub.ConfigRepoStub;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = ConfigCcrOutboundBatch.class)
@Import(value = {ConfigEncryption.class, ConfigRepoStub.class})
public class ConfigCcrOutboundBatch{
	
	static {
		ConfigCommon.addMessageResource("i18n/ccroutbound-batch");
	}
	
	@PostConstruct
	public void init() throws Exception {
		log.info(Color.g("Outbound Batch controller started"));
	}
	

}
