package com.cbi.ccr.csw.db.common;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFMSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFTSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFmsDBRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFtsDBRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMSSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMssDbRepository;
import com.cbi.ccr.csw.service.common.CommonConfigurationService;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.frw.common.exception.ChcException;


@Service
public class CommonConfigurationServiceDB extends CommonConfigurationService {
	
	@Autowired
	private ConfigurationFmsDBRepository fmsRepoDb;

	@Autowired
	private ConfigurationFtsDBRepository ftsRepoDb;

	@Autowired
	private ConfigurationMssDbRepository mssRepo;

	@Override
	@PostConstruct
	public void init() {
		super.init();
	}

	public ConfigurationFMSDB loadFMSConfiguration(String localBaId, String remoteBaId) throws ChcException {
		List<ConfigurationFMSDB> configurations = fmsRepoDb.findByLocalBaIdAndRemoteBaId(localBaId, remoteBaId);
		if (configurations.isEmpty())
			configurations = fmsRepoDb.findByLocalBaIdAndRemoteBaId(localBaId, null);
		if (configurations.isEmpty())
			configurations = fmsRepoDb.findByLocalBaIdAndRemoteBaId(null, remoteBaId);
		if (configurations.isEmpty())
			configurations = fmsRepoDb.findByLocalBaIdAndRemoteBaId(null, null);
		if (configurations.isEmpty())
			throw new ChcException(I18nService.ERR_INVALID_BA, localBaId, remoteBaId);
		return configurations.get(0);
	}

	public ConfigurationFTSDB loadFTSConfiguration(String localBaId, String remoteBaId)
			throws ChcException {
		List<ConfigurationFTSDB> configurations = ftsRepoDb.findByLocalBaIdAndRemoteBaId(localBaId, remoteBaId);
		if (configurations.isEmpty())
			configurations = ftsRepoDb.findByLocalBaIdAndRemoteBaId(localBaId, null);
		if (configurations.isEmpty())
			configurations = ftsRepoDb.findByLocalBaIdAndRemoteBaId(null, remoteBaId);
		if (configurations.isEmpty())
			configurations = ftsRepoDb.findByLocalBaIdAndRemoteBaId(null, null);
		if (configurations.isEmpty())
			throw new ChcException(I18nService.ERR_INVALID_BA, localBaId, remoteBaId);
		return configurations.get(0);
	}

	public ConfigurationMSSDB loadMSSConfiguration(String localBaId, String remoteBaId) throws ChcException {
		List<ConfigurationMSSDB> configurations = mssRepo.findByLocalBaIdAndRemoteBaId(localBaId, remoteBaId);
		if (configurations.isEmpty())
			configurations = mssRepo.findByLocalBaIdAndRemoteBaId(localBaId, null);
		if (configurations.isEmpty())
			configurations = mssRepo.findByLocalBaIdAndRemoteBaId(null, remoteBaId);
		if (configurations.isEmpty())
			configurations = mssRepo.findByLocalBaIdAndRemoteBaId(null, null);
		if (configurations.isEmpty())
			throw new ChcException(I18nService.ERR_INVALID_BA, localBaId, remoteBaId);
		return configurations.get(0);
	}
	

}
