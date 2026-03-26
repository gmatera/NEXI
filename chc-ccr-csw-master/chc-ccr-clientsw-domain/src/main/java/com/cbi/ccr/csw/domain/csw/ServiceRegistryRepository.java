package com.cbi.ccr.csw.domain.csw;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ServiceRegistryRepository extends JpaRepository<ServiceRegistry, String>{

	@Modifying
	@Query("update ServiceRegistry reg set reg.lastUpdate = ?1 where reg.id = ?2")
	int lastUpdate(LocalDateTime lastUpdate, String id);
	
}
