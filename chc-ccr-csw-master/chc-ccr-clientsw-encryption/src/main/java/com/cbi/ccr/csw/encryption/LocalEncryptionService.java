package com.cbi.ccr.csw.encryption;

import javax.annotation.PostConstruct;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class LocalEncryptionService {

	public static final String ENCRYPTION_PREFIX = "enc-";
	
	@Value("${csw_local_encryption_key}")
	private String cswEncryptionKey;
	
	public static final String LOCAL_KEY = "J4R0jl6ssBu3SEg9gt7imnDU5NcGzPrV";
	
	private BasicTextEncryptor textEncryptorWithEncryptionKey = new BasicTextEncryptor();
	private BasicTextEncryptor textEncryptorWithLocalKey = new BasicTextEncryptor();
	
	@PostConstruct
	public void init() {
		
		textEncryptorWithLocalKey.setPassword(LOCAL_KEY);
		
		
		if(cswEncryptionKey.startsWith(LocalEncryptionService.ENCRYPTION_PREFIX)) {
			String encrytedText = cswEncryptionKey.replace(LocalEncryptionService.ENCRYPTION_PREFIX, "");
			cswEncryptionKey = textEncryptorWithLocalKey.decrypt(encrytedText);
		}
		textEncryptorWithEncryptionKey.setPassword(cswEncryptionKey);
		
	}
	
	public String decrypt(@NonNull String encrytedText) {
		if(encrytedText.startsWith(LocalEncryptionService.ENCRYPTION_PREFIX)) {
			encrytedText = encrytedText.replace(LocalEncryptionService.ENCRYPTION_PREFIX, "");
			return textEncryptorWithEncryptionKey.decrypt(encrytedText);
		}else {
			return encrytedText;
		}
		
	}
	
	public String encrypt(@NonNull String text) {
		if(text.startsWith(LocalEncryptionService.ENCRYPTION_PREFIX)) {
			return text;
		}
	
		return LocalEncryptionService.ENCRYPTION_PREFIX + textEncryptorWithEncryptionKey.encrypt(text);
	}
}
