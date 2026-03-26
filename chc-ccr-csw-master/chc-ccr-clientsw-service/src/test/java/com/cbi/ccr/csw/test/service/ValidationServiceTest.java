package com.cbi.ccr.csw.test.service;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cbi.ccr.csw.domain.ConfigRouteInterface;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.ConfigRouteInterfaceRepository;
import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.service.common.ValidationService;
import com.cbi.frw.common.exception.ChcException;

@SpringBootTest(classes = { ConfigServiceT.class })
class ValidationServiceTest extends CommonTest {

	@Autowired
	private ValidationService validationService;

	@Autowired
	private ConfigRouteInterfaceRepository configRouteInterfaceRepository;
	
	//TODO aggiungere test di tutte le validazioni regex (FMS, FTS e MSS)
	
//	@Test
//	void invalidBa() {
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateBaAndInterface("AA", "BB", "FMS", RouteInterface.MQ);
//		}, "invalid ba");
//
//	}
//
//	@Test
//	void invalidInterface() {
//		mockRouteInterfaceRepoCall();
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateBaAndInterface(LOCAL_BA_ID, REMOTE_BA_ID,"FMS", RouteInterface.MQ);
//		}, "invalid interface");
//
//	}

	@Test
	void regexLocalBaFMS() {
		FMSSend fmsSend = buildFMS();
		fmsSend.setLocalBaId("AAA");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFMSRegex(fmsSend);
		}, "LOCAL_BA");
	}
	
	@Test
	void regexRemoteBaFMS() {
		FMSSend fmsSend = buildFMS();
		fmsSend.setRemoteBaId("AAA");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFMSRegex(fmsSend);
		}, "REMOTE_BA");
	}
	
	@Test
	void regexVfnFMS() {
		FMSSend fmsSend = buildFMS();
		fmsSend.setVfn("!");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFMSRegex(fmsSend);
		}, "VFN");
	}
	
	@Test
	void regexUdrFMS() {
		FMSSend fmsSend = buildFMS();
		fmsSend.setUdr("!");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFMSRegex(fmsSend);
		}, "USERDATAREMOTE");
	}
	
	@Test
	void regexTurFMS() {
		FMSSend fmsSend = buildFMS();
		fmsSend.setTur("!");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFMSRegex(fmsSend);
		}, "TUR");
	}
	
	@Test
	void regexMessageTypeFMS() {
		FMSSend fmsSend = buildFMS();
		fmsSend.setMessageType("AAA");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFMSRegex(fmsSend);
		}, "MESSAGETYPE");
	}
	
	@Test
	void regexCatApplFMS() {
		FMSSend fmsSend = buildFMS();
		fmsSend.setCatAppl("a");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFMSRegex(fmsSend);
		}, "CATAPPL");
	}
	
	@Test
	void regexLocalBaFTS() {
		FTSSend ftsSend = buildFTS();
		ftsSend.setLocalBaId("AAA");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFTSRegex(ftsSend);
		}, "LOCAL_BA");
	}
	
	@Test
	void regexRemoteBaFTS() {
		FTSSend ftsSend = buildFTS();
		ftsSend.setRemoteBaId("AAA");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFTSRegex(ftsSend);
		}, "REMOTE_BA");
	}
	
	@Test
	void regexVfnFTS() {
		FTSSend ftsSend = buildFTS();
		ftsSend.setVfn("!");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFTSRegex(ftsSend);
		}, "VFN");
	}
	
	@Test
	void regexApplicativeDataFieldFTS() {
		FTSSend ftsSend = buildFTS();
		ftsSend.setApplicativeDataField("!");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateFTSRegex(ftsSend);
		}, "APPLICATIVE_DATA_FIELD");
	}
	
	@Test
	void regexLocalBaMSS() {
		MSSSend mssSend = buildMSS();
		mssSend.setLocalBaId("AAA");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateMSSRegex(mssSend);
		}, "LOCAL_BA");
	}
	
	@Test
	void regeRemoteBaMSS() {
		MSSSend mssSend = buildMSS();
		mssSend.setRemoteBaId("AAA");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateMSSRegex(mssSend);
		}, "REMOTE_BA");
	}
	
	@Test
	void regexMessageTypeMSS() {
		MSSSend mssSend = buildMSS();
		mssSend.setMessageType("aaa");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateMSSRegex(mssSend);
		}, "MESSAGETYPE");
	}
	
	@Test
	void regexTurMSS() {
		MSSSend mssSend = buildMSS();
		mssSend.setTur("!");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateMSSRegex(mssSend);
		}, "TUR");
	}
	
	@Test
	void regexRemoteRefMSS() {
		MSSSend mssSend = buildMSS();
		mssSend.setRemoteRef("!");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateMSSRegex(mssSend);
		}, "REMOTEREF");
	}
	
	@Test
	void regexMsgIdMSS() {
		MSSSend mssSend = buildMSS();
		mssSend.setMsgId("aaa");
		assertThrowsExactly(ChcException.class, () -> {
			validationService.validateMSSRegex(mssSend);
		}, "MSGID");
	}
	
	//TODO aggiungere validazione fileSize e messageLeng
	
