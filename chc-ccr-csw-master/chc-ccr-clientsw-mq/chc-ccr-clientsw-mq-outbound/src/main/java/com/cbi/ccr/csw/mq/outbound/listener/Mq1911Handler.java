package com.cbi.ccr.csw.mq.outbound.listener;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.mq.common.dto.mss.MQ1911SecSendMsgreq;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.mq.outbound.service.MqServiceBase;
import com.cbi.ccr.csw.mq.outbound.service.OutboundServiceMSSMQ;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;

import chc.framework.util.parsing.input.FlowioInputMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Mq1911Handler extends MqServiceBase<MQ1911SecSendMsgreq>{
	
	private FlowioInputMapper<MQ1911SecSendMsgreq> flowioMapper;
	
	@Autowired
	private OutboundServiceMSSMQ serviceMssMq;
	
	@Autowired
	protected MqPrimitiveSenderService mqPrimitiveSenderService;
	
	@PostConstruct
	private void initBinder() {
		flowioMapper = new FlowioInputMapper<>(BinderFactory.binder1911(null));
	}
	
	@Override
	protected FlowioInputMapper<MQ1911SecSendMsgreq> getFlowioMapper() {
		return flowioMapper;
	}
	
	@Override
	protected MQ1911SecSendMsgreq getPrimitiveDto(Byte[] data) throws ChcUnrecoverableException, PrimitiveLengthMismatchException, ChcException {
		
		try {
			MQ1911SecSendMsgreq req1911 = flowioMapper.read(Arrays.asList(data));

			FlowioInputMapper<MQ1911SecSendMsgreq> newFlowioMapper = new FlowioInputMapper<>(BinderFactory.binder1911(req1911.getMabLen()));

			checkSize(newFlowioMapper, data.length);
			
			req1911 = newFlowioMapper.read(Arrays.asList(data));
			
			if(req1911.getMabDigestLen() == null)
				req1911.setMabDigestLen(0);
			
			if(req1911.getLocalAuthInfoLen() == null || 
					StringUtils.isEmpty(req1911.getLocalAuthInfo()))
				req1911.setLocalAuthInfoLen(0);
			
			return req1911;
		} catch (PrimitiveLengthMismatchException e) {
			MQ1911SecSendMsgreq req1911 = flowioMapper.read(Arrays.asList(data));
			FlowioInputMapper<MQ1911SecSendMsgreq> newFlowioMapper = new FlowioInputMapper<>(BinderFactory.binder1911(req1911.getMabLen()));
			req1911 = newFlowioMapper.read(Arrays.asList(data));
			mqPrimitiveSenderService.send1921ErrorWithPrimitive(0, serviceMssMq.build1921ErrorWithPrimitive(req1911, e.getLocalizedMessage()));
			serviceMssMq.createEntity(req1911, SendStatusMQ.REJECTED, null, null);
			throw e;
		} catch (Exception e) {
			throw new ChcUnrecoverableException(I18nService.ERR_PROCESSING_MESSAGE);
		}
	}
	
	@Override
	protected void processMessage(Byte[] bytedata) throws ChcUnrecoverableException, PrimitiveLengthMismatchException, ChcException{
		
		MQ1911SecSendMsgreq primitiveDTO;
		primitiveDTO = getPrimitiveDto(bytedata);
		CswLog.getLogData().setService(ServiceEnum.MSS.name());
		CswLog.getLogData().setFunction("processMessage");
		CswLog.getLogData().setModule(ModuleEnum.OUTBOUND_MQ.name());
		CswLog.getLogData().setLocalBaId(primitiveDTO.getBaLoc());
		CswLog.getLogData().setRemoteBaId(primitiveDTO.getBaRem());
		CswLog.getLogData().setUdr(primitiveDTO.getUdr());
		CswLog.getLogData().setSystem(LogSystemEnum.CSW.name());
		CswLog.info(log, String.format("Processing primitive id: %s", primitiveDTO.getId()));
		
		serviceMssMq.submitToHub(primitiveDTO);
	}
}
