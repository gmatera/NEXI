package com.cbi.ccr.csw.app;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.cbi.ccr.csw.app.filter.LogFilter;
import com.cbi.ccr.csw.dashboard.ConfigCswDashboardApi;
import com.cbi.ccr.csw.db.inbound.ConfigCswInboundDB;
import com.cbi.ccr.csw.db.outbound.ConfigCswOutboundDB;
import com.cbi.ccr.csw.encryption.LocalEncryptionService;
import com.cbi.ccr.csw.femws.inbound.ConfigCswInboundFemws;
import com.cbi.ccr.csw.femws.outbound.ConfigCswOutboundFemws;
import com.cbi.ccr.csw.mq.inbound.ConfigCswInboundMQ;
import com.cbi.ccr.csw.mq.outbound.ConfigCswOutboundMQ;
import com.cbi.ccr.csw.poller.db.ConfigCswPollerDB;
import com.cbi.ccr.csw.poller.mq.ConfigPollerMq;
import com.cbi.ccr.csw.restful.outbound.ConfigCswOutboundRestful;
import com.cbi.ccr.csw.service.common.CcrAbstratctApp;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.frw.api.ConfigInternalApi;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.micros.MainApp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableWebMvc // used to enable AngularUI controllers
@Configuration
@ComponentScan(basePackageClasses = { CswAllApp.class })
@Import(value = { ConfigCommonService.class, ConfigCswInboundDB.class, ConfigCswOutboundDB.class,
		ConfigCswOutboundMQ.class, ConfigCswInboundMQ.class, ConfigCswPollerDB.class, ConfigPollerMq.class, ConfigCswDashboardApi.class,
		ConfigCswOutboundFemws.class, ConfigCswInboundFemws.class, ConfigCswOutboundRestful.class,
		ConfigInternalApi.class })
public class CswAllApp extends CcrAbstratctApp {

	public static void main(String[] args) {
		MainApp.main(CswAllApp.class);
		log.info(Color.g("CSW version 1.10.0"));
	}

	@Bean
	public LogFilter logFilter() {
		return new LogFilter();
	}

	

}