//	@Test
//	void regexFileSizeFMS() {
//		FMSSend fmsSend = buildFMS();
//		fmsSend.setFileSize(2L);
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateSize("2MB.pdf", 2L, 1024L * 1024L * 1024L, false);
//		}, "null");
//	}
//	
//	@Test
//	void regexFileSizeFTS() {
//		FTSSend ftsSend = buildFTS();
//		ftsSend.setFileSize(2L);
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateSize("2MB.pdf", 2L, 1024L * 1024L * 1024L, false);
//		}, "null");
//	}
//	
//	@Test
//	void regexMessageLengthFMS() {
//		FMSSend fmsSend = buildFMS();
//		fmsSend.setFileSize(2L);
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateSize("1MB.jpg", 2L, 1024L * 1024L * 1024L, true);
//		}, "null");
//	}
//	
//	@Test
//	void regexMessageLengthMSS() {
//		MSSSend mssSend = buildMSS();
//		mssSend.setMessageLeng(2);
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateSize("1MB.jpg", 2L, 1024L * 1024L * 1024L, true);
//		}, "null");
//	}
	
	//TODO aggiungere validazione del file hash MD5
	
//	@Test
//	void regexFileHashFMS() {
//		FMSSend fmsSend = buildFMS();
//		fmsSend.setRemoteBaId("AAA");
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateMD5(null, fmsSend.getFileHash());
//		}, "null");
//	}
//	
//	@Test
//	void regexFileHashFTS() {
//		FTSSend ftsSend = buildFTS();
//		ftsSend.setRemoteBaId("AAA");
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateMD5(null, ftsSend.getFileHash());
//		}, "null");
//	}
	
	//TODO aggiungere validazione del fileDigest e messageDIgest SHA256
	
//	@Test
//	void regexFileDigestFMS() {
//		FMSSend fmsSend = buildFMS();
//		fmsSend.setFileSize(2L);
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateSha256(null, null);
//		}, "null");
//	}
//	
//	@Test
//	void regexFileDigestFTS() {
//		FTSSend ftsSend = buildFTS();
//		ftsSend.setFileSize(2L);
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateSha256(null, null);
//		}, "null");
//	}
//	
//	@Test
//	void regexMessageDigestFMS() {
//		FMSSend fmsSend = buildFMS();
//		fmsSend.setFileSize(2L);
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateSha256(null, null);
//		}, "null");
//	}
//	
//	@Test
//	void regexMessageDigestMSS() {
//		MSSSend mssSend = buildMSS();
//		mssSend.setMessageLeng(2);
//		assertThrowsExactly(ChcException.class, () -> {
//			validationService.validateSha256(null, null);
//		}, "null");
//	}

	private void mockRouteInterfaceRepoCall() {
		ConfigRouteInterface routeInterface = new ConfigRouteInterface();
		routeInterface.setInterFace(RouteInterface.DB);
		routeInterface.setLocalBaId(LOCAL_BA_ID);
		routeInterface.setRemoteBaId(REMOTE_BA_ID);

		List<ConfigRouteInterface> entityList = new ArrayList<>();
		entityList.add(routeInterface);

//		when(configRouteInterfaceRepository.findByLocalBaIdAndRemoteBaIdAndService(LOCAL_BA_ID, REMOTE_BA_ID, "FMS"))
//				.thenReturn(entityList);
	}

	private FMSSend buildFMS() {
		FMSSend fmsSend = new FMSSend();
		fmsSend.setLocalBaId(LOCAL_BA_ID);
		fmsSend.setRemoteBaId(REMOTE_BA_ID);
		fmsSend.setVfn("VFN");
		fmsSend.setUdr("UDR");
		fmsSend.setTur("TUR");
		fmsSend.setMessageType("MSG");
		fmsSend.setCatAppl("CAT");
		fmsSend.setFileSize(1L);
		fmsSend.setFileName("1MB.jpg");	
		fmsSend.setMessageLeng(2);
		return fmsSend;
	}
	
	private FTSSend buildFTS() {
		FTSSend ftsSend = new FTSSend();
		ftsSend.setLocalBaId(LOCAL_BA_ID);
		ftsSend.setRemoteBaId(REMOTE_BA_ID);
		ftsSend.setVfn("VFN");
		ftsSend.setApplicativeDataField("APP");
		ftsSend.setFileSize(1L);
		ftsSend.setFileName("1MB.jpg");
		return ftsSend;
	}
	
	private MSSSend buildMSS() {
		MSSSend mssSend = new MSSSend();
		mssSend.setLocalBaId(LOCAL_BA_ID);
		mssSend.setRemoteBaId(REMOTE_BA_ID);
		mssSend.setTur("TUR");
		mssSend.setMessageType("MSG");
		mssSend.setRemoteRef("REM");
		mssSend.setMessageLeng(2);
		return mssSend;
	}
}
