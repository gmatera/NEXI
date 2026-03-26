package com.cbi.ccr.csw.mq.domain.config;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationFtsMQRepository extends JpaRepository<ConfigurationFTSMQ, Long>, JpaSpecificationExecutor<ConfigurationFTSMQ>{
	List<ConfigurationFTSMQ> findByLocalBaIdAndRemoteBaId(String localBaId, String remoteBaId);
}
