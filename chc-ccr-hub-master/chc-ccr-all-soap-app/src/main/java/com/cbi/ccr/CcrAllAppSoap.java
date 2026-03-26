package com.cbi.ccr;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.ccr.common.filter.LogFilter;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.micros.MainApp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableEurekaClient
@Configuration
@ComponentScan(basePackageClasses = CcrAllAppSoap.class)
@Import(value = {ConfigCcrInboundSoap.class, ConfigCcrOutboundSoap.class})
public class CcrAllAppSoap extends MainApp{
	
	public static void main(String[] args) {
		MainApp.main( CcrAllAppSoap.class);
		log.info(Color.g("CCR v 1.5.0-SNAPSHOT v2"));
	}
	
	@Bean
    public LogFilter logFilter() {
        return new LogFilter();
    }
	
	@Override
	public void run(String... args) throws Exception {
		// not used
	}

	@Override
	protected void run() {
		// not used
		
	}

	@Override
	protected void shutdown() {
		// not used
		
	}
	
}
