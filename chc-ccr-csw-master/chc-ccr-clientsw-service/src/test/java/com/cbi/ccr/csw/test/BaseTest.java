package com.cbi.ccr.csw.test;

import javax.persistence.EntityManagerFactory;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.support.TransactionTemplate;

import com.cbi.ccr.csw.domain.ConfigRouteInterfaceRepository;
import com.cbi.ccr.csw.domain.csw.ServiceRegistryLogRepository;
import com.cbi.ccr.csw.domain.csw.ServiceRegistryRepository;
import com.cbi.ccr.csw.domain.csw.config.AddOnConfigurationFTSRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFmsDBRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFtsDBRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMssDbRepository;
import com.cbi.ccr.csw.domain.fms.FMSRecvDBRepository;
import com.cbi.ccr.csw.domain.fms.FMSSendDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSRecvDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSSendDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSRecvDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSSendDBRepository;
import com.cbi.ccr.csw.domain.user.UserRepository;
import com.cbi.ccr.csw.service.common.LivenessService;
import com.cbi.frw.persistence.ConfigJpa;
import com.cbi.frw.persistence.service.GenericDAO;

public abstract class BaseTest {

	@MockBean
	protected UserRepository userRepository;
	
	@MockBean
	protected FMSSendDBRepository fmsSendRepository;
	@MockBean
	protected FTSSendDBRepository ftsSendRepository;
	@MockBean
	protected MSSSendDBRepository mssSendRepository;

	@MockBean
	protected FMSRecvDBRepository fmsRecvRepository;
	@MockBean
	protected FTSRecvDBRepository ftsRecvRepository;
	@MockBean
	protected MSSRecvDBRepository mssRecvRepository;
	@MockBean
	protected ConfigurationFmsDBRepository configurationFmsRepository;
	@MockBean
	protected ConfigurationFtsDBRepository configurationFtsRepository;
	@MockBean
	protected ConfigurationMssDbRepository configurationMssRepository;
	@MockBean
	protected ServiceRegistryRepository serviceRegistryRepository;
	@MockBean
	protected ServiceRegistryLogRepository serviceRegistryLogRepository;
	@MockBean
	protected ConfigRouteInterfaceRepository configRouteInterfaceRepository;
	@MockBean
	protected AddOnConfigurationFTSRepository addOnConfigurationFTSRepository;
	@MockBean
	protected GenericDAO genericDAO;
	@MockBean
	EntityManagerFactory entityManagerFactory;
	@MockBean
	protected LivenessService livenessService;
	@MockBean
	ConfigJpa configJpa;
	@MockBean
	TransactionTemplate transactionTemplate;


}
