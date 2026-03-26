package com.cbi.ccr.csw.db.inbound.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cbi.ccr.csw.db.common.CommonConfigurationServiceDB;
import com.cbi.ccr.csw.db.inbound.service.CSWIndoundServiceFMSDB;
import com.cbi.ccr.csw.db.inbound.service.CSWIndoundServiceFTSDB;
import com.cbi.ccr.csw.db.inbound.service.CSWIndoundServiceMSSDB;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.mq.inbound.service.CSWIndoundServiceFMSMQ;
import com.cbi.ccr.csw.mq.inbound.service.CSWIndoundServiceFTSMQ;
import com.cbi.ccr.csw.mq.inbound.service.CSWIndoundServiceMSSMQ;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.logging.LogLevel;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.http.stream.LongProcessingHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = CSWInboundControllerPath.BASE)
public class CSWInboundController {

	@Autowired
	private CommonConfigurationServiceDB configurationService;

	@Autowired
	private CSWIndoundServiceFMSDB cswIndoundServiceFMSDB;

	@Autowired
	private CSWIndoundServiceFTSDB cswIndoundServiceFTSDB;

	@Autowired
	private CSWIndoundServiceMSSDB cswIndoundServiceMSSDB;
	

	private CSWIndoundServiceFMSMQ cswIndoundServiceFMSMQ;
	private CSWIndoundServiceFTSMQ cswIndoundServiceFTSMQ;
	private CSWIndoundServiceMSSMQ cswIndoundServiceMSSMQ;

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private AsyncTaskExecutor taskExecutor;
	
	@PostConstruct
	public void init() {
		log.info(Color.g(String.format("################# Controller Inbound %s is active", CSWInboundControllerPath.BASE)));
	}

	@PostMapping(path = CSWInboundControllerPath.FMS_RECEIVE_ID, consumes = { MediaType.APPLICATION_OCTET_STREAM_VALUE,
			MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	public void uploadFms(@PathVariable(name = "phyMsgId") Long phyMsgId,
			@RequestPart(name = "message", required = true) MultipartFile message,
			@RequestPart(name = "file", required = true) MultipartFile file,
			@RequestPart("wrapper") @Valid MessageWrapperDTO wrapper, HttpServletResponse response) throws IOException, ChcException {

		CswLog.getLogData().setFunction("uploadFms");
		CswLog.getLogData().setService(ServiceEnum.FMS.name());
		CswLog.getLogData().setId(phyMsgId.toString());
		
		RouteInterface fmsInterface = configurationService.getInterfaceFromConfigurationFMS(wrapper);
		if (fmsInterface == RouteInterface.DB) {
			CswLog.getLogData().setModule(ModuleEnum.INBOUND_BATCH_DB.name());
			LongProcessingHandler.writeTo2(response, taskExecutor, () -> cswIndoundServiceFMSDB
					.processFMSMessage(message.getInputStream(), file.getInputStream(), wrapper, phyMsgId),
					CswLog.getLogData(), log);
		} else {
			
			if(cswIndoundServiceFMSMQ == null) {
				cswIndoundServiceFMSMQ = context.getBean(CSWIndoundServiceFMSMQ.class);
			}
			CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
			CswLog.getLogData().setFunction("uploadFmsMQ");
			LongProcessingHandler.writeTo2(response, taskExecutor, () -> cswIndoundServiceFMSMQ
					.processFMSMessage(message.getInputStream(), file.getInputStream(), wrapper, phyMsgId),
					CswLog.getLogData(), log);
		}
	}

	@PostMapping(path = CSWInboundControllerPath.FTS_RECEIVE_ID, consumes = { MediaType.APPLICATION_OCTET_STREAM_VALUE,
			MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	public void uploadFts(@PathVariable(name = "phyMsgId") Long phyMsgId,
			@RequestPart(name = "file", required = true) MultipartFile file,
			@RequestPart("wrapper") MessageWrapperDTO wrapper, HttpServletResponse response) throws IOException, ChcException {
		
		CswLog.getLogData().setFunction("uploadFts");
		CswLog.getLogData().setService(ServiceEnum.FTS.name());
		CswLog.getLogData().setId(phyMsgId.toString());
				
		RouteInterface ftsInterface = configurationService.getInterfaceFromConfigurationFTS(wrapper);
		
		if(ftsInterface.equals(RouteInterface.FS)) {
			CswLog.getLogData().setService(ServiceEnum.AON.name());
		}
		
		if (ftsInterface.equals(RouteInterface.DB) || ftsInterface.equals(RouteInterface.FS)) {
			CswLog.getLogData().setModule(ModuleEnum.INBOUND_BATCH_DB.name());
			LongProcessingHandler.writeTo2(response, taskExecutor,
					() -> cswIndoundServiceFTSDB.processFTSMessage(file.getInputStream(), wrapper, phyMsgId),
					CswLog.getLogData(), log);
		} else {
			
			if(cswIndoundServiceFTSMQ == null) {
				cswIndoundServiceFTSMQ = context.getBean(CSWIndoundServiceFTSMQ.class);
			}
			
			CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
			LongProcessingHandler.writeTo2(response, taskExecutor,
					() -> cswIndoundServiceFTSMQ.processFTSMessage(file.getInputStream(), wrapper, phyMsgId),
					CswLog.getLogData(), log);
		}
	}

	@PostMapping(path = CSWInboundControllerPath.MSS_RECEIVE_ID, consumes = { MediaType.APPLICATION_OCTET_STREAM_VALUE,
			MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	public void uploadMss(@PathVariable(name = "phyMsgId") Long phyMsgId,
			@RequestPart(name = "message", required = true) MultipartFile message,
			@RequestPart("wrapper") MessageWrapperDTO wrapper, HttpServletResponse response) throws IOException, ChcException {
		
		CswLog.getLogData().setFunction("uploadMss");
		CswLog.getLogData().setService(ServiceEnum.MSS.name());
		CswLog.getLogData().setId(phyMsgId.toString());
			
		RouteInterface mssInterface = configurationService.getInterfaceFromConfigurationMSS(wrapper);
		if (mssInterface == RouteInterface.DB) {
			CswLog.getLogData().setModule(ModuleEnum.INBOUND_BATCH_DB.name());
			LongProcessingHandler.writeTo2(response, taskExecutor,
					() -> cswIndoundServiceMSSDB.processMSSMessage(message.getInputStream(), wrapper, phyMsgId),
					CswLog.getLogData(), log);
		} else {
			
			if(cswIndoundServiceMSSMQ == null) {
				cswIndoundServiceMSSMQ = context.getBean(CSWIndoundServiceMSSMQ.class);
			}
			
			CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
			LongProcessingHandler.writeTo2(response, taskExecutor,
					() -> cswIndoundServiceMSSMQ.processMSSMessage(message.getInputStream(), wrapper, phyMsgId),
					CswLog.getLogData(), log);
		}
	}
}
