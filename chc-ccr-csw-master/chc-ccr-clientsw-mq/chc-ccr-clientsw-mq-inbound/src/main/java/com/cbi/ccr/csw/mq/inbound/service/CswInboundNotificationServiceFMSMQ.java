package com.cbi.ccr.csw.mq.inbound.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1402SecSendFileind;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveService;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQRepository;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;

import chc.framework.util.parsing.input.FlowioOutputMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CswInboundNotificationServiceFMSMQ extends CommonInboundNotificationServiceMq<FMSSendMQ, FMSSendMQRepository, ConfigurationFMSMQ> {

	//private FlowioOutputMapper<MQ1402SecSendFileind> flowioWriter1402 = new FlowioOutputMapper<>(BinderFactory.binder1402());
	
	@Autowired
	private MqPrimitiveSenderService primitiveSenderService;
	
	@Override
	protected ConfigurationFMSMQ getConfiguration(FMSSendMQ entity) throws ChcException {
		return configurationService.loadFMSMQConfiguration(entity.getLocalBaId(), entity.getRemoteBaId());
	}

	@Override
	protected void sendPrimitiveMessage(FMSSendMQ entity, NotificationDTO notification, String message) throws ChcException {
		
		CswLog.getLogData().setUdr(entity.getUdr());
		CswLog.getLogData().setVfn(entity.getVfn());
		CswLog.getLogData().setLocalBaId(entity.getLocalBaId());
		CswLog.getLogData().setRemoteBaId(entity.getRemoteBaId());
		CswLog.getLogData().setId(entity.getId().toString());
		primitiveSenderService.send1402(entity, message, notification.getCmdTms());
		
//		MQ1402SecSendFileind primitive = MqPrimitiveService.get1402PrimitiveFMS(entity, message, notification.getCmdTms(), getConfiguration(entity));
//		try {
//			String queue = mqPrimitiveConfigurationLoader.getQueueByPrimitive("1402");
//			
//			byte[] primitiveByte = flowioWriter1402.writeByte(primitive);
//			
//			log.info("Sending 1402: {}", new String(primitiveByte));
//			debugPrimitive1402(primitive, flowioWriter1402);
//			jmsTemplate.convertAndSend(queue, primitiveByte );
//		} catch (NoSuchFieldException e) {
//			throw new ChcException(I18nCommon.ERR_NOT_FOUND, e.getMessage());
//		}
	}
	
//	private void debugPrimitive1402(MQ1402SecSendFileind primitive, FlowioOutputMapper<MQ1402SecSendFileind> flowioWriter) throws NoSuchFieldException, ChcException {
//		if(!configurationService.isDebugPrimitiveMq())
//			return;
//		
//		try (FileOutputStream fos = new FileOutputStream(new File("/tmp/1402.txt"))){
//			fos.write(flowioWriter.writeByte(primitive));
//		} catch (IOException e) {
//			log.error("debugPrimitive ", e);
//		}
//	}

}
