package com.cbi.ccr.csw.mq.common.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFTSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFmsMQRepository;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFtsMQRepository;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMSSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMssMQRepository;
import com.cbi.ccr.csw.service.common.CommonConfigurationService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.exception.ChcException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonConfigurationServiceMQ extends CommonConfigurationService {
	
	@Getter
	@Value("${enable_debug_primitive_mq}")
	private boolean debugPrimitiveMq;

	@Autowired
	private ConfigurationFmsMQRepository fmsRepoMq;

	@Autowired
	private ConfigurationFtsMQRepository ftsRepoMq;

	@Autowired
	private ConfigurationMssMQRepository mssRepoMq;

	@Override
	@PostConstruct
	public void init() {
		
		super.init();
		
		if(debugPrimitiveMq) {
			log.info("MQ PRIMITIVE DEBUG ENABLE");
		}
	}

	

	public ConfigurationFMSMQ loadFMSMQConfiguration(String localBaId, String remoteBaId) throws ChcException {
		List<ConfigurationFMSMQ> configurations = fmsRepoMq.findByLocalBaIdAndRemoteBaId(localBaId, remoteBaId);
		if (configurations.isEmpty())
			configurations = fmsRepoMq.findByLocalBaIdAndRemoteBaId(localBaId, null);
		if (configurations.isEmpty())
			configurations = fmsRepoMq.findByLocalBaIdAndRemoteBaId(null, null);
		if (configurations.isEmpty())
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, localBaId, remoteBaId);
		return configurations.get(0);
	}

	public ConfigurationFTSMQ loadFTSMQConfiguration(String localBaId, String remoteBaId)
			throws ChcException {
		List<ConfigurationFTSMQ> configurations = ftsRepoMq.findByLocalBaIdAndRemoteBaId(localBaId, remoteBaId);
		if (configurations.isEmpty())
			configurations = ftsRepoMq.findByLocalBaIdAndRemoteBaId(localBaId, null);
		if (configurations.isEmpty())
			configurations = ftsRepoMq.findByLocalBaIdAndRemoteBaId(null, null);
		if (configurations.isEmpty())
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, localBaId, remoteBaId);
		return configurations.get(0);
	}

	// TODO MSS MQ
	public ConfigurationMSSMQ loadMSSMQConfiguration(String localBaId, String remoteBaId) throws ChcException {
		List<ConfigurationMSSMQ> configurations = mssRepoMq.findByLocalBaIdAndRemoteBaId(localBaId, remoteBaId);
		if (configurations.isEmpty())
			configurations = mssRepoMq.findByLocalBaIdAndRemoteBaId(localBaId, null);
		if (configurations.isEmpty())
			configurations = mssRepoMq.findByLocalBaIdAndRemoteBaId(null, null);
		if (configurations.isEmpty())
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, localBaId, remoteBaId);
		return configurations.get(0);
	}

	

}
