package com.cbi.ccr.csw.domain.csw.config.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationMssDbRepository extends JpaRepository<ConfigurationMSSDB, Long>, JpaSpecificationExecutor<ConfigurationMSSDB>{
	List<ConfigurationMSSDB> findByLocalBaIdAndRemoteBaId(String localBaId, String remoteBaId);
}
