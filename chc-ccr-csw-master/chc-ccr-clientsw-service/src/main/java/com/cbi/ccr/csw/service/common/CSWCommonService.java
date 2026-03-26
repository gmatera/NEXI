package com.cbi.ccr.csw.service.common;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.persistence.service.AbstractService;
import com.cbi.frw.persistence.service.GenericDAO;

public abstract class CSWCommonService extends AbstractService  {

	@Autowired
	protected HubEncryptionUtil encryptionUtil;
		
	@Autowired
	protected ModelMapper mapper;
	
//	@Autowired
//	protected FileValidationService fileValidationService;
	
	@Autowired
	protected GenericDAO genericDAO;
	
	@Autowired
	protected LauService lauService;
	
	@Autowired
	protected ApiGatewayStub apiGatewayStub;
	
	@Override
	@PostConstruct
	public void init() {
		super.init();
	}

	public static String format(LocalDateTime ldt) {
		return ldt.format(DateTimeFormatter.ofPattern("yyMMddHHmmss+0000"));
	}
	
	public static String format2(LocalDateTime ldt) {
		return ldt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	}
	public static String format3(LocalDateTime ldt) {
		return ldt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
	}
	public static String format4(LocalDateTime ldt) {
		return ldt.format(DateTimeFormatter.ofPattern("yyMMdd"));
	}
	
	protected byte[] getEncryptionServiceKey(UUID key) throws ChcException, ChcStubException {
		byte[] encryptionServiceKey;
		try {
			encryptionServiceKey = apiGatewayStub.cryptoHubGetKey(key);
		} catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| NoSuchAlgorithmException | DecoderException  e) {
			throw new ChcException(I18nService.ERR_PROCESSING_MESSAGE, e.getLocalizedMessage());
		}
		return encryptionServiceKey;
	}
}
