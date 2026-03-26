package com.cbi.ccr.csw.mq.inbound.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1402SecSendFileind;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFTSMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQRepository;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.exception.ChcException;

import chc.framework.util.parsing.input.FlowioOutputMapper;

@Service
public class CswInboundNotificationServiceFTSMQ  extends CommonInboundNotificationServiceMq<FTSSendMQ, FTSSendMQRepository, ConfigurationFTSMQ> {

	@Autowired
	private MqPrimitiveSenderService primitiveSenderService;

	
	@Override
	protected ConfigurationFTSMQ getConfiguration(FTSSendMQ entity) throws ChcException {
		return configurationService.loadFTSMQConfiguration(entity.getLocalBaId(), entity.getRemoteBaId());
	}

	@Override
	protected void sendPrimitiveMessage(FTSSendMQ entity, NotificationDTO notification, String message) throws ChcException {
		CswLog.getLogData().setLocalBaId(entity.getLocalBaId());
		CswLog.getLogData().setRemoteBaId(entity.getRemoteBaId());
		CswLog.getLogData().setVfn(entity.getVfn());
		CswLog.getLogData().setId(entity.getId().toString());
		primitiveSenderService.send1402(entity, message, notification.getCmdTms());
		
//		MQ1402SecSendFileind primitive = MqPrimitiveService.get1402PrimitiveFTS(entity, message, notification.getCmdTms(), getConfiguration(entity));
//		FlowioOutputMapper<MQ1402SecSendFileind> flowioWriter = new FlowioOutputMapper<>(BinderFactory.binder1402());
//		try {
//			log.info("Sending primitive 1402: {}", new String(flowioWriter.writeByte(primitive)));
//			String queue = mqPrimitiveConfigurationLoader.getQueueByPrimitive("1402");
//			debugPrimitive1402(primitive, flowioWriter);
//			jmsTemplate.convertAndSend(queue, flowioWriter.writeByte(primitive));
//		} catch (NoSuchFieldException e) {
//			throw new ChcException(I18nCommon.ERR_NOT_FOUND, e.getMessage());
//		}
	}
	
	private void debugPrimitive1402(MQ1402SecSendFileind primitive, FlowioOutputMapper<MQ1402SecSendFileind> flowioWriter) throws NoSuchFieldException, ChcException {
		if(!configurationService.isDebugPrimitiveMq())
			return;
		
		try (FileOutputStream fos = new FileOutputStream(new File("/tmp/1402.txt"))){
			fos.write(flowioWriter.writeByte(primitive));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
