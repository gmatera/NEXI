package com.cbi.frw.ibmmq.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.cbi.frw.ibmmq.test.ConfigT.TestBean;
import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.common.CommonConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = ConfigT.class)
class IbmMqTest {

	@Autowired
	private JmsTemplate jmsTemplate;

	private static final String RCV_FILE_QUEUE = "DEV.QUEUE.2";

	private static final String GROUP_ID = "groupIdX";

	public static final String fileName = "File10B";

	@Test
	void dynamicQueue() throws JMSException {
		
	
       jmsTemplate.convertAndSend("DEV.QUEUE.1", "Hello World!");
        
        log.info("{}", TestBean.getInstance().message);
        
        Awaitility.await().pollInterval(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(10)).until(
				() -> TestBean.getInstance().message != null, Matchers.equalTo(Boolean.TRUE));
	}
	
	@Test
	void sendMessage() throws InterruptedException {
		jmsTemplate.convertAndSend("DEV.QUEUE.1", "Hello World!");

		Object receive = jmsTemplate.receiveAndConvert("DEV.QUEUE.1").toString();
		System.out.println("receive " + receive);

//		Thread.sleep(5000);
	}

	@Test
	public void sendFileMessage() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/" + fileName));

		jmsTemplate.convertAndSend("DEV.QUEUE.1", bytes);

		byte[] receive = (byte[]) jmsTemplate.receiveAndConvert("DEV.QUEUE.1");
		System.out.println("Received " + new String(receive));

	}

	@Test
	public void sendFileGrouped() throws JMSException {
		List<byte[]> fileParts = new ArrayList<>();
	    fileParts.add(null);
	    fileParts.add("hello".getBytes());
	    fileParts.add("world".getBytes());
	    
		for (int i = 1; i < fileParts.size(); i++) {
			sendMessage(fileParts, i);
		}
	}

	private void sendMessage(List<byte[]> fileParts, int index) {
		jmsTemplate.send(RCV_FILE_QUEUE, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {	
				BytesMessage message = session.createBytesMessage();
				message.setStringProperty("JMSXGroupID", GROUP_ID);
				message.setIntProperty("JMSXGroupSeq", index);
				if (index == fileParts.size()-1) {
					message.setBooleanProperty("JMS_IBM_Last_Msg_In_Group", true);
				}
				String stringBytes = "Message " + new String(fileParts.get(index)); 
				message.writeBytes(stringBytes.getBytes());
				return message;
			}
		});
	}

	@Test
	public void browseFileGrouped() throws JMSException {
		Integer groupSize = jmsTemplate.browseSelected(RCV_FILE_QUEUE,
				"JMSXGroupID='" + GROUP_ID + "'AND JMS_IBM_Last_Msg_In_Group=TRUE", new BrowserCallback<Integer>() {
					@SuppressWarnings("unchecked")
					@Override
					public Integer doInJms(Session s, QueueBrowser qb) throws JMSException {
						if (Collections.list(qb.getEnumeration()).isEmpty()) return 0;
						BytesMessage lastMessage = (BytesMessage) (Collections.list(qb.getEnumeration()).get(0));
						return lastMessage.getIntProperty("JMSXGroupSeq");
					}
				});
		;
		boolean failed = false;
		for (int i = 1; (i <= groupSize) && !failed; i++) {

			BytesMessage message = (BytesMessage) jmsTemplate.browseSelected(RCV_FILE_QUEUE,
					"JMSXGroupID='" + GROUP_ID + "'AND JMSXGroupSeq=" + i, new BrowserCallback<BytesMessage>() {
						@SuppressWarnings("unchecked")
						@Override
						public BytesMessage doInJms(Session s, QueueBrowser qb) throws JMSException {
							return (BytesMessage) (Collections.list(qb.getEnumeration()).get(0));

						}
					});
			;

			if (message != null) {
				byte[] byteData = null;
		        byteData = new byte[(int) message.getBodyLength()];
		        message.readBytes(byteData);
				System.out.println(new String(byteData));
			} else {
				failed = true;
			}
		}
	}
	@Test
	public void deleteFileGrouped() throws JMSException {
		
		//sendFileGrouped();
		
		BytesMessage lastMessage = (BytesMessage) jmsTemplate.receiveSelected(RCV_FILE_QUEUE,
				"JMSXGroupID='" + GROUP_ID + "'AND JMS_IBM_Last_Msg_In_Group=TRUE");
		
		if (lastMessage != null) {

			int groupSize = lastMessage.getIntProperty("JMSXGroupSeq");
			String groupId = lastMessage.getStringProperty("JMSXGroupID");

			boolean failed = false;

			for (int i = 1; (i < groupSize) && !failed; i++) {

				Message msg =  jmsTemplate.receiveSelected(RCV_FILE_QUEUE,
						"JMSXGroupID='" + groupId + "'AND JMSXGroupSeq=" + i);
				
				if(msg != null) {
					log.info("DELETED");
				}else {
					log.info("NOT FOUND ");
				}
			}
		}
		
		
	}
	
	@Test
	public void receiveFileGrouped() throws JMSException {
		MQConnectionFactory factory = new MQConnectionFactory();
		factory.setQueueManager("QM1");
		factory.setHostName("10.0.0.214");
		factory.setPort(1414);
		factory.setChannel("DEV.ADMIN.SVRCONN");
		factory.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
		factory.setStringProperty(WMQConstants.USERID, "admin");
		factory.setStringProperty(WMQConstants.PASSWORD, "passw0rd");
		factory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, false);
		MQQueue destination = new MQQueue(RCV_FILE_QUEUE);
		Connection connection = factory.createConnection();
		connection.start();
		Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

		MessageConsumer lastMessageConsumer = session.createConsumer(destination, "JMS_IBM_Last_Msg_In_Group=TRUE");
		session.createBrowser(destination, RCV_FILE_QUEUE);
		BytesMessage lastMessage = (BytesMessage) lastMessageConsumer.receiveNoWait();
		lastMessageConsumer.close();

		if (lastMessage != null) {

			int groupSize = lastMessage.getIntProperty("JMSXGroupSeq");
			String groupId = lastMessage.getStringProperty("JMSXGroupID");

			boolean failed = false;

			for (int i = 1; (i < groupSize) && !failed; i++) {

				MessageConsumer consumer = session.createConsumer(destination,
						"JMSXGroupID='" + groupId + "'AND JMSXGroupSeq=" + i);
				BytesMessage message = (BytesMessage) consumer.receiveNoWait();

				if (message != null) {
					byte[] byteData = null;
			        byteData = new byte[(int) message.getBodyLength()];
			        message.readBytes(byteData);
					System.out.println(new String(byteData));
				} else {
					failed = true;
				}

				consumer.close();

			}

			if (failed) {
				session.rollback();
			} else {
				byte[] byteData = new byte[(int) lastMessage.getBodyLength()];
		        lastMessage.readBytes(byteData);
				System.out.println(new String(byteData));
				
				session.commit();
			}

		}

		connection.close();
	}

}
