package com.cbi.ccr.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.ccr.ConfigCcrInboundBatch;
import com.cbi.ccr.ConfigCcrInboundSoap;
import com.cbi.ccr.ConfigCcrOutboundBatch;
import com.cbi.ccr.ConfigCcrOutboundSoap;
import com.cbi.ccr.common.filter.LogFilter;
import com.cbi.ccr.dto.InboundControllerPath;
import  com.cbi.ccr.poller.ConfigCcrPoller;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.ConfigEncryption;
import com.cbi.frw.http.HttpUtilsNoProxy;
import com.cbi.frw.http.jwt.filter.JwtFilter;
import com.cbi.frw.micros.MainApp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableEurekaClient
@Configuration
@ComponentScan(basePackageClasses = CcrAllApp.class)
@Import(value = {ConfigCcrInboundBatch.class, ConfigCcrOutboundBatch.class, 
		ConfigCcrInboundSoap.class, ConfigCcrOutboundSoap.class,
		ConfigCcrPoller.class, ConfigEncryption.class})
public class CcrAllApp extends MainApp{
	
	@Autowired
	private ApplicationContext appContext; 
	
	@Autowired
	private HttpUtilsNoProxy httpUtilsNoProxy;
	
	@Value("${enable_jwt_token_validation}")
	private boolean enableJWTokenValidation;
	
	@Value("${jwt_scope_validation}")
	private String jwtScopeValidation;

	@Value("${zuul_certificate_url}")
	private String zuulSignatureCertificateUrl;
	
	public static void main(String[] args) {
		MainApp.main( CcrAllApp.class);
		log.info(Color.g("CCR v 1.5.0-SNAPSHOT v2"));
	}
	
	@Bean
    public LogFilter logFilter() {
        return new LogFilter();
    }
	
	@Bean
	public FilterRegistrationBean<JwtFilter> jwtFilter() {
	    FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
	    registrationBean.setFilter(new JwtFilter(
	    		enableJWTokenValidation,
	    		jwtScopeValidation,
	    		httpUtilsNoProxy,
	    		zuulSignatureCertificateUrl,
	    		appContext
	    		));
	    registrationBean.addUrlPatterns(
	    		InboundControllerPath.BASE + InboundControllerPath.FMS_MESSAGE + "/*",
	    		InboundControllerPath.BASE + InboundControllerPath.FTS_MESSAGE + "/*",
	    		InboundControllerPath.BASE + InboundControllerPath.MSS_MESSAGE + "/*"
	    		);
	    return registrationBean;
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
