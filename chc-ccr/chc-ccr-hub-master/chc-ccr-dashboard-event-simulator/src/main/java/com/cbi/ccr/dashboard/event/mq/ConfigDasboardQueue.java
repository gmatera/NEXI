package com.cbi.ccr.dashboard.event.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.frw.rabbitmq.ConfigRabbit;

@Configuration
@Import(value = {ConfigRabbit.class})
public class ConfigDasboardQueue {

	@Value("${dashboard_queue}")
	private String dashboardQueue;
	
	@Value("${dashboard_queue_durable}")
	private Boolean dashboardQueueDurable;
	
	@Value("${dashboard_exchange}")
	private String dashboardExchange;

	@Bean
	Queue queueDashboard() {
		return new Queue(dashboardQueue, dashboardQueueDurable);
	}

	@Bean
	TopicExchange exchangeDashboard() {
		return new TopicExchange(dashboardExchange);
	}

	@Bean
	Binding binding(Queue queueDashboard, TopicExchange exchangeDashboard) {
		return BindingBuilder.bind(queueDashboard).to(exchangeDashboard).with("*.#");
	}
}
