package com.cbi.frw.ibmmq.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JmsPutGetTLSOneWay {

	// System exit status value (assume unset value to be 1)
	private static int status = 1;

	// Create variables for the connection to MQ
	private static final String HOST = "localhost"; // Host name or IP address
	private static final int PORT = 1414; // Listener port for your queue manager
	private static final String CHANNEL = "DEV.APP.SVRCONN"; // Channel name
	private static final String QMGR = "QM1"; // Queue manager name
	private static final String APP_USER = "app"; // User name that application uses to connect to MQ
	private static final String APP_PASSWORD = "passw0rd"; // Password that the application uses to connect to MQ
	private static final String QUEUE_NAME = "DEV.QUEUE.1"; // Queue that the application uses to put and get messages to and from
	
	
	private static final String  KEYSTORE = "src/test/clientkey_oneway.p12";
	
	
	// FTS.CREA.IND
	// DEV.QUEUE.1

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		// aggiungi -Djavax.net.debug=true  -Djavax.net.debug=ssl:handshake per avere log addizionali 

		// Create a connection factory
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		JmsConnectionFactory cf = ff.createConnectionFactory();

		// Set the properties
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
		cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
		cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
		cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsPutGet (JMS)");
		cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
		
		// sono necessarie con TLS OneWay (solo server)
		cf.setStringProperty(WMQConstants.USERID, APP_USER);
		cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
		
		cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "TLS_RSA_WITH_AES_128_CBC_SHA256");
		System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings","false");
		 
		// TLS_RSA_WITH_AES_256_GCM_SHA384
		// TLS_RSA_WITH_AES_128_CBC_SHA256
		
		 setupKeystore(cf, KEYSTORE);

		 sendTestMessage(cf);

		System.exit(status);

	} // end main()

	public static void sendTestMessage(JmsConnectionFactory cf) {
		// Variables
		JMSContext context = null;
		Destination destination = null;
		JMSProducer producer = null;
		JMSConsumer consumer = null;
		
		try {  
			// Create JMS objects
			context = cf.createContext();
			destination = context.createQueue("queue:///" + QUEUE_NAME);

			long uniqueNumber = System.currentTimeMillis() % 1000;
			TextMessage message = context.createTextMessage("Your lucky number today is " + uniqueNumber);

			producer = context.createProducer();
			producer.send(destination, message);
			System.out.println("Sent message:\n" + message);

			consumer = context.createConsumer(destination); // autoclosable
			String receivedMessage = consumer.receiveBody(String.class, 15000); // in ms or 15 seconds

			System.out.println("\nReceived message:\n" + receivedMessage);

            context.close();

			recordSuccess();
		} catch (Exception jmsex) {
			recordFailure(jmsex);
		}
	}
	
	public static void setupKeystore(JmsConnectionFactory cf, String  keystore) throws Exception {
		
		Path tsPath = Paths.get(keystore).toAbsolutePath();
		
		 if(!Files.exists(tsPath)) {
			 log.error("clientkey.p12 non trovato in {}", tsPath.toAbsolutePath());
			 System.exit(-1);
	      }
			
		KeyStore keyStore = KeyStore.getInstance("pkcs12");
		java.io.FileInputStream keyStoreInputStream = new java.io.FileInputStream(tsPath.toString());
		keyStore.load(keyStoreInputStream, "changeit".toCharArray());

		KeyStore trustStore = KeyStore.getInstance ("pkcs12");
		
		java.io.FileInputStream trustStoreInputStream = new java.io.FileInputStream(tsPath.toString());
		trustStore.load (trustStoreInputStream, "changeit".toCharArray());

		keyStoreInputStream.close();
		trustStoreInputStream.close();

		KeyManagerFactory keyManagerFactory = 
		  KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		
		TrustManagerFactory trustManagerFactory = 
		  TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		
		keyManagerFactory.init(keyStore,"changeit".toCharArray());
		trustManagerFactory.init(trustStore);

		SSLContext sslContext = SSLContext.getInstance("TLSv1"); 
		sslContext.init(keyManagerFactory.getKeyManagers(), 
		  trustManagerFactory.getTrustManagers(), 
		  null);
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		cf.setObjectProperty(CommonConstants.WMQ_SSL_SOCKET_FACTORY, sslSocketFactory);
	}
	
	/**
	 * Record this run as successful.
	 */
	private static void recordSuccess() {
		System.out.println("SUCCESS");
		status = 0;
		return;
	}

	/**
	 * Record this run as failure.
	 *
	 * @param ex
	 */
	private static void recordFailure(Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				System.out.println(ex);
			}
		}
		System.out.println("FAILURE");
		status = -1;
		return;
	}

	/**
	 * Process a JMSException and any associated inner exceptions.
	 *
	 * @param jmsex
	 */
	private static void processJMSException(JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s):");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
		return;
	}

}