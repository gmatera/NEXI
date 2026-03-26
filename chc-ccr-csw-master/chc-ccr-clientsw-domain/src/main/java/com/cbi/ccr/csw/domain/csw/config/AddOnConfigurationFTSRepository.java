package com.cbi.ccr.csw.domain.csw.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddOnConfigurationFTSRepository extends JpaRepository<AddOnFTSConfiguration, Long>, JpaSpecificationExecutor<AddOnFTSConfiguration>{
	
	AddOnFTSConfiguration findByLocalBaIdAndRemoteBaId(String localBa, String remoteBa);

}
