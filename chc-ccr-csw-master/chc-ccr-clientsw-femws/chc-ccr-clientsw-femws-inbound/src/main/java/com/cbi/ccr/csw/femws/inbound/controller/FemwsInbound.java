package com.cbi.ccr.csw.femws.inbound.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundRequestDTO;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundResponseDTO;
import com.cbi.ccr.csw.femws.inbound.service.FemwsInboundService;
import com.cbi.ccr.csw.service.common.JweJwtService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(CSWInboundControllerPath.BASE+CSWInboundControllerPath.SOAP_INBOUND)
public class FemwsInbound {
	
	@Autowired
	private FemwsInboundService femwsService;
	@Value("${csw_version}")
	private String cswVersion;
	
	@Autowired
	private JweJwtService jweJwtService;
	
	
	@PostConstruct
	void init() {
		
	}
	
	@GetMapping
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.addHeader("FEMS-WS-version", cswVersion);
        response.sendError(405, "Method not allowed. This service expects POST requests");
	}
	
	
	@PostMapping()
	public ResponseEntity<String> doPost(@RequestBody String jwe, HttpServletResponse response) throws ChcException, ChcStubException {
		
		
		FemsWsInboundRequestDTO messageDTO;
		FemsWsInboundResponseDTO resp;
		String responseJWE;
		if(jweJwtService.isSecurityEnabled()) {
			messageDTO = JSON.fromJson(jweJwtService.getDeserializedDecriptedJWE(jwe), FemsWsInboundRequestDTO.class);
		} else {
			messageDTO = JSON.fromJson(jwe, FemsWsInboundRequestDTO.class);
		}
		
		CswLog.getLogData().setModule(ModuleEnum.SOAP_INBOUND.name());
		CswLog.getLogData().setFunction("post");
		
		resp = femwsService.processInbound(messageDTO);

		if(jweJwtService.isSecurityEnabled()) {
			responseJWE = jweJwtService.getSerializedEncryptedJWE(JSON.toJson(resp));
		}else {
			responseJWE = JSON.toJson(resp);
		}
		
		return ResponseEntity.ok(responseJWE);

	}
	
	
}
