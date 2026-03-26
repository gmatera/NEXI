package com.cbi.ccr.csw.mq.outbound.listener;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.mq.outbound.service.MqServiceBase;
import com.cbi.ccr.csw.mq.outbound.service.OutboundServiceFMSMQ;
import com.cbi.ccr.csw.mq.outbound.service.OutboundServiceFTSMQ;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;

import chc.framework.util.parsing.input.FlowioInputMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Mq1400Handler extends MqServiceBase<MQ1400SecSendFilereq>{

	@Autowired
	private OutboundServiceFMSMQ serviceFmsMq;

	@Autowired
	private OutboundServiceFTSMQ serviceFtsMq;
	
	@Autowired
	protected MqPrimitiveSenderService mqPrimitiveSenderService;
	
	@Override
	protected FlowioInputMapper<MQ1400SecSendFilereq> getFlowioMapper() {
		return null;
	}

	@Override
	protected MQ1400SecSendFilereq getPrimitiveDto(Byte[] data) throws ChcUnrecoverableException, PrimitiveLengthMismatchException, ChcException {
		
		try {
			Byte[] mabArray = Arrays.copyOfRange(data, 3788, 3798);
			String stringValue = new String(ArrayUtils.toPrimitive(mabArray));

			int mabLen = Integer.parseInt(stringValue);
			
			FlowioInputMapper<MQ1400SecSendFilereq> flowioMapper = new FlowioInputMapper<>(BinderFactory.binder1400(mabLen));
			
			checkSize(flowioMapper, data.length);
			
			MQ1400SecSendFilereq req1400 = flowioMapper.read(Arrays.asList(data));
			
			if(req1400.getLocalAuthInfoLen() == null)
				req1400.setLocalAuthInfoLen(0L);
			
			return req1400;
		} catch (PrimitiveLengthMismatchException e) {
			Byte[] mabArray = Arrays.copyOfRange(data, 3788, 3798);
			String stringValue = new String(ArrayUtils.toPrimitive(mabArray));

			int mabLen = Integer.parseInt(stringValue);
			FlowioInputMapper<MQ1400SecSendFilereq> flowioMapper = new FlowioInputMapper<>(BinderFactory.binder1400(mabLen));
			MQ1400SecSendFilereq req1400 = flowioMapper.read(Arrays.asList(data));
			mqPrimitiveSenderService.send1401ErrorWithPrimitive(0, serviceFmsMq.build1401ErrorWithPrimitive(req1400, e.getLocalizedMessage(), e.getCode()));
			if(req1400.getSyncFlag() == 1)
				serviceFmsMq.createEntity(req1400, SendStatusMQ.REJECTED, null, null);
			else
				serviceFtsMq.createEntity(req1400, SendStatusMQ.REJECTED, null, null);
			throw e;
		} catch (Exception e) {
			throw new ChcUnrecoverableException(I18nService.ERR_PROCESSING_MESSAGE);
		}
	}

	@Override
	protected void processMessage(Byte[] bytedata) throws ChcUnrecoverableException, PrimitiveLengthMismatchException, ChcException {
		MQ1400SecSendFilereq primitiveDTO;
		primitiveDTO = getPrimitiveDto(bytedata);
		CswLog.getLogData().setService(ServiceEnum.FTS.name());
		CswLog.getLogData().setFunction("processMessage");
		CswLog.getLogData().setModule(ModuleEnum.OUTBOUND_MQ.name());
		CswLog.getLogData().setSystem(LogSystemEnum.CSW.name());
		CswLog.getLogData().setLocalBaId(primitiveDTO.getBaLoc());
		CswLog.getLogData().setRemoteBaId(primitiveDTO.getBaRem());
		CswLog.getLogData().setVfn(primitiveDTO.getVfn());
		if(primitiveDTO.getSyncFlag() == 1) {
			CswLog.getLogData().setUdr(primitiveDTO.getUdr());
			CswLog.getLogData().setService(ServiceEnum.FMS.name());
		}
		CswLog.info(log, String.format("Processing primitive id: %s", primitiveDTO.getId()));
		
		if (primitiveDTO.getSyncFlag() == 1) {
//			FMS
			serviceFmsMq.submitToHub(primitiveDTO);
		} else {
//			FTS
			serviceFtsMq.submitToHub(primitiveDTO);
		}
		CswLog.uset();
	}
	
}
