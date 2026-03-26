package com.cbi.ccr.csw.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.jwt.JwtAuthTokenFilter;
import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.mq.domain.ConfigCswDomainMQ;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.common.util.Color;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("DASHBOARD")
@EnableWebSecurity
@Configuration
@ComponentScan(basePackageClasses = ConfigCswDashboardApi.class)
@PropertySource({ "classpath:/dash.properties"})
@Import(value = {ConfigCswDomainMQ.class,ConfigCswDomain.class,  ConfigCommonService.class, ConfigCommon.class})
public class ConfigCswDashboardApi {

	static {
		ConfigCommon.addMessageResource("i18n/dash");
	}
	
	protected ConfigCswDashboardApi() {
		ServiceRoles.getInstacne().addRole(ServiceRole.DASHBOARD);
	}

	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.DASHBOARD)));
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public FilterRegistrationBean<JwtAuthTokenFilter> lmiJwtFilter() {
	    FilterRegistrationBean<JwtAuthTokenFilter> registrationBean = new FilterRegistrationBean<>();
	    registrationBean.setFilter(new JwtAuthTokenFilter());
	    registrationBean.addUrlPatterns(ControllerPath.LMI+"/*");
	    return registrationBean;
	}
}
