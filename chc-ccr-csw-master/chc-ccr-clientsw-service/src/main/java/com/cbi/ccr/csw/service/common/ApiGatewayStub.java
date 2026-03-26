package com.cbi.ccr.csw.service.common;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.encryption.dto.CryptoHubRequestDTO;
import com.cbi.frw.encryption.dto.CryptoHubResponseDTO;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtilsProxy;
import com.cbi.frw.jwe.JweUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiGatewayStub {

	@Value("${crypto_hub_url}")
	private String cryptoHubUrl;
	
	@Value("${enable_encryption}")
	private boolean encryptionEnabled;
	
	@Autowired
	private HttpUtilsProxy httpUtils;
	
	@Autowired
	private JweJwtService jweJwtService;
	
	@Autowired
	private ApplicationContext appContext;
	
	private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	
	@PostConstruct
	public void init() {
		try {
			new URL(cryptoHubUrl).toURI();
		} catch (MalformedURLException | URISyntaxException e) {
			log.error("CRYPTO HUB endpoint not valid: {}", cryptoHubUrl);
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}
	}
	
	public byte[] cryptoHubGetKey(UUID key) throws ChcStubException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException, ChcException {
		if(!encryptionEnabled)
			return "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF".getBytes();
		
		byte[] base64PubKey =  Base64.getDecoder().decode(JweUtils.filterPublicKey(jweJwtService.getEncPublicKey()));
		String hexPublicKey = Hex.encodeHexString(base64PubKey);
		
		CryptoHubRequestDTO hubRequestDTO = new CryptoHubRequestDTO();
		hubRequestDTO.setClientPublicKey(hexPublicKey);
		hubRequestDTO.setIdFile(key.toString());
		
		String requestJWE = jweJwtService.getSerializedEncryptedJWE(JSON.toJson(hubRequestDTO));
		HttpResponse<String> responseJWE = httpUtils.sendRequestWithBody(cryptoHubUrl, commonAuthorizationHeader(), requestJWE, String.class, 10000, "post");
		CryptoHubResponseDTO response = JSON.fromJson(jweJwtService.getDeserializedDecriptedJWE(responseJWE.getResponse()), CryptoHubResponseDTO.class);
		
		if(response.getGeneratedKEY() == null)
			throw new ChcException(I18nService.ERR_INVOKING_CRYPTO_HUB, String.format("invalid response: %s", JSON.toJson(response)));
		
		return decrypt(jweJwtService.getRsaEncPrivateKey(), Hex.decodeHex(response.getGeneratedKEY()));
	}


	private Map<String, String> commonAuthorizationHeader() throws ChcException, ChcStubException {
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", jweJwtService.getJwt());
		return headers;
	}
	
	public byte[] encrypt(RSAPublicKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
	    Cipher cipher = Cipher.getInstance(ALGORITHM);   
	    cipher.init(Cipher.ENCRYPT_MODE, key);  
	    return cipher.doFinal(plaintext);
	}

	public byte[] decrypt(RSAPrivateKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
	    Cipher cipher = Cipher.getInstance(ALGORITHM);   
	    cipher.init(Cipher.DECRYPT_MODE, key);  
	    return cipher.doFinal(ciphertext);
	}
}
