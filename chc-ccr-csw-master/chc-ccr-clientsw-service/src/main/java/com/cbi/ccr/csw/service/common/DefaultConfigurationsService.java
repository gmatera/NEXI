package com.cbi.ccr.csw.service.common;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.DSNCreationAlgo;
import com.cbi.ccr.csw.domain.csw.config.GlobalProperties;
import com.cbi.ccr.csw.domain.csw.config.GlobalPropertiesRepository;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.PropertiesEnum;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFMSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFTSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFmsDBRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFtsDBRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMSSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationMssDbRepository;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFTSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFmsMQRepository;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFtsMQRepository;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMSSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMssMQRepository;


@Service
public class DefaultConfigurationsService {
	
	private static final String ASCII = "ASCII";
	private static final String MEGABYTE = String.valueOf((1024L * 1024));

	@Value("${local_storage_path_decrypted}")
	private String rcvPath;
	
	@Autowired
	private ConfigurationFmsDBRepository confFMSDB;
	
	@Autowired
	private ConfigurationFtsDBRepository confFTSDB;
	
	@Autowired
	private ConfigurationMssDbRepository confMSSDB;
	
	@Autowired
	private ConfigurationFmsMQRepository confFMSMQ;
	
	@Autowired
	private ConfigurationFtsMQRepository confFTSMQ;
	
	@Autowired
	private ConfigurationMssMQRepository confMSSMQ;
	
	@Autowired	
	private GlobalPropertiesRepository globalProperties;
	
	@PostConstruct
	public void insertAllDefaults() {
		insertGlobalPropertiesDefaults();
		insertFMSDBDefaults();
		insertFTSDBDefaults();
		insertMSSDBDefaults();
		insertFMSMQDefaults();
		insertFTSMQDefaults();
		insertMSSMQDefaults();
	}
		
	
	private void insertGlobalPropertiesDefaults() { 
		if(globalProperties.findFirstByPropertyName(PropertiesEnum.UPLOAD_QUEUE_NAME_GROUP_SIZE.getLabel()) != null) 
			return;

		GlobalProperties globalConfig = new GlobalProperties();
		globalConfig.setPropertyName(PropertiesEnum.UPLOAD_QUEUE_NAME_GROUP_SIZE.getLabel());
		globalConfig.setMandatory(true);
		globalConfig.setType(null);
		globalConfig.setRequiredMsg(null);
		globalConfig.setMinLength(0);
		globalConfig.setMaxLength(10);
		globalConfig.setValue(MEGABYTE);
		
		globalProperties.save(globalConfig);
	}
	
	//============================================================== DB ==============================================================
	/**
	 * LAU_ENABLED = non abilitata (nessuna chiave in default)
	 * SND_COMPLETION_ALGO/RCV_COMPLETION_ALGO = sì (a fine trasferimento, è richiesto per l'item il completamento automatico in CLEANABLE)
	 * Per FTS/FMS:
	 * SND_CODE_PAGE/HUB_CODE_PAGE/RCV_CODE_PAGE = BINARIO
	 * SND_LINE_SEPARATOR/HUB_LINE_SEPARATOR/RCV_LINE_SEPARATOR = no line separator (none)
	 * SND_RECORD_FORMAT/RCV_RECORD_FORMAT(Hub) = file variabile
	 * SND_MAX_REC_LENGHT/RCV_MAX_REC_LENGHT(Hub) = 32.000
	 * RCV_PATH = path esposto in start.bat con il parametro local_storage_path_decrypted
	 * RCV_DIGEST_FILE_ALG = SHA-256
	 * RCV_DSN_CREATION_ALGO = -1 e prefisso non valorizzato (fname file consegnato = vfn del file ricevuto)
	 */
	private void insertFMSDBDefaults() {
		if(!confFMSDB.findByLocalBaIdAndRemoteBaId(null, null).isEmpty()) 
			return;
		
		ConfigurationFMSDB conf = new ConfigurationFMSDB();
		conf.setLauEnabled(false);
		conf.setSndCompletionAlgo(true);
		conf.setRcvCompletionAlgo(true);
		conf.setSndLineSeparator(LineSeparator.NONE);
		conf.setRcvLineSeparator(LineSeparator.NONE);
		conf.setHubLineSeparator(LineSeparator.NONE);
		conf.setSndRecordFormat(RecordFormat.VARIABLE);
		conf.setRcvRecordFormat(RecordFormat.VARIABLE);
		conf.setSndMaxRecLength(32000);
		conf.setRcvMaxRecLength(32000);
		conf.setRcvPath(rcvPath);
		conf.setRcvDnsCreationAlgo(DSNCreationAlgo.DSN_MINUS_1);
		conf.setInterfaceType("DB");
		confFMSDB.save(conf);
	}
	
	private void insertFTSDBDefaults() {
		if(!confFTSDB.findByLocalBaIdAndRemoteBaId(null, null).isEmpty()) 
			return;
		
		ConfigurationFTSDB conf = new ConfigurationFTSDB();
		conf.setLauEnabled(false);
		conf.setSndCompletionAlgo(true);
		conf.setRcvCompletionAlgo(true);
		conf.setSndLineSeparator(LineSeparator.NONE);
		conf.setRcvLineSeparator(LineSeparator.NONE);
		conf.setHubLineSeparator(LineSeparator.NONE);
		conf.setSndRecordFormat(RecordFormat.VARIABLE);
		conf.setRcvRecordFormat(RecordFormat.VARIABLE);
		conf.setSndMaxRecLength(32000);
		conf.setRcvMaxRecLength(32000);
		conf.setRcvPath(rcvPath);
		conf.setRcvDnsCreationAlgo(DSNCreationAlgo.DSN_MINUS_1);
		conf.setInterfaceType("DB");
		confFTSDB.save(conf);
	}
	
