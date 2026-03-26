package com.cbi.ccr.csw.mq.outbound.listener;

import java.time.ZonedDateTime;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1406SecReleasereq;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.CommonConfigurationServiceMQ;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQRepository;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQRepository;
import com.cbi.ccr.csw.mq.domain.i.CswInboundMqWithFileEntity;
import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.mq.outbound.service.MqServiceBase;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.exception.ChcUnrecoverableException;

import chc.framework.util.parsing.input.FlowioInputMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Mq1406Handler extends MqServiceBase<MQ1406SecReleasereq> {

	@Autowired
	protected CommonConfigurationServiceMQ configurationService;

	@Autowired
	private MqPrimitiveSenderService mqPrimitiveSenderService;

	@Autowired
	private FMSRecvMQRepository repositoryFMS;

	@Autowired
	private FTSRecvMQRepository repositoryFTS;

	private FlowioInputMapper<MQ1406SecReleasereq> flowioMapper1406;

	@PostConstruct
	private void initBinder() {
		flowioMapper1406 = new FlowioInputMapper<>(BinderFactory.binder1406());
	}

	@Override
	protected FlowioInputMapper<MQ1406SecReleasereq> getFlowioMapper() {
		return flowioMapper1406;
	}

	@Override
	protected MQ1406SecReleasereq getPrimitiveDto(Byte[] data) throws ChcUnrecoverableException, PrimitiveLengthMismatchException {
		try {
			
			checkSize(flowioMapper1406, data.length);
			return flowioMapper1406.read(Arrays.asList(data));
		
		} catch (PrimitiveLengthMismatchException e) {
			throw e;
		} catch (Exception e) {
			throw new ChcUnrecoverableException(I18nService.ERR_PROCESSING_MESSAGE);
		}
	}

	@Override
	protected void processMessage(Byte[] bytedata) throws ChcUnrecoverableException, PrimitiveLengthMismatchException{
		
		MQ1406SecReleasereq primitiveDTO;
		primitiveDTO = getPrimitiveDto(bytedata);
		CswLog.getLogData().setFunction("processMessage");
		CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
		CswLog.getLogData().setSystem(LogSystemEnum.CSW.name());
		CswLog.getLogData().setLocalBaId(primitiveDTO.getBaLoc());
		CswLog.getLogData().setRemoteBaId(primitiveDTO.getBaRem());
		CswLog.getLogData().setVfn(primitiveDTO.getVfn());
				
		CswLog.info(log, String.format("Processing primitive id: %s", primitiveDTO.getId()));
		if (primitiveDTO.getSyncFlag() == 1) {
			CswLog.getLogData().setService(ServiceEnum.FMS.name());
			CswLog.getLogData().setUdr(primitiveDTO.getUdr());
			
			// result = 1, rej Reason 34
			FMSRecvMQ entity = repositoryFMS.findOneByLocalBaIdAndRemoteBaIdAndUdr(primitiveDTO.getBaLoc(),
					primitiveDTO.getBaRem(), primitiveDTO.getUdr());

			if(!checkValidStatus(primitiveDTO, entity))
				return;
			
			ZonedDateTime cswProcessTms = ZonedDateTime.now();
			entity.setBaProcessTms(primitiveDTO.getBaProcessTms());
			entity.setCswProcessTms(cswProcessTms.toLocalDateTime());
//			primitiveDTO.set
			send1407(entity.getId(), null, primitiveDTO, cswProcessTms);
			transactionTemplate.executeWithoutResult(t -> repositoryFMS.save(entity));

		} else {
			CswLog.getLogData().setService(ServiceEnum.FTS.name());

			FTSRecvMQ entity = repositoryFTS.findOneByLocalBaIdAndRemoteBaIdAndVfn(primitiveDTO.getBaLoc(),
					primitiveDTO.getBaRem(), primitiveDTO.getVfn());
			
			if(!checkValidStatus(primitiveDTO, entity))
				return;

			ZonedDateTime cswProcessTms = ZonedDateTime.now();
			entity.setBaProcessTms(primitiveDTO.getBaProcessTms());
			entity.setCswProcessTms(cswProcessTms.toLocalDateTime());

			send1407(entity.getId(), null, primitiveDTO, cswProcessTms);
			transactionTemplate.executeWithoutResult(t -> repositoryFTS.save(entity));
		}

	}

	private<E extends CswInboundMqWithFileEntity> boolean checkValidStatus(MQ1406SecReleasereq primitiveDTO, E entity) {
		if (entity == null) {
			send1407(0, 34, primitiveDTO, null);
			return false;
		}
		// se diverso da uno stato finale manda in errore 1407, result 1, reason 33
		if (!entity.getStatus().equals(RecvStatusMQ.CLEANABLE)
				&& !entity.getStatus().equals(RecvStatusMQ.DELIVERED)) {
			send1407(entity.getId(), 33, primitiveDTO, null);
			return false;
		}

		if (!entity.getStatus().equals(RecvStatusMQ.CLEANABLE)) {
			entity.setStatus(RecvStatusMQ.CLEANABLE);
			entity.setComplete(1);
		}
		
		return true;
	}

	private void send1407(long entityId, Integer rejectReason, MQ1406SecReleasereq primitiveDTO, ZonedDateTime siStdProcessTms) {
		mqPrimitiveSenderService.send1407(entityId, rejectReason, primitiveDTO, siStdProcessTms);
	}



}
