package com.cbi.frw.ibmmq.test;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import com.cbi.frw.ibmmq.ConfigIbmMq;

@Configuration
@ComponentScan(basePackageClasses = ConfigT.class)
@Import(value = {ConfigIbmMq.class})
public class ConfigT implements JmsListenerConfigurer {
	
	@Bean
	public TestBean testBean() {
		return new TestBean();
	}
	
//	@Override
//    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
//        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
//        endpoint.setId("myJmsEndpoint");
//        endpoint.setDestination("DEV.QUEUE.1");
//        endpoint.setMessageListener(msg -> {
//        	  TestBean.getInstance().message = msg;
//        });
//        registrar.registerEndpoint(endpoint);
//    }
	
	
	
	public static class TestBean{
		
		private static TestBean instance = new TestBean();
		
		public Object message;
		
		public static TestBean getInstance() {
			return instance;
		}
	}

	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
		// TODO Auto-generated method stub
		
	}
}
