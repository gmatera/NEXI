package com.cbi.ccr.csw.mq.outbound;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.mq.common.ConfigCswMQCommon;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveConfigurationLoader;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMqPrimitive;
import com.cbi.ccr.csw.mq.outbound.listener.MqListener;
import com.cbi.ccr.csw.service.common.ConfigCommonService;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.frw.ibmmq.ConfigIbmMq;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("MQ")
@Configuration
@PropertySource({ "classpath:/outbound-mq.properties" })
@Import(value = {ConfigIbmMq.class, ConfigCswDomain.class, ConfigCommonService.class, ConfigCswMQCommon.class })
@ComponentScan(basePackageClasses = { ConfigCswOutboundMQ.class, HubEncryptionUtil.class })
public class ConfigCswOutboundMQ implements JmsListenerConfigurer {

	@Autowired
	private MqPrimitiveConfigurationLoader mqPrimitiveLoader;

	@Autowired
	private MqListener listener;

	protected ConfigCswOutboundMQ() {
		ServiceRoles.getInstacne().addRole(ServiceRole.MQ);
	}

	@Value("${outbound_mq_listener_concurrency}")
	private String outboundMqListenerConcurrency;
	
	@PostConstruct
	public void inti() {
		log.info(Color.g(String.format("################# %s profile active", ServiceRole.MQ)));
		log.info(Color.g(String.format("################# Stared MQ listener with concurrency: %s", outboundMqListenerConcurrency)));
	}
	
	@Override
	@DependsOn("mqPrimitiveLoader")
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {

		List<ConfigurationMqPrimitive> mqPrimitives = mqPrimitiveLoader.findAllPrimitive();
		if (mqPrimitives.isEmpty())
			log.info(Color.y("No Message Queues found in database, listeners will not be available."));
		else
			log.info(Color.g("Loaded {} queues..."), mqPrimitives.size());

		for (ConfigurationMqPrimitive config : mqPrimitives) {
			if (Boolean.TRUE.equals(config.getToLoad())) {
				SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
				endpoint.setId(config.getId().toString());
				endpoint.setDestination(config.getQueueName());
				endpoint.setMessageListener(listener);
				endpoint.setConcurrency(outboundMqListenerConcurrency);
				registrar.registerEndpoint(endpoint);
				log.info(Color.g("created listener for queue: {}"), config.getQueueName());
			}
		}

	}

}
