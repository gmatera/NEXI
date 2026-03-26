package com.cbi.frw.encryption.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.frw.encryption.dto.CryptoHubRequestDTO;
import com.cbi.frw.encryption.dto.CryptoHubResponseDTO;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtilsProxy;
import com.cbi.frw.jwe.JweUtils;

import lombok.extern.slf4j.Slf4j;

@Service
public class EncryptionService {

	private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

	/**
	 * http://crypto-chc-int-app-0:8038/chc-cryptohub/getkey 10.1.32.51
	 */
	@Value("${enable_encryption}")
	private boolean encryptionEnabled;
	
	@Value("${crypto_hub_url}")
	protected String cryptoHubUrl;

	@Autowired
	private HttpUtilsProxy httpUtilsProxy;
	
	private String hexPublicKey;
	
	/**
	 * 
	 *Riservato per CCR HUB
	 */
	public byte[] getKey(UUID key, String publicKey, RSAPrivateKey privateKey, String jwt) throws  ChcStubException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, DecoderException {
		if(!encryptionEnabled)
			return "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF".getBytes();
		
		byte[] base64PubKey =  Base64.getDecoder().decode(JweUtils.filterPublicKey(publicKey));
		
		if(hexPublicKey == null)
			hexPublicKey = Hex.encodeHexString(base64PubKey);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", jwt);
		HttpResponse<CryptoHubResponseDTO> response;
		
		CryptoHubRequestDTO hubRequestDTO = new CryptoHubRequestDTO();
		hubRequestDTO.setClientPublicKey(hexPublicKey);
		hubRequestDTO.setIdFile(key.toString());

		response = httpUtilsProxy.postRequestWithBody(cryptoHubUrl, headers, hubRequestDTO, CryptoHubResponseDTO.class, 10000);
		
		return decrypt(privateKey, Hex.decodeHex(response.getResponse().getGeneratedKEY()));

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
