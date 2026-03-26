package com.cbi.ccr.outbound.mq;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.logging.CcrLogData;
import com.cbi.ccr.common.logging.LogSystemEnum;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;
import com.cbi.ccr.dto.mq.command.orch.ChcCommandWrapper;
import com.cbi.ccr.dto.mq.command.orch.CommandName;
import com.cbi.ccr.outbound.LogOutboundBatchModuleEnum;
import com.cbi.ccr.outbound.service.FMSOutboundService;
import com.cbi.ccr.outbound.service.FTSOutboundService;
import com.cbi.ccr.outbound.service.MSSOutboundService;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.persistence.service.AbstractService;

import liquibase.pro.packaged.lo;
import lombok.extern.slf4j.Slf4j;


/**
 * Consumes messages sent from Orchestrator
 * @author mek
 *
 */
@Slf4j
@Component
public class OrchestratorConsumer extends AbstractService {
	
	@Autowired
	private FMSOutboundService fmsService;
	
	@Autowired
	private FTSOutboundService ftsService;
	
	@Autowired
	private MSSOutboundService mssService;

	@Value("${ccr_queue}")
	private String queue;
	
	@PostConstruct
	public void inint() {
		log.info("ccr_queue {}", queue);
	}
			
	@RabbitListener(queues = "${ccr_queue}", concurrency = "${outbound_mq_listener_concurrency}")
	public void receiver(String fileBody) {
		try {
			ChcCommandWrapper commandWrapper = JSON.fromJson(fileBody, ChcCommandWrapper.class);
			ChcCommand command = commandWrapper.getChcCommand();
			
			CCRtransportData transportData = command.getCcrTransportData();
			
			CcrLog.setLogData(CcrLogData.builder()
					.system(LogSystemEnum.CCR.name())
					.module(LogOutboundBatchModuleEnum.OUTBOUND_BATCH.name())
					.function("receive_ORCH_"+command.getCmdName())
					.service(transportData.getServiceType().name())
					.id(command.getPhyFileDescriptor().getChcPhyMsgId())
					.build());
			
			if(log.isDebugEnabled()) {
				CcrLog.getLogData().setMessage(String.format("message received from Orch: %s", JSON.toJson(commandWrapper)));
				CcrLog.debug(log);
				
			}else {
				CcrLog.getLogData().setMessage("message received from Orch:");
				CcrLog.info(log);
			}
			
			
			switch (command.getCmdName()) {
			case CommandName.BATCH_FLOW:
				checkAndProcessBatchFlow(command);
				break;
			case CommandName.POSITIVE_NOTIFY_FLOW:
				processNotification(command.getPhyFileDescriptor().getChcPhyMsgId(), command.getCmdTS(), transportData.getServiceType(), true);
				break;
			case CommandName.NEGATIVE_NOTIFY_FLOW:
				processNotification(command.getPhyFileDescriptor().getChcPhyMsgId(), command.getCmdTS(),  transportData.getServiceType(), false);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			CcrLog.getLogData().setMessage(String.format("Invalid data received from Orch: %s", e.toString()));
			CcrLog.error(log, e);
		}
	}

	private void checkAndProcessBatchFlow(ChcCommand command) {
		switch (command.getCcrTransportData().getServiceType()) {
		case FMS:
			fmsService.processOutboundMessage(command);
			break;
		case FTS:
		case AON:
			ftsService.processOutboundMessage(command);
			break;
		case MSS:
			mssService.processOutboundMessage(command);
			break;
		default:
			break;
		}
	}

	private void processNotification(String id,  String cmdTms, ServiceType serviceType, boolean esito) {
		
		Long phyMsgId = Long.valueOf(id);
		switch (serviceType) {
		case FMS:
			fmsService.processAndForwardNotification(phyMsgId, cmdTms, esito);
			break;
		case FTS:
		case AON:
			ftsService.processAndForwardNotification(phyMsgId, cmdTms, esito, serviceType);
			break;
		case MSS:
			mssService.processAndForwardNotification(phyMsgId, cmdTms, esito);
			break;
		default:
			break;
		}
			
	}

}
