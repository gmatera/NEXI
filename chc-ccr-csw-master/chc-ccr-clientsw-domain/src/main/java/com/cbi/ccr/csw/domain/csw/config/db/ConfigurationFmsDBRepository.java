package com.cbi.ccr.csw.domain.csw.config.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationFmsDBRepository extends JpaRepository<ConfigurationFMSDB, Long>, JpaSpecificationExecutor<ConfigurationFMSDB>{
	List<ConfigurationFMSDB> findByLocalBaIdAndRemoteBaId(String localBaId, String remoteBaId);
}
