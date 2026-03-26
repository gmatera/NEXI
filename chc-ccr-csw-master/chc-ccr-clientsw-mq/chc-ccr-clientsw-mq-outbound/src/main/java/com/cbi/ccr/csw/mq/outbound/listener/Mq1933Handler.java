package com.cbi.ccr.csw.mq.outbound.listener;

import java.time.LocalDateTime;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1933SecReleaseMsgreq;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1934SecReleaseMsgcnf;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.CommonConfigurationServiceMQ;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveConfigurationLoader;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveService;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMSSMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQRepository;
import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.mq.outbound.service.MqServiceBase;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;

import chc.framework.util.parsing.input.FlowioInputMapper;
import chc.framework.util.parsing.input.FlowioOutputMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Mq1933Handler extends MqServiceBase<MQ1933SecReleaseMsgreq>{
	
	@Autowired
	protected CommonConfigurationServiceMQ configurationService;
	
	@Autowired
	private MqPrimitiveConfigurationLoader mqPrimitiveConfigurationLoader;
	
	@Autowired
	protected JmsTemplate jmsTemplate;
	
	@Autowired
	private MSSRecvMQRepository repositoryMSS;
	
	@Autowired
	protected MqPrimitiveSenderService mqPrimitiveSenderService;
	
	private FlowioInputMapper<MQ1933SecReleaseMsgreq> flowioMapper1933;
	private FlowioOutputMapper<MQ1934SecReleaseMsgcnf> flowioMapper1934;
	
	@PostConstruct
	private void initBinder() {
		flowioMapper1933 = new FlowioInputMapper<>(BinderFactory.binder1933());
		flowioMapper1934 = new FlowioOutputMapper<>(BinderFactory.binder1934());
	}
	
	@Override
	protected FlowioInputMapper<MQ1933SecReleaseMsgreq> getFlowioMapper() {
		return flowioMapper1933;
	}

	@Override
	protected MQ1933SecReleaseMsgreq getPrimitiveDto(Byte[] data) throws ChcUnrecoverableException, PrimitiveLengthMismatchException {
		try {
			checkSize(flowioMapper1933, data.length);
			
			return flowioMapper1933.read(Arrays.asList(data));
		} catch (PrimitiveLengthMismatchException e) {
			throw e;
		} catch (Exception e) {
			throw new ChcUnrecoverableException(I18nService.ERR_PROCESSING_MESSAGE);
		}
	}
	
	@Override
	protected void processMessage(Byte[] bytedata) throws ChcUnrecoverableException, PrimitiveLengthMismatchException{
		MQ1933SecReleaseMsgreq primitiveDTO;
		primitiveDTO = getPrimitiveDto(bytedata);
		CswLog.getLogData().setMessage(String.format("Processing primitive id: %s", primitiveDTO.getId()));
		CswLog.getLogData().setSystem(LogSystemEnum.CSW.name());
		CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
		CswLog.getLogData().setService(ServiceEnum.MSS.name());
		CswLog.getLogData().setLocalBaId(primitiveDTO.getBaLoc());
		CswLog.getLogData().setRemoteBaId(primitiveDTO.getBaRem());
		CswLog.getLogData().setUdr(primitiveDTO.getUdr());
		CswLog.getLogData().setFunction("processMessage");
//		CswLog.info(log);
//		log.info("Processing primitive id: {}", primitiveDTO.getId());
		ConfigurationMSSMQ configMSS;
		try {
			configMSS = configurationService.loadMSSMQConfiguration(primitiveDTO.getBaLoc(), primitiveDTO.getBaRem());
			
			MSSRecvMQ entity = repositoryMSS.findOneByLocalBaIdAndRemoteBaIdAndUdr(primitiveDTO.getBaLoc(), primitiveDTO.getBaRem(), primitiveDTO.getUdr());
			if(!entity.getStatus().equals(RecvStatusMQ.CLEANABLE)) {
				entity.setStatus(RecvStatusMQ.CLEANABLE);
				entity.setComplete(1);
			}
			entity.setCswProcessTms(LocalDateTime.now());
			entity.setBaProcessTms(LocalDateTime.now());
			CswLog.getLogData().setMessage("MSS MQ sending 1934 ..");
//			CswLog.debug(log);
			
			send1934(configMSS, entity);
			CswLog.getLogData().setMessage("Sent 1934");
//			CswLog.info(log);
			transactionTemplate.executeWithoutResult(t -> {
				repositoryMSS.save(entity);
			});
			
		} catch (Exception e) {
			throw new ChcRollbackException(I18nService.ERR_PROCESSING_MESSAGE, e.getLocalizedMessage());
		}	
		CswLog.uset();
	}

	private void send1934(ConfigurationMSSMQ configFMS, MSSRecvMQ entity) {
		try {
			MQ1934SecReleaseMsgcnf mq1934 = MqPrimitiveService.get1934PrimitiveMq(entity);
			
			byte[] primitiveByte = flowioMapper1934.writeByte(mq1934);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(mq1934.getId());
			p.setServiceType(ServiceType.MSS);
			p.setEntityId(entity.getId());
			
			mqPrimitiveSenderService.storePrimitiveOnDB(primitiveByte, p);
			
		} catch (JmsException | NoSuchFieldException e1) {
			e1.printStackTrace();
		}
	}
	
}
