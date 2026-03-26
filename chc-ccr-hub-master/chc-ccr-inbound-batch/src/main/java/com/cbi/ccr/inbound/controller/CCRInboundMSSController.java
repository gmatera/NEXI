package com.cbi.ccr.inbound.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

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
import com.cbi.ccr.csw.dto.fms.MSSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.ccr.inbound.LogInboundBatchModuleEnum;
import com.cbi.ccr.inbound.service.CCRIndoundServiceMSS;
import com.cbi.ccr.inbound.util.LocalCache;
import com.cbi.frw.http.stream.LongProcessingHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = InboundControllerPath.BASE + InboundControllerPath.MSS_MESSAGE_ID)
public class CCRInboundMSSController extends CCRInboundController{

	@Autowired
	private CCRIndoundServiceMSS ccrIndoundServiceMSS;

	
	@PostMapping(
			consumes = { MediaType.APPLICATION_OCTET_STREAM_VALUE,
					MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public void uploadMss(
			@PathVariable(name = "id") String id,
			@RequestPart(name = "message", required = true) MultipartFile message,
			@RequestPart("wrapper") @NotNull MessageWrapperDTO wrapper,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		CcrLog.setLogData(CcrLogData.builder()
				.system(LogSystemEnum.CCR.name())
				.module(LogInboundBatchModuleEnum.INBOUND_BATCH.name())
				.function("uploadMss")
				.build());
		
		// TEST-SIMULATION
		if(Boolean.TRUE.equals(integrationTestActive)) {
			try {
				MSSMessageDTO dto = ccrIndoundServiceMSS.decrypt(wrapper, MSSMessageDTO.class);
				
				// attenzione, max_retry_attempts deve essere >= 2
//				if(handleReadTimeout(dto.getVfn()) || handleServiceUnavailable(dto.getVfn(), response)) {
//					return;
//				}
			} catch (Exception e) {
				LocalCache.getInstance().clear();
			}
		} 
		
		LongProcessingHandler.writeTo2(response, taskExecutor, () -> ccrIndoundServiceMSS.processMessage(wrapper, MSSMessageDTO.class, message), CcrLog.getLogData(), log);

	}

}
