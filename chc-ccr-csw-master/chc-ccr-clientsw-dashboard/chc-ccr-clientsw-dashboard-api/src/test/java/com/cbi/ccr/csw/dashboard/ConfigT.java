package com.cbi.ccr.csw.dashboard;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.Msg;
import com.cbi.frw.persistence.ConfigJpa;

@Configuration
@Import(value = {ConfigCswDashboardApi.class})
@ComponentScan(excludeFilters  = {@ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE, classes = {ConfigJpa.class})})
@EnableAutoConfiguration(exclude = { LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
public class ConfigT{
	
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames(ConfigCommon.getMsgResources().toArray(new String[ConfigCommon.getMsgResources().size()]));
		source.setUseCodeAsDefaultMessage(false);
		Msg.setMessageSource(source);
		return source;
	}

}
