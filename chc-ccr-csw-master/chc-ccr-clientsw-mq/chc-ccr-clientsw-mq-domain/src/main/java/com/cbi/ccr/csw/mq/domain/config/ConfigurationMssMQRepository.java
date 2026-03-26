package com.cbi.ccr.csw.mq.domain.config;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationMssMQRepository extends JpaRepository<ConfigurationMSSMQ, Long>, JpaSpecificationExecutor<ConfigurationMSSMQ>{
	List<ConfigurationMSSMQ> findByLocalBaIdAndRemoteBaId(String localBaId, String remoteBaId);
}
