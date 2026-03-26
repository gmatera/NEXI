package com.cbi.ccr.csw.retention.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.GlobalProperties;
import com.cbi.ccr.csw.domain.csw.config.GlobalPropertiesRepository;

@Service
public class RetentionService {

	@Autowired
	private GlobalPropertiesRepository globalPropertiesRepository;
	
	@Autowired
	private MQRetentionService mqRetentionService;
	@Autowired
	private DBRetentionService dbRetentionService;
	
	@Value("${spring.profiles.active}")
	private String profiles; 
	
	@Scheduled(fixedDelayString = "${retention_fixed_poller_delay}")
	public void doIt() {
		
		
		List<GlobalProperties> list = globalPropertiesRepository.findAll();
		Iterator<GlobalProperties> iterator = list.iterator();
		while (iterator.hasNext()) {
			GlobalProperties globalProperties = iterator.next();
			if(!globalProperties.getPropertyName().startsWith("RETENTION_")) {
				iterator.remove();
			}
		}
		
		if(profiles.contains("MQ")) {
			mqRetentionService.doIt(list);
		}
		
		dbRetentionService.doIt(list);
	}
}
