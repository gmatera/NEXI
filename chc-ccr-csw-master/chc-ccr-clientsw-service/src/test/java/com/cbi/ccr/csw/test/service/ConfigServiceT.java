package com.cbi.ccr.csw.test.service;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.cbi.ccr.csw.domain.ConfigRouteInterfaceRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFmsDBRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFtsDBRepository;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.Msg;

@Configuration
@ComponentScan(basePackageClasses = ConfigServiceT.class)
@Import({ConfigCommonService.class, ConfigCommon.class})
public class ConfigServiceT {

//	@MockBean
//	PlatformTransactionManager manager;
//	
//	@MockBean
//	ConfigurationFmsRepository configFMSRepository;
//	
//	@MockBean
//	ConfigurationFtsRepository configFTSRepository;
//	
//	@MockBean
//	ConfigRouteInterfaceRepository configRouteInterfaceRepository;

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames(ConfigCommon.getMsgResources().toArray(new String[ConfigCommon.getMsgResources().size()]));
		source.setUseCodeAsDefaultMessage(false);
		Msg.setMessageSource(source);
		return source;
	}
}
