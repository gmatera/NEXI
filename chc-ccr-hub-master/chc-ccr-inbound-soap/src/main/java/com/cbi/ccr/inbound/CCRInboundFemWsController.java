package com.cbi.ccr.inbound;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.InboundLogSoapModuleEnum;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.logging.CcrLogData;
import com.cbi.ccr.common.logging.LogSystemEnum;
import com.cbi.ccr.csw.femws.InboundControllerPathSoap;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundRequestDTO;
import com.cbi.ccr.csw.femws.dto.FemsWsOutboundRequestDTO;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.http.ChcStubException;

@RestController
@RequestMapping(path = InboundControllerPathSoap.BASE + InboundControllerPathSoap.SOAP_MESSAGE)
public class CCRInboundFemWsController{

	@Autowired
	private CCRInboundServiceFemWs ccrInboundServiceFemWs;
	
	@PostMapping( path=InboundControllerPathSoap.SOAP_INBOUND)
	public ResponseEntity<FemsWsInboundRequestDTO>post(@RequestHeader(name = "x-chc-t1", required = false) String xChcT1, @RequestBody FemsWsOutboundRequestDTO messageDTO) throws ChcException, ChcStubException {
		
		CcrLog.setLogData(CcrLogData.builder()
				.system(LogSystemEnum.CCR.name())
				.module(InboundLogSoapModuleEnum.SOAP_INBOUND.name())
				.function("post")
				.build());
		
		return ResponseEntity.ok(ccrInboundServiceFemWs.sendToORchestrator(xChcT1, messageDTO));
	}
	
	@GetMapping(path=InboundControllerPathSoap.SOAP_INBOUND)
	public void	get(HttpServletResponse response) throws IOException {
		response.sendError(405, "Method not allowed. This service expects POST requests");
	}
}
