package com.cbi.ccr.csw.domain.csw.config.femws;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationFemsWsRepository extends JpaRepository<ConfigurationFemsWs, Long>, JpaSpecificationExecutor<ConfigurationFemsWs>{
	List<ConfigurationFemsWs> findByBaId(String baId);
}
