package com.cbi.ccr.csw.db.outbound.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.db.outbound.service.OutboundServiceFMSDB;
import com.cbi.ccr.csw.db.outbound.service.OutboundServiceFTSDB;
import com.cbi.ccr.csw.db.outbound.service.OutboundServiceMSSDB;
import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.dto.SubmitDTO;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.api.dto.InternalControllerPath;
import com.cbi.frw.api.dto.LivenessDTO;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.logging.LogLevel;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.stream.KeepAlive;

import liquibase.pro.packaged.lo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path=CSWOutboundControllerPath.BASE)
public class CSWOutboundControllerDB{
	
	@Autowired
	private OutboundServiceFMSDB outboundServiceFMS;
	
	@Autowired
	private OutboundServiceFTSDB outboundServiceFTS;
	
	@Autowired
	private OutboundServiceMSSDB outboundServiceMSS;
	
	
	@Autowired
	private AsyncTaskExecutor taskExecutor;
	
	@PostConstruct
	public void init() {
		log.info(Color.g(String.format("################# Controller Outbound DB %s is active", CSWOutboundControllerPath.BASE)));
	}
	
	@GetMapping(path = InternalControllerPath.LIVENESS)
	public ResponseEntity<LivenessDTO> liveness() {
		
		return ResponseEntity.ok(new LivenessDTO());
	}
	
	@PostMapping(path = CSWOutboundControllerPath.SUBMIT_FMS)
	public void submitFMS(@RequestBody SubmitDTO dto, HttpServletResponse response) throws IOException, ChcStubException, InterruptedException, ChcException {
		
		KeepAlive keepAlive = new KeepAlive(response.getWriter());
		
		CswLog.getLogData().setFunction("submitFMS");
		CswLog.getLogData().setModule(ModuleEnum.OUTBOUND_BATCH_DB.name());
		CswLog.getLogData().setService(ServiceEnum.FMS.name());
		CswLog.getLogData().setId(dto.getId().toString());
		
		try {
			response.setStatus(200);
			
			keepAlive.start(taskExecutor);
			// sending file to the CCR
			
			if(log.isDebugEnabled())
				CswLog.debug(log, "Start processing FMS");
			
			outboundServiceFMS.submitToHub(dto);
			// file sent
			// waiting KeepAlive complete
			keepAlive.waitCompletetion();
		} catch (Exception e) {
			keepAlive.waitCompletetion();
			CswLog.error(log, String.format("submit fms error: %s", e.toString()));
			response.flushBuffer();
			// non si possono lanciare eccezioni se si usa il keepAlive
		} 
	}
	
	@PostMapping(path = CSWOutboundControllerPath.SUBMIT_FTS)
	public void submitFTS(@RequestBody SubmitDTO dto, HttpServletResponse response) throws IOException, ChcStubException, InterruptedException, ChcException {
		KeepAlive keepAlive = new KeepAlive(response.getWriter());
		
		CswLog.getLogData().setFunction("submitFTS");
		CswLog.getLogData().setModule(ModuleEnum.OUTBOUND_BATCH_DB.name());
		CswLog.getLogData().setService(ServiceEnum.FTS.name());
		CswLog.getLogData().setId(dto.getId().toString());
		
		try {
			response.setStatus(200);
			
			keepAlive.start(taskExecutor);
			
			CswLog.info(log, "Start processing FTS");
			
			// sending file to the CCR
			outboundServiceFTS.submitToHub(dto);
			// file sent
			// waiting KeepAlive complete
			keepAlive.waitCompletetion();
		} catch (Exception e) {
			keepAlive.waitCompletetion();
			CswLog.error(log, String.format("submit fts error: %s", e.toString()));
			response.flushBuffer();
			// non si possono lanciare eccezioni se si usa il keepAlive
		}
	}
	
	@PostMapping(path = CSWOutboundControllerPath.SUBMIT_MSS)
	public void submitMSS(@RequestBody SubmitDTO dto, HttpServletResponse response) throws IOException, ChcStubException, InterruptedException, ChcException {
			
		KeepAlive keepAlive = new KeepAlive(response.getWriter());
	
		CswLog.getLogData().setFunction("submitMSS");
		CswLog.getLogData().setModule(ModuleEnum.OUTBOUND_BATCH_DB.name());
		CswLog.getLogData().setService(ServiceEnum.MSS.name());
		CswLog.getLogData().setId(dto.getId().toString());
		
		try {
			response.setStatus(200);
			keepAlive.start(taskExecutor);
			
			CswLog.info(log, "Start processing MSS");
			
			// sending file to the CCR
			outboundServiceMSS.submitToHub(dto);
			// file sent
			// waiting KeepAlive complete
			keepAlive.waitCompletetion();
			
		} catch (Exception e) {
			keepAlive.waitCompletetion();
			CswLog.error(log, String.format("submit mss error: %s", e.toString()));
			response.flushBuffer();
			// non si possono lanciare eccezioni se si usa il keepAlive
		}
	}
}
