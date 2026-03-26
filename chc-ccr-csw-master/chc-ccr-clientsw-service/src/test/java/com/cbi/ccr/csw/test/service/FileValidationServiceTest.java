package com.cbi.ccr.csw.test.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFMSDB;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFmsDBRepository;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFtsDBRepository;
import com.cbi.ccr.csw.service.common.FileRecordFormatValidator;

@SpringBootTest(classes = { ConfigServiceT.class })
class FileValidationServiceTest extends CommonTest {
	
	@Autowired 
	private FileRecordFormatValidator fileValidationService;
	
	@Autowired
	private ConfigurationFmsDBRepository configFMSRepository;
	
	@Autowired
	private ConfigurationFtsDBRepository configFTSRepository;
	
	@Value("${test_file_path}")
	private String testFilePath;
	
	private String fileAsciiVarLF= "FILE-ASCII-VAR-LF.txt";
	private String fileAsciiFixedLF = "FILE-ASCII-100FISSO-LF.txt";
	
	private String fileEbcdic30kCRLF = "FILE30k.CRLFE25";
	private String fileEbcdic5kCRLF = "FILE5K.CRLFE15";
	
//	@Test
//	void validateFmsFixedAsciiFile() {
//		mockLoadConfigurationFMS(buildFMSConfiguration());
//		File file = new File(testFilePath, fileAsciiFixedLF);
//		assertDoesNotThrow(() -> {
//			fileValidationService.processFileValidationFMS(LOCAL_BA_ID, REMOTE_BA_ID, file.getAbsolutePath());
//		});
//	}
//	
//	@Test
//	void validateFmsVariableAsciiFile() {
//		ConfigurationFMS config =  buildFMSConfiguration();
//		config.setSndRecordFormat(RecordFormat.VARIABLE);
//		config.setSndMaxRecLength(32000);
//		mockLoadConfigurationFMS(config);
//		File file = new File(testFilePath, fileAsciiVarLF);
//		assertDoesNotThrow(() -> {
//			fileValidationService.processFileValidationFMS(LOCAL_BA_ID, REMOTE_BA_ID, file.getAbsolutePath());
//		});
//	}
//	
//	@Test
//	void validateFmsVariableEBCDICFile() {
//		ConfigurationFMS config =  buildFMSConfiguration();
//		config.setSndRecordFormat(RecordFormat.VARIABLE);
//		config.setSndMaxRecLength(34000);
//		config.setSndLineSeparator(LineSeparator.CRLF_0X0D25);
//		mockLoadConfigurationFMS(config);;
//		File file = new File(testFilePath, fileEbcdic30kCRLF);
//		assertDoesNotThrow(() -> {
//			fileValidationService.processFileValidationFMS(LOCAL_BA_ID, REMOTE_BA_ID, file.getAbsolutePath());
//		});
//	}
	
	/**
	 * non abbiamo un file EBCDIC a tracciato fisso
	 */
//	@Test
//	void validateFmsFixedEBCDICFile() {
//		ConfigurationFMS config =  buildFMSConfiguration();
//		config.setSndRecordFormat(RecordFormat.FIXED);
//		config.setSndMaxRecLength(57);
//		config.setSndLineSeparator(LineSeparator.CRLF_0X0D25);
//		mockLoadConfigurationFMS(config);;
//		File file = new File(testFilePath, fileEbcdic30kCRLF);
//		assertDoesNotThrow(() -> {
//			fileValidationService.processFileValidationFMS(LOCAL_BA_ID, REMOTE_BA_ID, file.getAbsolutePath());
//		});
//	}
	
	//TODO casi di errore sul formato del record fisso e variabile
	//TODO aggiungere test analoghi per FTS

	private void mockLoadConfigurationFMS(ConfigurationFMSDB config) {	
		List<ConfigurationFMSDB> configurations = new ArrayList<>();
		configurations.add(config);
		
		when(configFMSRepository.findByLocalBaIdAndRemoteBaId(LOCAL_BA_ID, REMOTE_BA_ID)).thenReturn(configurations);	
	}

	private ConfigurationFMSDB buildFMSConfiguration() {
		ConfigurationFMSDB config = new ConfigurationFMSDB();
		
		config.setInterfaceType("DB");
		config.setLocalBaId(LOCAL_BA_ID);
		config.setRemoteBaId(REMOTE_BA_ID);
		
		config.setSndMaxRecLength(100);
		config.setSndCodePage(CodePage.ASCII);
		config.setSndRecordFormat(RecordFormat.FIXED);
		config.setSndLineSeparator(LineSeparator.LF_0X0A);
		
		config.setRcvCodePage(CodePage.ASCII);	
		config.setRcvLineSeparator(LineSeparator.LF_0X0A);
		return config;
	}

}
