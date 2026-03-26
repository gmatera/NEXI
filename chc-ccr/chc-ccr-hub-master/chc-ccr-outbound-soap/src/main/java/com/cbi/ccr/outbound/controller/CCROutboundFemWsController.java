package com.cbi.ccr.outbound.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.OutboundLogSoapModuleEnum;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.logging.CcrLogData;
import com.cbi.ccr.common.logging.LogSystemEnum;
import com.cbi.ccr.dto.OutboundControllerPath;
import com.cbi.ccr.outbound.service.CCROutboundServiceFemWs;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.http.ChcStubException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = OutboundControllerPath.BASE + OutboundControllerPath.SOAP_MESSAGE)
public class CCROutboundFemWsController{

	@Autowired
	private CCROutboundServiceFemWs ccrOutboundServiceFemWs;
	
	@PostMapping(OutboundControllerPath.SOAP_OUTBOUND)
	public void post(HttpServletRequest request, HttpServletResponse response) throws ChcException, ChcStubException {
		
		CcrLog.setLogData(CcrLogData.builder()
				.system(LogSystemEnum.CCR.name())
				.module(OutboundLogSoapModuleEnum.SOAP_OUTBOUND.name())
				.function("post")
				.id(request.getHeader(CCROutboundServiceFemWs.X_CHC_ID))
				.build());
		
		ccrOutboundServiceFemWs.processMessage(request, response);
			
	}
	

}
