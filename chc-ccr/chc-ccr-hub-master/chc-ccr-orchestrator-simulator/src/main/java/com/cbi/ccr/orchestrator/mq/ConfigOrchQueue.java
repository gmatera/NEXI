package com.cbi.ccr.orchestrator.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.cbi.frw.micros.MainApp;
import com.cbi.frw.rabbitmq.ConfigRabbit;

@Profile("orch")
@Configuration
@Import(value = {ConfigRabbit.class})
public class ConfigOrchQueue{
	
//	@Value("${orchestrator_queue}")
//	private String orchestratorQueue;
//	
//	@Value("${orchestrator_queue_durable}")
//	private Boolean orchestratorQueueDurable;
//	
//	@Value("${orchestrator_exchange}")
//	private String orchestratorExchange;
//
//	@Bean
//	Queue queueOrchestrator() {
//		return new Queue(orchestratorQueue, orchestratorQueueDurable);
//	}
//
//	@Bean
//	TopicExchange exchangeOrchestrator() {
//		return new TopicExchange(orchestratorExchange);
//	}
//
//	@Bean
//	Binding binding(Queue queueOrchestrator, TopicExchange exchangeOrchestrator) {
//		return BindingBuilder.bind(queueOrchestrator).to(exchangeOrchestrator).with("*.#");
//	}
//	
}
