package com.cbi.frw.ibmmq.test;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MqListener {
	
//	@JmsListener(destination = "DEV.QUEUE.1")
    public void listener(Object message) {
       log.info("message received {}",message);
       //do something
    }
}
