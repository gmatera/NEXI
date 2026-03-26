package com.cbi.ccr.csw.domain.csw.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GlobalPropertiesRepository extends JpaRepository<GlobalProperties, Long>, JpaSpecificationExecutor<GlobalProperties>{
	
	GlobalProperties findFirstByPropertyName(String propertyName);
	
	
}
