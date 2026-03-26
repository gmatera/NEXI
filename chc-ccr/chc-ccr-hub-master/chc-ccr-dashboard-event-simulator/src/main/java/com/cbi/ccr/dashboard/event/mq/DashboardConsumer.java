package com.cbi.ccr.dashboard.event.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DashboardConsumer {
	
	@RabbitListener(queues = "${dashboard_queue}")
	public void receiver(String fileBody) {
		log.info("CONSUMING MESSAGE: {}", fileBody );
	}
}
