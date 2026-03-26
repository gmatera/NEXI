package com.cbi.ccr.csw.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cbi.ccr.csw.dto.fms.ServiceType;

public interface  ConfigRouteInterfaceRepository extends JpaRepository<ConfigRouteInterface, Long> ,JpaSpecificationExecutor<ConfigRouteInterface>{

	List<ConfigRouteInterface> findByLocalBaIdAndRemoteBaIdAndService(String localBaId, String remoteBaId, ServiceType service);
	
}
