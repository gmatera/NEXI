package com.cbi.ccr.csw.mq.common.mq;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.service.common.logger.CswLog;

import encoding.EncodingUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileQueueUtil {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public static String generateGroupId() {
		return new BigInteger(12 * 8, new Random()).toString(16);
	}
	
	public void sendFileGrouped(File file, Long groupSize, String sndFileQueue, String groupId) throws IOException {

		List<byte[]> fileParts = FileData.getListOfMessageParts(file, groupSize);
		int numberOfParts = fileParts.size() - 1; // zero is excluded
		CswLog.debug(log, String.format("number of messages that will be sent: %s", numberOfParts));
		
		for (int i = 1; i < fileParts.size(); i++) {
			CswLog.debug(log, String.format("sending part number %s of %s", i, numberOfParts));
			sendMessage(sndFileQueue, groupId, i, fileParts);
		}
		
		CswLog.debug(log, "All messages sent");
	}

	private void sendMessage(String sndFileQueue, String groupId, int index, List<byte[]> fileParts) {
		jmsTemplate.send(sndFileQueue, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {	
				BytesMessage message = session.createBytesMessage();
				message.setStringProperty("JMSXGroupID", groupId);
				message.setIntProperty("JMSXGroupSeq", index);
				if (index == fileParts.size() -1) {
					CswLog.debug(log, String.format("last part number %s", index));
					message.setBooleanProperty("JMS_IBM_Last_Msg_In_Group", true);
				}
				message.writeBytes(fileParts.get(index));
				return message;
			}
		});
	}
	
	public byte[] browseFileGrouped(String destination, byte[] groupId) throws JMSException {
		CswLog.debug(log, String.format("GroupId: %s", EncodingUtils.encodeHexString(groupId).toUpperCase()));
		Integer groupSize = jmsTemplate.browseSelected(destination,
				"JMSXGroupID='ID:" + EncodingUtils.encodeHexString(groupId).toUpperCase() + "' AND JMS_IBM_Last_Msg_In_Group=TRUE", new BrowserCallback<Integer>() {
					@SuppressWarnings("unchecked")
					@Override
					public Integer doInJms(Session s, QueueBrowser qb) throws JMSException {
						if (Collections.list(qb.getEnumeration()).isEmpty()) return 0;
						
						Object msg = (Collections.list(qb.getEnumeration()).get(0));
						
						if(msg instanceof BytesMessage) {
							BytesMessage lastMessage = (BytesMessage) msg;
							
							CswLog.debug(log, String.format("JMSXGroupSeq: %s", lastMessage.getIntProperty("JMSXGroupSeq")));
							return lastMessage.getIntProperty("JMSXGroupSeq");
						}else if(msg instanceof TextMessage) {
							TextMessage lastMessage = (TextMessage) msg;
							CswLog.info(log, String.format("JMSXGroupSeq: %s", lastMessage.getIntProperty("JMSXGroupSeq")));
							return lastMessage.getIntProperty("JMSXGroupSeq");
						} else{
							CswLog.info(log, String.format("ERROR last Message received is of type: %s", msg.getClass().toString()));
							return 0;
						}
					}
				});
		
		boolean failed = false;
		List<byte[]> fileParts = new ArrayList<>();
		
		CswLog.debug(log, String.format("group size: %s", groupSize));
		for (int i = 1; (i <= groupSize) && !failed; i++) {

			Message message = jmsTemplate.browseSelected(destination,
					"JMSXGroupID='ID:" + EncodingUtils.encodeHexString(groupId).toUpperCase()  + "' AND JMSXGroupSeq=" + i, new BrowserCallback<Message>() {
						@SuppressWarnings("unchecked")
						@Override
						public Message doInJms(Session s, QueueBrowser qb) throws JMSException {
							return (Message) (Collections.list(qb.getEnumeration()).get(0));
						}
					});
			

			if (message != null) {
				if(message instanceof BytesMessage) {
					CswLog.debug(log, "############ JMS File is BytesMessage format. reading it");
					BytesMessage msg  =  (BytesMessage) message; 
					byte[] byteData = new byte[(int) msg.getBodyLength()];
					msg.readBytes(byteData);
					fileParts.add(byteData);
				} else if(message instanceof TextMessage) {
					CswLog.info(log, "############ JMS File is TextMessage format. reading it");
					TextMessage msg = (TextMessage) message;
					String text = msg.getText();
					byte[] byteData = text.getBytes();
					fileParts.add(byteData);
				} else {
					CswLog.info(log, String.format("############ Not reconized JMS Message format: %s", message.getClass().toString()));
					failed = true;
				}
			} else {
				failed = true;
			}
		}

		return FileData.assembleByteArray(fileParts);
	}

	public void deleteFileGrouped(String destination, byte[] groupIdbyte) throws JMSException {
		CswLog.info(log, String.format("DELETING MQ file GroupId: %s", groupIdbyte));
		BytesMessage lastMessage = (BytesMessage) jmsTemplate.receiveSelected(destination,
				"JMSXGroupID='ID:" + EncodingUtils.encodeHexString(groupIdbyte).toUpperCase()  + "' AND JMS_IBM_Last_Msg_In_Group=TRUE");
		
		if (lastMessage != null) {

			int groupSize = lastMessage.getIntProperty("JMSXGroupSeq");
			String groupId = lastMessage.getStringProperty("JMSXGroupID");

			boolean failed = false;

			for (int i = 1; (i < groupSize) && !failed; i++) {

				jmsTemplate.receiveSelected(destination,
						"JMSXGroupID='" + groupId + "'AND JMSXGroupSeq=" + i);
			}
		}
		
	}
	
	
	public static class FileData {

		private FileData() {}
		
		public static List<byte[]> getListOfMessageParts(File file, Long maxPartSize) throws IOException {
			List<byte[]> fileParts = new ArrayList<>();
			try (FileInputStream fis = new FileInputStream(file)) {
				
				long size = Files.size(Paths.get(file.getAbsolutePath()));
			
				byte[] buffer = new byte[Math.toIntExact((size < maxPartSize) ? size : maxPartSize)];
				
				int index = 1;
				fileParts.add(0,null);
				int r = 0;
				while((r = fis.read(buffer)) != -1) {
					
					byte[] bufferint =  new byte[r];
					
					System.arraycopy(buffer, 0, bufferint, 0, r);
					
					fileParts.add(index, bufferint);
					
					index++;
				}
				
				return fileParts;
			} 
		}
		
		public static byte[] assembleByteArray(List<byte[]> fileParts) {

				int length = 0;
				for(byte[] bytesArray: fileParts) {
					length += bytesArray.length;
				}

				ByteBuffer buffer = ByteBuffer.wrap(new byte[length]);
					
				for(byte[] bytesArray: fileParts) {
					buffer.put(bytesArray);
				}
		
			return buffer.array();
		}
		
	}
	
	public static byte[] trim(byte[] bytes)
	{
	    int i = bytes.length - 1;
	    while (i >= 0 && bytes[i] == 0)
	    {
	        --i;
	    }

	    return Arrays.copyOf(bytes, i + 1);
	}
}
