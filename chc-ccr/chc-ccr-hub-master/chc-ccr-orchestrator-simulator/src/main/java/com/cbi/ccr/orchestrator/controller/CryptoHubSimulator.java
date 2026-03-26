package com.cbi.ccr.orchestrator.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.frw.encryption.dto.CryptoHubRequestDTO;
import com.cbi.frw.encryption.dto.CryptoHubResponseDTO;
import com.cbi.frw.jwe.JweUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/chc-cryptohub/rest")
public class CryptoHubSimulator {
	
	private static final String KEY = "0123456789ABCDEF0123456789ABCDEF";
	private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	
	@PostMapping("/getkey")
	public ResponseEntity<CryptoHubResponseDTO> getKey(@RequestBody CryptoHubRequestDTO dto) throws DecoderException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		CryptoHubResponseDTO response = new CryptoHubResponseDTO();
		
		if(dto.getIdFile() != null && dto.getClientPublicKey() != null) {
			byte[] decodedPublicKey = Hex.decodeHex(dto.getClientPublicKey());
			String base64PublicKey = Base64.getEncoder().encodeToString(decodedPublicKey);
			RSAPublicKey rsaPublicKey = JweUtils.getRSAPublicKey(base64PublicKey);
			
			byte[] generatedKey = encrypt(rsaPublicKey, KEY.getBytes());
			
			log.info("Request received for id: {}", dto.getIdFile());
			response.setIdFile(dto.getIdFile());
			response.setGeneratedKEY(Hex.encodeHexString(generatedKey));
			return ResponseEntity.ok(response);
		} 
		
		return ResponseEntity.status(500).body(response);
	}
	
	public byte[] encrypt(RSAPublicKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
	    Cipher cipher = Cipher.getInstance(ALGORITHM);   
	    cipher.init(Cipher.ENCRYPT_MODE, key);  
	    return cipher.doFinal(plaintext);
	}
}