	private void insertMSSDBDefaults() {
		if(!confMSSDB.findByLocalBaIdAndRemoteBaId(null, null).isEmpty()) 
			return;
		
		ConfigurationMSSDB conf = new ConfigurationMSSDB();
		conf.setLauEnabled(false);
		conf.setSndCompletionAlgo(true);
		conf.setRcvCompletionAlgo(true);
		conf.setInterfaceType("DB");
		confMSSDB.save(conf);
	}
	
	//============================================================== MQ ==============================================================

	/**
	 * LAU_ENABLED = non abilitata (nessuna chiave in default)
	 * SND_COMPLETION_ALGO/RCV_COMPLETION_ALGO = sì (a fine trasferimento, è richiesto per l'item il completamento automatico in CLEANABLE)
	 * Per FTS/FMS:
	 * SND_CODE_PAGE/HUB_CODE_PAGE/RCV_CODE_PAGE = BINARIO
	 * SND_LINE_SEPARATOR/HUB_LINE_SEPARATOR/RCV_LINE_SEPARATOR = no line separator (none)
	 * SND_RECORD_FORMAT/RCV_RECORD_FORMAT(Hub) = file variabile
	 * SND_MAX_REC_LENGHT/RCV_MAX_REC_LENGHT(Hub) = 32.000
	 * RCV_PATH = path esposto in start.bat con il parametro local_storage_path_decrypted
	 * RCV_DIGEST_FILE_ALG = SHA-256
	 * RCV_DSN_CREATION_ALGO = -1 e prefisso non valorizzato (fname file consegnato = vfn del file ricevuto)
	 * 
	 * 
	 * RCV_PRIM_CONV_FORMAT = ASCII
	 * Per FMS/FTS:
	 * RCV_AUTO_READ = sì (i flussi in ricezione prevedono la consegna del file contestualmente alla ricezione)
	 * MQI_POS_CREATE_IND = no (non viene richiesto invio primitiva 1413)
	 * UPLOAD_Q_NAME = RCVDATA (si conviene un nome generico che sarà in carico del cliente modificare con la corretta coda di ricezione dei dati)
	 */
	
	private void insertFMSMQDefaults() {
		if(!confFMSMQ.findByLocalBaIdAndRemoteBaId(null, null).isEmpty()) 
			return;
		
		ConfigurationFMSMQ conf = new ConfigurationFMSMQ();
		conf.setLauEnabled(false);
		conf.setSndCompletionAlgo(true);
		conf.setRcvCompletionAlgo(true);
		conf.setSndLineSeparator(LineSeparator.NONE);
		conf.setRcvLineSeparator(LineSeparator.NONE);
		conf.setHubLineSeparator(LineSeparator.NONE);
		conf.setSndRecordFormat(RecordFormat.VARIABLE);
		conf.setRcvRecordFormat(RecordFormat.VARIABLE);
		conf.setSndMaxRecLength(32000);
		conf.setRcvMaxRecLength(32000);
		conf.setRcvPath(rcvPath);
		conf.setInterfaceType("MQ");
		conf.setRcvPrimConvFormat(ASCII);
		conf.setRcvAutoRead(true);
		conf.setMqiPosCreateInd(false);
		conf.setUploadQName("RCVDATA");
		
		confFMSMQ.save(conf);
	}
	
	private void insertMSSMQDefaults() {
		if(!confFTSMQ.findByLocalBaIdAndRemoteBaId(null, null).isEmpty()) 
			return;
		
		ConfigurationFTSMQ conf = new ConfigurationFTSMQ();
		conf.setLauEnabled(false);
		conf.setSndCompletionAlgo(true);
		conf.setRcvCompletionAlgo(true);
		conf.setSndLineSeparator(LineSeparator.NONE);
		conf.setRcvLineSeparator(LineSeparator.NONE);
		conf.setHubLineSeparator(LineSeparator.NONE);
		conf.setSndRecordFormat(RecordFormat.VARIABLE);
		conf.setRcvRecordFormat(RecordFormat.VARIABLE);
		conf.setSndMaxRecLength(32000);
		conf.setRcvMaxRecLength(32000);
		conf.setRcvPath(rcvPath);
		conf.setInterfaceType("MQ");
		conf.setRcvPrimConvFormat(ASCII);
		conf.setRcvAutoRead(true);
		conf.setMqiPosCreateInd(false);
		conf.setUploadQName("RCVDATA");
		confFTSMQ.save(conf);
	}

	private void insertFTSMQDefaults() {
		if(!confMSSMQ.findByLocalBaIdAndRemoteBaId(null, null).isEmpty()) 
			return;
		
		ConfigurationMSSMQ conf = new ConfigurationMSSMQ();
		conf.setLauEnabled(false);
		conf.setSndCompletionAlgo(true);
		conf.setRcvCompletionAlgo(true);
		conf.setInterfaceType("MQ");
		conf.setRcvPrimConvFormat(ASCII);

		confMSSMQ.save(conf);
	}
	
	

}
