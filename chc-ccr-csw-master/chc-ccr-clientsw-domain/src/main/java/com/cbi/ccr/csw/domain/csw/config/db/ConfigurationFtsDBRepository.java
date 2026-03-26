package com.cbi.ccr.csw.domain.csw.config.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationFtsDBRepository extends JpaRepository<ConfigurationFTSDB, Long>, JpaSpecificationExecutor<ConfigurationFTSDB>{
	List<ConfigurationFTSDB> findByLocalBaIdAndRemoteBaId(String localBaId, String remoteBaId);
}
