package com.cbi.ccr.csw.mq.outbound.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.dto.SubmitDTO;
import com.cbi.ccr.csw.mq.outbound.service.OutboundServiceFMSMQ;
import com.cbi.ccr.csw.mq.outbound.service.OutboundServiceFTSMQ;
import com.cbi.ccr.csw.mq.outbound.service.OutboundServiceMSSMQ;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.api.controller.InternalControllerAbstract;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.stream.KeepAlive;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path=CSWOutboundControllerPath.BASE_MQ)
public class CSWOutboundMQController extends InternalControllerAbstract{
	
	@Autowired
	private OutboundServiceFMSMQ outboundServiceFMS;
	
	@Autowired
	private OutboundServiceFTSMQ outboundServiceFTS;
	
	@Autowired
	private OutboundServiceMSSMQ outboundServiceMSS;
	
	@Autowired
	private AsyncTaskExecutor taskExecutor;
	
	private static final String RETRY = "processMessage-retry";
	
	@PostConstruct
	public void init() {
		log.info(Color.g(String.format("################# Controller Outbound MQ %s is active", CSWOutboundControllerPath.BASE_MQ)));
	}
	
	@PostMapping(path = CSWOutboundControllerPath.SUBMIT_FMS)
	public void submitFMS(@RequestBody SubmitDTO dto, HttpServletResponse response) throws IOException{
		
		KeepAlive keepAlive = new KeepAlive(response.getWriter());
		try {
			response.setStatus(200);
			
			keepAlive.start(taskExecutor);
			
			CswLog.getLogData().setModule(ModuleEnum.OUTBOUND_MQ.name());
			CswLog.getLogData().setService(ServiceEnum.FMS.name());
			CswLog.getLogData().setId(dto.getId().toString());
			CswLog.getLogData().setFunction(RETRY);
			
			// sending file to the CCR
			outboundServiceFMS.submitToHub(dto);
			// file sent
			// waiting KeepAlive complete
			keepAlive.waitCompletetion();
		} catch (Exception e) {
			keepAlive.waitCompletetion();
			CswLog.error(log, String.format("submit fms error: %s", e.toString()));
			// response.flushBuffer();
			// non si possono lanciare eccezzioni se si usa il keepAlive
		} 
	}
	
	@PostMapping(path = CSWOutboundControllerPath.SUBMIT_FTS)
	public void submitFTS(@RequestBody SubmitDTO dto, HttpServletResponse response) throws IOException, ChcStubException, InterruptedException, ChcException {
		KeepAlive keepAlive = new KeepAlive(response.getWriter());
		
		try {
			response.setStatus(200);
			
			keepAlive.start(taskExecutor);
			
			CswLog.getLogData().setModule(ModuleEnum.OUTBOUND_MQ.name());
			CswLog.getLogData().setService(ServiceEnum.FTS.name());
			CswLog.getLogData().setId(dto.getId().toString());
			CswLog.getLogData().setFunction(RETRY);
			
			// sending file to the CCR
			outboundServiceFTS.submitToHub(dto);
			// file sent
			// waiting KeepAlive complete
			keepAlive.waitCompletetion();
		} catch (Exception e) {
			keepAlive.waitCompletetion();
			CswLog.error(log, String.format("submit fts error: %s", e.toString()));
			response.flushBuffer();
			// non si possono lanciare eccezzioni se si usa il keepAlive
		} 
	}
	
	@PostMapping(path = CSWOutboundControllerPath.SUBMIT_MSS)
	public void submitMSS(@RequestBody SubmitDTO dto, HttpServletResponse response) throws IOException, ChcStubException, InterruptedException, ChcException {
			
		KeepAlive keepAlive = new KeepAlive(response.getWriter());
		try {
			response.setStatus(200);
			keepAlive.start(taskExecutor);
			
			CswLog.getLogData().setModule(ModuleEnum.OUTBOUND_MQ.name());
			CswLog.getLogData().setService(ServiceEnum.MSS.name());
			CswLog.getLogData().setId(dto.getId().toString());
			CswLog.getLogData().setFunction(RETRY);
			
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
