package com.cbi.ccr.csw.db.inbound.controller;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.db.common.CommonConfigurationServiceDB;
import com.cbi.ccr.csw.db.inbound.service.CSWIndoundNotificationServiceFMSDB;
import com.cbi.ccr.csw.db.inbound.service.CSWIndoundNotificationServiceFTSDB;
import com.cbi.ccr.csw.db.inbound.service.CSWIndoundNotificationServiceMSSDB;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.dto.NotificationDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.inbound.service.CSWIndoundServiceFMSMQ;
import com.cbi.ccr.csw.mq.inbound.service.CswInboundNotificationServiceFMSMQ;
import com.cbi.ccr.csw.mq.inbound.service.CswInboundNotificationServiceFTSMQ;
import com.cbi.ccr.csw.mq.inbound.service.CswInboundNotificationServiceMSSMQ;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.util.Color;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(CSWInboundControllerPath.BASE)
public class CSWNotificationController  {
	
	@Autowired
	private CSWIndoundNotificationServiceFMSDB cswInboundNotificationServiceFMSDB;
	
	@Autowired
	private CSWIndoundNotificationServiceFTSDB cswInboundNotificationServiceFTSDB;
	
	@Autowired
	private CSWIndoundNotificationServiceMSSDB cswInboundNotificationServiceMSSDB;
	
	private CswInboundNotificationServiceFMSMQ cswInboundNotificationServiceFMSMQ;
	private CswInboundNotificationServiceFTSMQ cswInboundNotificationServiceFTSMQ;
	private CswInboundNotificationServiceMSSMQ cswInboundNotificationServiceMSSMQ;
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private CommonConfigurationServiceDB configurationService;
	
	@PostConstruct
	public void init() {
		log.info(Color.g(String.format("################# Controller Inbound Notifications %s is active", CSWInboundControllerPath.BASE)));
	}
	
	@PostMapping(CSWInboundControllerPath.NOTIFICATION)
	public ResponseEntity<Void> notify(@RequestBody @Valid NotificationDTO notificationDTO) throws ChcException {
		
		RouteInterface serviceInterface = configurationService.getInterfaceFromConfiguration(notificationDTO);
		
		CswLog.getLogData().setModule(ModuleEnum.INBOUND_BATCH_DB.name());
		if(serviceInterface.equals(RouteInterface.MQ))
			CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
		
		CswLog.getLogData().setService(notificationDTO.getService().toString());

		CswLog.getLogData().setLocalBaId(notificationDTO.getLocalBaId());
		CswLog.getLogData().setRemoteBaId(notificationDTO.getRemoteBaId());
		CswLog.getLogData().setFunction("notification");
		CswLog.getLogData().setId(notificationDTO.getMessageKey().toString());
		
		switch(notificationDTO.getService()) {
		case FMS:
			if (serviceInterface.equals(RouteInterface.DB))
				cswInboundNotificationServiceFMSDB.processNotification(notificationDTO);
			else if (serviceInterface.equals(RouteInterface.MQ)) {
				if(cswInboundNotificationServiceFMSMQ == null) {
					cswInboundNotificationServiceFMSMQ = context.getBean(CswInboundNotificationServiceFMSMQ.class);
				}
				cswInboundNotificationServiceFMSMQ.processNotification(notificationDTO);
			}
			break;
		case FTS:
		case AON:
			if (serviceInterface.equals(RouteInterface.DB) || serviceInterface.equals(RouteInterface.FS))
				cswInboundNotificationServiceFTSDB.processNotification(notificationDTO);
			else if (serviceInterface.equals(RouteInterface.MQ)) {
				if(cswInboundNotificationServiceFTSMQ == null) {
					cswInboundNotificationServiceFTSMQ = context.getBean(CswInboundNotificationServiceFTSMQ.class);
				}
				cswInboundNotificationServiceFTSMQ.processNotification(notificationDTO);
			}
			break;
		case MSS:
			if (serviceInterface.equals(RouteInterface.DB))
				cswInboundNotificationServiceMSSDB.processNotification(notificationDTO);
			else if (serviceInterface.equals(RouteInterface.MQ)) {
				if(cswInboundNotificationServiceMSSMQ == null) {
					cswInboundNotificationServiceMSSMQ = context.getBean(CswInboundNotificationServiceMSSMQ.class);
				}
				cswInboundNotificationServiceMSSMQ.processNotification(notificationDTO);
			}
			break;
		default:
			CswLog.error(log, String.format("Service not valid: %s", notificationDTO.getService()));
			break;
		}
		return ResponseEntity.ok().build();
	}
	

	
	
}
