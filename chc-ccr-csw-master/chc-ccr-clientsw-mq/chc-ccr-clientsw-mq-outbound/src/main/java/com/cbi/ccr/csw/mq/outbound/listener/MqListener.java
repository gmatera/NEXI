package com.cbi.ccr.csw.mq.outbound.listener;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.exception.ChcUnrecoverableException;
import com.cbi.frw.common.util.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Profile("MQ")
@Slf4j
@Component
public class MqListener implements MessageListener {

	@Autowired
	private Mq1400Handler handler1400;
	
	@Autowired
	private Mq1406Handler handler1406;
	
	@Autowired
	private Mq1410Handler handler1410;
	
	@Autowired
	private Mq1419Handler handler1419;
	
	@Autowired
	private Mq1911Handler handler1911;
	
	@Autowired
	private Mq1933Handler handler1933;
	
	@Autowired
	private Mq1991Handler handler1991;

	@Override
	public void onMessage(Message message) {
		CswLog.debug(log, String.format("message received at %s msg: %s", LocalDateTime.now(), message));
		
		String primitiveId = null;
		Byte[] byteArraydata = null;
		String messageId = "";
		
		try {
			if (message instanceof TextMessage) {
				TextMessage byteMessage = (TextMessage) message;
				String text = byteMessage.getText();
				primitiveId = text.substring(0, 4);
				byteArraydata = ArrayUtils.toObject(text.getBytes());
				messageId = byteMessage.getJMSMessageID();
				
			}else if (message instanceof BytesMessage) {
				BytesMessage byteMessage = (BytesMessage) message;
				byte[] byteData = new byte[(int) byteMessage.getBodyLength()];
				byteMessage.readBytes(byteData);
				byteMessage.reset();
				primitiveId = new String(Arrays.copyOf(byteData, 4));
				byteArraydata = ArrayUtils.toObject(byteData);
				
				messageId = byteMessage.getJMSMessageID();
			} else {
				CswLog.error(log, String.format("DISCARDING MESSGE, it is not a TextMessage or  BytesMessage but is %s ",message.getClass().toString()));
				return;
			}
		
		} catch (Exception e) {
			CswLog.error(log, String.format("DISCARDING MESSGE, Error reading from MQ %s", e));
			// CHC-387, in questo caso il messaggio deve essere scartato
			return;
		}
		
		try {
			switch (primitiveId) {
			case "1400":
				handler1400.processMessage(byteArraydata);
				break;
			case "1406":
				handler1406.processMessage(byteArraydata);
				break;
			case "1410":
				handler1410.processMessage(byteArraydata);
				break;
			case "1419":
				handler1419.processMessage(byteArraydata);
				break;
			case "1911":
				handler1911.processMessage(byteArraydata);
				break;
			case "1933":
				handler1933.processMessage(byteArraydata);
				break;
			case "1991":
				handler1991.processMessage(byteArraydata);
				break;
			default:
				CswLog.info(log, String.format("No Handler found for id: %s", primitiveId));
				break;
			}
			
		} catch (PrimitiveLengthMismatchException | ChcUnrecoverableException e) {
			// occorre consumare il messaggio, in quanto non è possibile procedere
			// la primitiva non è lavorabile
			logPrimitiveToFile(byteArraydata, primitiveId, messageId, e);
		} catch (Exception e) {
			logPrimitiveToFile(byteArraydata, primitiveId, messageId, e);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// ignored
				Thread.currentThread().interrupt();
			}
			// re-throw so the message will get processed again
			//throw e;
		} finally {
			CswLog.uset();
		}
	}
	
	private void logPrimitiveToFile(Byte[] byteArraydata, String primitiveId, String messageId, Exception e) {
		
		File out = new File(FileUtils.getTemp(), String.format("%s_%s.txt", System.currentTimeMillis(), primitiveId));
		
		try (FileOutputStream fos = new FileOutputStream(out)){
			fos.write(ArrayUtils.toPrimitive(byteArraydata));
			
			CswLog.error(log, String.format("ERROR while processing the primitive %s id:%s, rawData saved into file %s. error is:%s",
					primitiveId, messageId, out.toString(), e));

		} catch (Exception ex) {
			CswLog.error(log, String.format("debug print primitive primitiveId err:%s", ex));
		}
	}
	

}
