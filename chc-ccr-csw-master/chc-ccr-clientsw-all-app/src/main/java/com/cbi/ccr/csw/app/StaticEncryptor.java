package com.cbi.ccr.csw.app;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jasypt.util.text.BasicTextEncryptor;

import com.cbi.ccr.csw.encryption.LocalEncryptionService;

import liquibase.repackaged.org.apache.commons.text.RandomStringGenerator;

public class StaticEncryptor {
	
	private static Logger log = null;
	
	public static void main(String[] args){
		
		System.setProperty("java.util.logging.SimpleFormatter.format",
	              "%5$s %n");
	      log = Logger.getLogger(StaticEncryptor.class.getName());
	      
		log.info("########################################\n");
		
		String message2 = "Passwords doesn't match. Please enter your password again: ";
		
		String localEncryptionKey = System.getProperty("encKey");
		
		BasicTextEncryptor textEncryptorWithEncryptionKey = new BasicTextEncryptor();
		
		BasicTextEncryptor textEncryptorWithLocalKey = new BasicTextEncryptor();
		textEncryptorWithLocalKey.setPassword(LocalEncryptionService.LOCAL_KEY);
		
		if(localEncryptionKey == null && args.length >0) {
			log.severe("Cannot pass argument if -DencKey parameter was not provided");
			System.exit(-1);
		}
		
		if(localEncryptionKey == null) {
			
			textEncryptorWithEncryptionKey.setPassword(doInitialInstallation(textEncryptorWithLocalKey));
			dbPassword(message2, textEncryptorWithEncryptionKey);
			keystorePassword(message2, textEncryptorWithEncryptionKey);
		} else {
			
			if(localEncryptionKey.startsWith(LocalEncryptionService.ENCRYPTION_PREFIX)) {
				String encrytedText = localEncryptionKey.replace(LocalEncryptionService.ENCRYPTION_PREFIX, "");
				localEncryptionKey = textEncryptorWithLocalKey.decrypt(encrytedText);
			}else {
				if(log.isLoggable(Level.INFO)) {
					log.info(String.format("local Encryption Key is: %s%s", LocalEncryptionService.ENCRYPTION_PREFIX, 
							textEncryptorWithLocalKey.encrypt(localEncryptionKey)));
				}
			}
			
			textEncryptorWithEncryptionKey.setPassword(localEncryptionKey);
			
			if(args.length > 0 && args[0] != null && "dbPwd".equals(args[0])) {
				dbPassword(message2, textEncryptorWithEncryptionKey);
			}else if(args.length > 0 && args[0] != null && "keystorePwd".equals(args[0])) {
				keystorePassword(message2, textEncryptorWithEncryptionKey);
			}
		}

		log.info("Once done, restart the ClientSW.");
		
		log.info("########################################");
		
		log.info("!!!!!! WARINING!!");
		log.info("The localEncryptionKey is also used to encrypt some database data, so you cannot change it anymore");
		
	}
	
	private static void dbPassword(String message2, BasicTextEncryptor textEncryptorWithEncryptionKey) {
		String message = "Please enter your Database password: ";
		String databasePwd = askForPassword(message, message2);
		
		String encryptedDatabasePassword = textEncryptorWithEncryptionKey.encrypt(databasePwd);
		
		if(log.isLoggable(Level.INFO)) {
			log.info(String.format("pwd is %s", databasePwd));
			log.info(String.format("encrypted database Password is: %s%s", LocalEncryptionService.ENCRYPTION_PREFIX, 
					encryptedDatabasePassword));
			log.info("Please update the property spring.datasource.password of your start.sh/bat script with the generated value");
			log.info("\n");
		}
	}
	
	private static void keystorePassword(String message2, BasicTextEncryptor textEncryptorWithEncryptionKey) {
		String message = "Please enter your keystore password: ";
		String databasePwd = askForPassword(message, message2);
		
		String encryptedDatabasePassword = textEncryptorWithEncryptionKey.encrypt(databasePwd);
		if(log.isLoggable(Level.INFO)) {
			log.info(String.format("encrypted keystore Password is: %s%s", LocalEncryptionService.ENCRYPTION_PREFIX, 
					encryptedDatabasePassword));
			log.info("Please update the property keyStorePassword of your start.sh/bat script with the generated value");
			log.info("\n");
		}
	}
	
	private static String doInitialInstallation(BasicTextEncryptor textEncryptorWithLocalKey) {
		
		log.info("WARNING!! Generating password for the FIRST time only installation!");
		log.info("Don't do that if you need to update only the database or keystore password!!\n");
		
		RandomStringGenerator generatorAlphaNumeric = new RandomStringGenerator.Builder()
				.selectFrom("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789(){}$%&£_?#/|".toCharArray())
	            .build();

		String localEncryptionKey = generatorAlphaNumeric.generate(25); 
		
		
		
		if(log.isLoggable(Level.INFO)) {
			log.info(String.format("local Encryption Key is: %s%s", LocalEncryptionService.ENCRYPTION_PREFIX, 
					textEncryptorWithLocalKey.encrypt(localEncryptionKey)));
			
			log.info("Add the property csw_local_encryption_key in your start.sh/bat script with the generated value");
			log.info("\n");
		}
		return localEncryptionKey;
	}
	
	private static String askForPassword(String message, String message2) {
		String pwd;
		
		boolean retry = false;
		while(true) {
			pwd = readPassword(retry ? message2 : message);
			String pwd2 = readPassword("Please repeat your password: ");
			
			if(pwd == null || !pwd.equals(pwd2)) {
				retry = true;
			}else {
				break;	
			}
		}
		return pwd;
	}
	
	private static String readPassword(String prompt) {
        String line = null;
        Console c = System.console();
        if (c != null) {
             line = new String(c.readPassword(prompt));
        } else {
        	log.info(prompt);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                 line = bufferedReader.readLine();
            } catch (IOException e) { 
                //Ignore    
            }
        }
        return line;
    }
}
