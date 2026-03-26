package com.cbi.ccr.csw.mq.outbound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cbi.ccr.csw.mq.common.dto.PrimitiveMQ;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveConfigurationLoader;
import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;
import com.cbi.frw.persistence.service.AbstractService;
import com.cbi.frw.persistence.service.GenericDAO;

import chc.framework.util.parsing.input.FlowioFixedPositionBinder;
import chc.framework.util.parsing.input.FlowioInputMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MqServiceBase<P extends PrimitiveMQ> extends AbstractService {

	@Autowired
	protected JmsTemplate jmsTemplate;
	
	@Autowired
	protected GenericDAO genericDAO;
	
	@Autowired
	protected MqPrimitiveConfigurationLoader mqPrimitiveConfigurationLoader;
	
	protected abstract FlowioInputMapper<P> getFlowioMapper();

	protected abstract P getPrimitiveDto(Byte[] data) throws ChcUnrecoverableException, PrimitiveLengthMismatchException, ChcException;
	
	protected abstract void processMessage(Byte[] bytedata) throws ChcUnrecoverableException, PrimitiveLengthMismatchException, ChcException;

	protected void checkSize(FlowioInputMapper<P> flowioMapper, int aspectedLengh) throws PrimitiveLengthMismatchException {
		if( ((FlowioFixedPositionBinder<P>) flowioMapper.getFlowioBinder()).getSize() != aspectedLengh) {
			throw new PrimitiveLengthMismatchException(I18nService.ERR_MQ_PRIMITIVE_LENGTH_MISMATCH, 
					((FlowioFixedPositionBinder<P>) flowioMapper.getFlowioBinder()).getSize(), aspectedLengh);
		}

	}
}
