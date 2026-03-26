package com.cbi.ccr.csw.domain.csw.config.restful;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationRestfulWsRepository extends JpaRepository<ConfigurationRestfulWs, Long>, JpaSpecificationExecutor<ConfigurationRestfulWs>{
	List<ConfigurationRestfulWs> findByBaId(String baId);
}
