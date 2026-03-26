package com.cbi.frw.ibmmq.test;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

public class JmsPutGetTLSMutual {

	// System exit status value (assume unset value to be 1)
	private static int status = 1;

	// Create variables for the connection to MQ
	private static final String HOST = "localhost"; // Host name or IP address
	private static final int PORT = 1414; // Listener port for your queue manager
	private static final String CHANNEL = "DEV.APP.SVRCONN"; // Channel name
	private static final String QMGR = "QM1"; // Queue manager name
	
	private static final String  KEYSTORE = "src/test/clientkey.p12";
	
	
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
			
			// non sono necessarie se uso Mutual TLS
//			cf.setStringProperty(WMQConstants.USERID, APP_USER);
//			cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
			
			cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "TLS_RSA_WITH_AES_128_CBC_SHA256");
			System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings","false");
			 
			// TLS_RSA_WITH_AES_256_GCM_SHA384
			// TLS_RSA_WITH_AES_128_CBC_SHA256
			
			JmsPutGetTLSOneWay.setupKeystore(cf, KEYSTORE);

			JmsPutGetTLSOneWay.sendTestMessage(cf);

		System.exit(status);

	} // end main()

	

}