package com.cbi.ccr.poller;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cbi.ccr.common.ConfigCCRCommon;
import com.cbi.ccr.domain.ConfigCCRDomain;
import com.cbi.frw.api.ConfigInternalApi;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.repo.stub.ConfigRepoStub;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
@ComponentScan(basePackageClasses = {ConfigCcrPoller.class, HubEncryptionUtil.class})
@Import(value = {ConfigCommon.class, ConfigInternalApi.class, ConfigCCRDomain.class, ConfigRepoStub.class,  ConfigCCRCommon.class})
public class ConfigCcrPoller {

	@PostConstruct
	public void init() throws Exception {
		log.info(Color.g("CCR poller for retry started"));
	}
}



