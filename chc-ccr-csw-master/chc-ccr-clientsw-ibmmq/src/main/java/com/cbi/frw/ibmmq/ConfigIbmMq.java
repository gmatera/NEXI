package com.cbi.frw.ibmmq;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.util.StringUtils;

import com.cbi.ccr.csw.encryption.LocalEncryptionService;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsConstants;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.common.CommonConstants;

import lombok.extern.slf4j.Slf4j;


@Profile("MQ")
@Slf4j
@Configuration
@EnableJms
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = { ConfigIbmMq.class })
@PropertySource({ "classpath:/ibmmq.properties" })
public class ConfigIbmMq {
	
	@Value("${ibm.mq.queueManager}")
	private String ibmMqQueueManager;
	@Value("${ibm.mq.connName}")
	private String ibmMqConnName;
	@Value("${ibm.mq.channel}")
	private String ibmMqConnChannel;
	@Value("${ibm.mq.user}")
	private String ibmMqConnUser;
	@Value("${ibm.mq.password}")
	private String ibmMqPassword;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Bean
	public DefaultJmsListenerContainerFactory myFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all boot's default to this factory, including the message
		// converter
		configurer.configure(factory, connectionFactory);

		return factory;
	}

	@Bean
	public JmsConnectionFactory connectionFactory(LocalEncryptionService encryptionService, Environment env) throws JMSException {

		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(JmsConstants.WMQ_PROVIDER);
		JmsConnectionFactory cf = ff.createConnectionFactory();

		cf.setStringProperty(CommonConstants.WMQ_CONNECTION_NAME_LIST, ibmMqConnName);
		cf.setStringProperty(CommonConstants.WMQ_CHANNEL, ibmMqConnChannel);
		cf.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
		cf.setStringProperty(CommonConstants.WMQ_QUEUE_MANAGER, ibmMqQueueManager);
		cf.setStringProperty(CommonConstants.WMQ_APPLICATIONNAME, "ClientSW");
		cf.setBooleanProperty(JmsConstants.USER_AUTHENTICATION_MQCSP, true);
		cf.setStringProperty(JmsConstants.USERID, ibmMqConnUser);
		cf.setStringProperty(JmsConstants.PASSWORD, ibmMqPassword);
		
		String tls = env.getProperty("ibm.mq.tls.enabled");
		String sslCipher = env.getProperty("ibm.mq.ssl-cipher-spec");
		
		if(tls != null && Boolean.parseBoolean(tls)) {
			
			String trustStore = env.getProperty("ibm.mq.keyStore");
			String trustStorePwd = env.getProperty("ibm.mq.keyStorePassword");
			
			checkProperties(trustStore, trustStorePwd, sslCipher);

			trustStorePwd = encryptionService.decrypt(trustStorePwd);
			
			Path file = Paths.get(trustStore).toAbsolutePath();
			try {
				setupKeystore(cf, file.toString(), trustStorePwd);
				
			} catch (Exception e) {
				log.error("Error occurred while setting up the KeyStore", e);
				System.exit(SpringApplication.exit(appContext, () -> -1));
			}
			
			cf.setStringProperty(CommonConstants.WMQ_SSL_CIPHER_SUITE, sslCipher);
			System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
		}

		return cf;
	}
	
	private void setupKeystore(JmsConnectionFactory cf, String  keystore, String password) throws Exception {
		
		Path tsPath = Paths.get(keystore).toAbsolutePath();
		
		 if(!Files.exists(tsPath)) {
			 log.error("clientkey.p12 non trovato in {}", tsPath.toAbsolutePath());
			 System.exit(SpringApplication.exit(appContext, () -> -1));
	      }
			
		KeyStore keyStore = KeyStore.getInstance("jks");
		java.io.FileInputStream keyStoreInputStream = new java.io.FileInputStream(tsPath.toString());
		keyStore.load(keyStoreInputStream, password.toCharArray());

		KeyStore trustStore = KeyStore.getInstance ("jks");
		
		java.io.FileInputStream trustStoreInputStream = new java.io.FileInputStream(tsPath.toString());
		trustStore.load (trustStoreInputStream, password.toCharArray());

		keyStoreInputStream.close();
		trustStoreInputStream.close();

		KeyManagerFactory keyManagerFactory = 
		  KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		
		TrustManagerFactory trustManagerFactory = 
		  TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		
		keyManagerFactory.init(keyStore,password.toCharArray());
		trustManagerFactory.init(trustStore);

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2"); 
		sslContext.init(keyManagerFactory.getKeyManagers(), 
		  trustManagerFactory.getTrustManagers(), 
		  null);
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		cf.setObjectProperty(CommonConstants.WMQ_SSL_SOCKET_FACTORY, sslSocketFactory);
	}

	private void checkProperties(String trustStore, String trustStorePwd, String sslCipher) {
		
		if(sslCipher == null) {
			log.error("sslCipher system property not found, could not start.");
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}
		
		if(!StringUtils.hasLength(trustStorePwd)) {
			log.error("ibm.mq.keyStorePassword system property not found, could not start.");
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}

		Path file = Paths.get(trustStore);
		
		if(!Files.exists(file)){
			log.error("ibm.mq.keyStore not found at location:{}. could not start",trustStore);
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}
		
	}
}
