package com.cbi.ccr.csw.mq.domain.config;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationFmsMQRepository extends JpaRepository<ConfigurationFMSMQ, Long>, JpaSpecificationExecutor<ConfigurationFMSMQ>{
	List<ConfigurationFMSMQ> findByLocalBaIdAndRemoteBaId(String localBaId, String remoteBaId);
}
