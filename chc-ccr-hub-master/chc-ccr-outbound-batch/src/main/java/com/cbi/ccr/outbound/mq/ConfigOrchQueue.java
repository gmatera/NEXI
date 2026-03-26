package com.cbi.ccr.outbound.mq;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.frw.rabbitmq.ConfigRabbit;

@Configuration
@Import(value = {ConfigRabbit.class})
public class ConfigOrchQueue{
	
}
