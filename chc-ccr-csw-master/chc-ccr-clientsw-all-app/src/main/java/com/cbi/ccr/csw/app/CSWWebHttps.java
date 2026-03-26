package com.cbi.ccr.csw.app;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.Nio2Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.cbi.ccr.csw.encryption.LocalEncryptionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class CSWWebHttps implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

	@Autowired
	private ApplicationContext appContext;

	
	@Autowired
	private Environment env;
	@Autowired
	private LocalEncryptionService localEncryptionService;
	
	
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // customize the factory here
    	
    	
    	
		String sslprop = env.getProperty("csw.ssl.enabled");

		if (sslprop != null && Boolean.parseBoolean(sslprop)) {
			// Enable SSL
							
			String keyStorePath = env.getProperty("csw.ssl.keyStore");
			String keyStorePwd = env.getProperty("csw.ssl.keyStorePassword");
			String keyStoreAlias = env.getProperty("csw.ssl.keyAlias");

			checkValues(keyStorePath, keyStorePwd, keyStoreAlias);

			
//			Ssl ssl =  new Ssl();
//			factory.setSsl(ssl);
//	    	ssl.setKeyStorePassword(localEncryptionService.decrypt(keyStorePwd));
//	    	ssl.setKeyPassword(localEncryptionService.decrypt(keyStorePwd));
//	    	ssl.setEnabled(true);
//	    	ssl.setKeyAlias("springboot");
//	    	ssl.setKeyStore(Paths.get(keyStorePath).toAbsolutePath().toString());
	    	
			String keystore = Paths.get(keyStorePath).toAbsolutePath().toString();
			
			TomcatConnectorCustomizer cust = new TomcatConnectorCustomizer() {
				
				@Override
				public void customize(Connector connector) {
					connector.setPort(Integer.parseInt(env.getProperty("server.port")));
					connector.setScheme("https");
					connector.setSecure(true);
					Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
					protocol.setSSLEnabled(true);
					
//					 AbstractHttp11JsseProtocol<Nio2Channel> handler = (AbstractHttp11JsseProtocol<Nio2Channel>)connector.getProtocolHandler();
//			        handler.setMaxKeepAliveRequests(-1);
			        //handler.setAcceptorThreadCount(2);
//			        handler.setMaxHeaderCount(256);
//			        connector.setRedirectPort(8443);
			        connector.setProperty("useComet", Boolean.toString(false));
			        connector.setProperty("socket.appReadBufSize", "102400");
			        connector.setProperty("socket.rxBufSize", "102400");
			        connector.setProperty("socket.performanceConnectionTime", "2");
			        connector.setProperty("socket.performanceLatency", "0");
			        connector.setProperty("socket.performanceBandwidth", "1");

				}
			};
			factory.setTomcatConnectorCustomizers(Arrays.asList(cust));
			
			
			
			factory.setSslStoreProvider(new SslStoreProvider() {
				
				@Override
				public KeyStore getTrustStore() throws Exception {
					return null;
				}
				
				@Override
				public KeyStore getKeyStore() throws Exception {
					KeyStore keyStore = KeyStore.getInstance("jks");
					java.io.FileInputStream keyStoreInputStream = new java.io.FileInputStream(keystore);
					keyStore.load(keyStoreInputStream, localEncryptionService.decrypt(keyStorePwd).toCharArray());
					return keyStore;
				}
			});
			
		}
    	
    	
    }

	
	public void checkValues(String keyStorePath, String keyStorePassword, String keyAlias) {

		if (!StringUtils.hasLength(keyStorePassword)) {
			log.error("parameter csw.ssl.keyStorePassword system property not found, could not start.");
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}
		if (!StringUtils.hasLength(keyStorePath)) {
			log.error("parameter csw.ssl.keyStore path not set, could not start.");
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}
		if (!StringUtils.hasLength(keyAlias)) {
			log.error("parameter csw.ssl.keyAlias not set, could not start.");
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}
		if (!Files.exists(Paths.get(keyStorePath))) {
			log.error("keyStore not found at location:{}. could not start.{}", keyStorePath);
			System.exit(SpringApplication.exit(appContext, () -> -1));
		}

	}
}
