package com.cbi.ccr.inbound.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.logging.CcrLogData;
import com.cbi.ccr.common.logging.LogSystemEnum;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.ccr.inbound.LogInboundBatchModuleEnum;
import com.cbi.ccr.inbound.service.CCRIndoundServiceFMS;
import com.cbi.ccr.inbound.util.LocalCache;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.http.stream.LongProcessingHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = InboundControllerPath.BASE + InboundControllerPath.FMS_MESSAGE_ID)
public class CCRInboundFMSController extends CCRInboundController{
	
	@Autowired
	private CCRIndoundServiceFMS service;

	@PostMapping( 
			consumes = { MediaType.APPLICATION_OCTET_STREAM_VALUE,
					MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public void uploadFms(
			@PathVariable(name = "id") String id,
			@RequestPart(name = "message", required = true) MultipartFile message,
			@RequestPart(name = "file", required = true) MultipartFile file,
			@RequestPart("wrapper") MessageWrapperDTO wrapper,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ChcException {
		
		CcrLog.setLogData(CcrLogData.builder()
				.system(LogSystemEnum.CCR.name())
				.module(LogInboundBatchModuleEnum.INBOUND_BATCH.name())
				.function("uploadFms")
				.build());
				
		// TEST-SIMULATION
		if(Boolean.TRUE.equals(integrationTestActive)) {
			try {
				FMSMessageDTO dto = service.decrypt(wrapper, FMSMessageDTO.class);
				
				// attenzione, max_retry_attempts deve essere >= 2
				if(handleReadTimeout(dto.getVfn()) || handleServiceUnavailable(dto.getVfn(), response)) {
					return;
				}
			} catch (Exception e) {
				LocalCache.getInstance().clear();
			}
		} 
		
		LongProcessingHandler.writeTo2(response, taskExecutor, () -> service.processMessage(wrapper, FMSMessageDTO.class, message, file), CcrLog.getLogData(), log);
	}
	


}
