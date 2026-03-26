package com.cbi.ccr.csw.service.common;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.ServiceRegistry;
import com.cbi.ccr.csw.domain.csw.ServiceRegistryLogRepository;
import com.cbi.ccr.csw.domain.csw.ServiceRegistryRepository;
import com.cbi.ccr.csw.domain.csw.ServiceStatus;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.persistence.service.AbstractService;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LivenessService extends AbstractService {

	@Value("${client_id}")
	private String clientId;
	@Value("${client_hostname}")
	private String clientHostname;
	@Value("${server.port}")
	private Integer clientPort;
	
	@NonNull
	private Set<ServiceRole> activeRoles;

	private boolean running;
	private boolean initialized;
	
	@Autowired
	private ServiceRegistryRepository serviceRegistryRepository;

	@Value("${csw_service_status_update_interval}")
	private Long cswServiceStatusUpdateInterval;

	@Autowired
	private ApplicationContext appContext;
	
	public static class ServiceRoles{
		private static ServiceRoles instance = new ServiceRoles();
		@Getter
		private Set<String> activeRoles;
		
		private ServiceRoles() {
		}
		public static ServiceRoles getInstacne(){
			return instance;
		}
		public void addRole(ServiceRole serviceType) {
			if(activeRoles == null) {
				activeRoles = new HashSet<>(4);
			}
			activeRoles.add(serviceType.name());
		}
	}
	
	@PostConstruct
	public void init() {
		super.init();
	}
	
	public void registerServices() {

		try {
			final ServiceRegistry sr;
			Optional<ServiceRegistry> srOpt = serviceRegistryRepository.findById(clientId);
			if (!srOpt.isPresent()) {
				sr = new ServiceRegistry();
				sr.setId(clientId);
				sr.setGroupId("DEFAULT");
				sr.setLastUpdate(LocalDateTime.now());
				sr.setRoles(ServiceRoles.getInstacne().getActiveRoles());
				sr.setHostName(clientHostname);
				sr.setPort(clientPort);
				sr.setServiceStatus(ServiceStatus.ACTIVE);
				
				transactionTemplate.executeWithoutResult(t -> serviceRegistryRepository.save(sr));
			}else {
				// wait up to cswServiceStatusUpdateInterval to check if this service is already
				// live
//				log.info("Waiting to check concurrency...");
				boolean alreadyActive = true;
				for (int i = 0; i < 10; i++) {
					ServiceRegistry sr1 = srOpt.get();
					if (sr1.isAlive(cswServiceStatusUpdateInterval)) {
						log.info("Another instance seems active waiting...");
						Thread.sleep(4000);
					}else {
						alreadyActive = false;
						break;
					}
				}

				if (alreadyActive) {
					log.error("Another ClientSW with id:{} is already running. Abort", clientId);
					System.exit(SpringApplication.exit(appContext, () -> -1));
				} 
				
				ServiceRegistry sr1 = srOpt.get();
				sr1.setRoles(ServiceRoles.getInstacne().getActiveRoles());
				serviceRegistryRepository.save(sr1);
			}
			
			setInitialized(true);
			log.info("Running...");
			
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Scheduled(fixedDelayString = "${csw_service_status_update_interval}")
	public void update() {
		if(!isInitialized()) {
			return;
		}
		try {
			transactionTemplate.executeWithoutResult(t -> serviceRegistryRepository.lastUpdate(LocalDateTime.now(), clientId));
			setRunning(true);
		} catch (Exception e) {
			setRunning(false);
			log.error("error updating status", e);
		}
	}

	public synchronized boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	public synchronized boolean isInitialized() {
		return initialized;
	}

	public synchronized void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
