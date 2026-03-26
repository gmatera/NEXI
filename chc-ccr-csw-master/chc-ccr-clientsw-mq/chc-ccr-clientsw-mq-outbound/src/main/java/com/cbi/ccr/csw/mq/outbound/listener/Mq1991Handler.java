package com.cbi.ccr.csw.mq.outbound.listener;

import java.time.LocalDateTime;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.mq.common.dto.mss.MQ1991SecNotAckMsgreq;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQRepository;
import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.mq.outbound.service.MqServiceBase;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;

import chc.framework.util.parsing.input.FlowioInputMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * This handler  works on the sender client software, 
 * waiting for the primitive 1991 to update all the entities to a status called CLEANABLE.
 * If the handler manage correctly the update, then a 1992 primitive is sent with Ok as status, 
 * otherwise the same primitive will contain the error
 *
 */

@Slf4j
@Service
public class Mq1991Handler extends MqServiceBase<MQ1991SecNotAckMsgreq> {
	
	private FlowioInputMapper<MQ1991SecNotAckMsgreq> flowioMapper1991;
	
	@Autowired
	private MSSSendMQRepository repositoryMSS;
	
	@Autowired
	protected MqPrimitiveSenderService mqPrimitiveSenderService;
	
	@PostConstruct
	private void initBinder() {
		flowioMapper1991 = new FlowioInputMapper<>(BinderFactory.binder1991());
	}
	
	@Override
	protected FlowioInputMapper<MQ1991SecNotAckMsgreq> getFlowioMapper() {
		return flowioMapper1991;
	}
	
	@Override
	protected MQ1991SecNotAckMsgreq getPrimitiveDto(Byte[] data) throws ChcUnrecoverableException, PrimitiveLengthMismatchException {
		try {
			checkSize(flowioMapper1991, data.length);
			return flowioMapper1991.read(Arrays.asList(data));
		} catch (PrimitiveLengthMismatchException e) {
			throw e;
		} catch (Exception e) {
			throw new ChcUnrecoverableException(I18nService.ERR_PROCESSING_MESSAGE);
		}
	}
	
	@Override
	protected void processMessage(Byte[] bytedata) throws ChcUnrecoverableException, PrimitiveLengthMismatchException{
		MQ1991SecNotAckMsgreq primitiveDTO;
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

		try {
			processMSS(primitiveDTO);
		} catch (Exception e) {
			throw new ChcRollbackException(I18nService.ERR_PROCESSING_MESSAGE, e);
		}

	}
	
	private void processMSS(MQ1991SecNotAckMsgreq primitiveDTO) throws ChcException, NoSuchFieldException {
		MSSSendMQ entity = repositoryMSS.findOneByLocalBaIdAndRemoteBaIdAndUdr(primitiveDTO.getBaLoc(), primitiveDTO.getBaRem(), primitiveDTO.getUdr());
		
		if(entity == null) {
			mqPrimitiveSenderService.send1992Error(0, primitiveDTO);
			return;
		}
		entity.setCswProcessTms(LocalDateTime.now());
		
		if(entity.getStatus().equals(SendStatusMQ.SENT)) {
			entity.setStatus(SendStatusMQ.CLEANABLE);
			entity.setComplete(1);
			entity.setBaProcessTms(LocalDateTime.now());
			transactionTemplate.executeWithoutResult(t -> repositoryMSS.save(entity));
			mqPrimitiveSenderService.send1992(entity.getId(), primitiveDTO);
		}else {
			mqPrimitiveSenderService.send1992Error(entity.getId(), primitiveDTO);
		}


		CswLog.uset();
		//throw new ChcException(I18nService.CSW_WRONG_STATUS, entity.getStatus());
		
	}
	
}
