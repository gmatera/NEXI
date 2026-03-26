package com.cbi.ccr.orc;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.cbi.ccr.common.EventService;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.udr.UdrUtils;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.ClientMessageStatus;
import com.cbi.ccr.domain.ClientMessageSubStatus;
import com.cbi.ccr.domain.CommonEntity;
import com.cbi.ccr.domain.CommonMessageRepository;
import com.cbi.ccr.dto.mq.command.dashboard.ChcEvent;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;
import com.cbi.ccr.dto.mq.command.orch.ChcCommandWrapper;
import com.cbi.ccr.dto.mq.command.orch.ChcId;
import com.cbi.ccr.dto.mq.command.orch.CommandName;
import com.cbi.ccr.dto.mq.command.orch.UDRunbulked;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.persistence.service.AbstractService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class OrchestratorService<T extends CommonEntity, E extends EventService<T>, R extends CommonMessageRepository<T>> extends AbstractService{

	private E eventService;
	private R repository;
	
	@Value("${orchestrator_exchange}")
	private String orchestratorExchange;
	
	@Value("${orchestrator_routing_key}")
	private String orchestratorRoutingKey;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	protected OrchestratorService(E eventService, R repository) {
		this.eventService = eventService;
		this.repository = repository; 
	}
	
	public abstract ChcCommand buildCommand(T clientMessage);
	
	protected ChcCommand buildCommonCommand(T message) {
//		CommandSpecificInfoList phyFileDescriptorContainer = CommandSpecificInfoList.builder()
//				.key("phyFileDescriptor")
//				.type("*/phyFileDescriptorType")
//				.value(JSON.toJson(mapPhyFileDescriptDto(message)))
//				.build();
//
//		CommandSpecificInfoList ccrTramsportContainer = CommandSpecificInfoList.builder()
//				.key("CCRtransportData")
//				.type("*/*/CCRtransportDataType")
//				.value(JSON.toJson(mapCCRTransportData(message)))
//				.build();
//
//		CommandSpecificInfo commandSpecificInfo = CommandSpecificInfo.builder()
//				.group(ChcCommand.PHY_FILE_DESCRIPTOR_GROUP)
//				.data(Arrays.asList(phyFileDescriptorContainer, ccrTramsportContainer))
//				.build();
		
		return ChcCommand.builder()
				.cmdTS(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(ZonedDateTime.now()))
				.cmdVersion(ChcCommand.COMMAND_VERSION)
				.chcIds(Arrays.asList(ChcId.builder().id(message.getChcId().toString()).build())) // CHCID correlation ID
				.phyFileDescriptor(eventService.mapPhyFileDescriptDto(message, message.getSubStatus().name()))
				.ccrTransportData(eventService.mapCCRTransportData(message))
				.build();
	}
	
	protected void addUdrToCommand(ChcCommand command, String udr) {
		UDRunbulked parsedUdr = UdrUtils.parseUDR(udr);
		command.setSvcPhySender(parsedUdr.getSvcPhySender());
		command.setSvcPhyReceiver(parsedUdr.getSvcPhyReceiver());
		command.setSvcLogSender(parsedUdr.getSvcLogSender());
		command.setSvcLogReceiver(parsedUdr.getSvcLogReceiver());
	}
	
	protected ChcCommandWrapper buildForOrchestrator(ChcCommand batchCommand) {
		batchCommand.setCmdName(CommandName.BATCH_FLOW);
		batchCommand.setWfSenderSt(ChcCommand.CCR);
		batchCommand.setWfReceiverSt(ChcCommand.ORC);
		
		return new ChcCommandWrapper(batchCommand);
		
	}
	
	public void inboundSendToOrchestrator(T clientMessage) {
		ChcCommand batchCommand = buildCommand(clientMessage);
		
		if(batchCommand.getCcrTransportData().getServiceType().equals(ServiceType.AON)) {
			batchCommand.getPhyFileDescriptor().setCsc("301");
			batchCommand.getPhyFileDescriptor().setQtm("01");
		}
		
		ChcCommandWrapper chcCommandWrapper = buildForOrchestrator(batchCommand);
		//log.debug("CCR-INDOUND {} command for ORCH created. Tring to send chcPhyMsgId {}", batchCommand.getCcrTransportData().getServiceType(), batchCommand.getPhyFileDescriptor().getChcPhyMsgId());

		try {
			rabbitTemplate.convertAndSend(orchestratorExchange, orchestratorRoutingKey, JSON.toJson(chcCommandWrapper));
			
			
			
			if(log.isDebugEnabled()) {
				CcrLog.getLogData().setMessage(String.format("Sent to Orchestrator msg:%s", JSON.toJson(chcCommandWrapper)));	
				CcrLog.debug(log);
			}else {
				CcrLog.getLogData().setMessage("Sent to Orchestrator");
				CcrLog.info(log);
			}
			
			transactionTemplate.executeWithoutResult(
					t -> repository.setSubStatus(ClientMessageSubStatus.SENT_TO_ORCHESTRATOR, clientMessage.getId()));
			
			// send Event BatchFlowSent
			eventService.sendEvent(clientMessage, ChcEvent.BATCH_FLOW_SENT, ChcEvent.CCR, ChcEvent.ORC, null, null, false, null, null);
			
		} 
		
		
		catch (Exception e) {

			CcrLog.getLogData().setMessage(String.format("unable to send message to ORC:%s", e.toString()));
			CcrLog.error(log, e);
			
			transactionTemplate.executeWithoutResult(t -> repository.setStatusAndLog(ClientMessageStatus.FAILED, e.getMessage(), clientMessage.getId()));
			eventService.sendEvent(clientMessage, ChcEvent.ERROR_ON_PROCESSING, ChcEvent.CSW, null, null, null, false, ChcEvent.ERROR_ON_PROCESSING, e.getLocalizedMessage());
		}
	}
	
	
	public void outboundSendNotificationToOrchestrator(T message, boolean success) {
		try {
	
			ChcCommand notifyCmd = buildCommand(message);
			if(success) {
				notifyCmd.setCmdName(CommandName.POSITIVE_NOTIFY_FLOW);
			}else {
				notifyCmd.setCmdName(CommandName.NEGATIVE_NOTIFY_FLOW);
			}
			notifyCmd.setWfSenderSt(ChcCommand.CCR);	
			notifyCmd.setWfReceiverSt(ChcCommand.ORC);	
			
			ChcCommandWrapper notificationWrapper = new ChcCommandWrapper(notifyCmd);
			rabbitTemplate.convertAndSend(orchestratorExchange, orchestratorRoutingKey, JSON.toJson(notificationWrapper));
			

			if(log.isDebugEnabled()) {
				CcrLog.getLogData().setMessage(String.format("%s: %s", success?  "POSITIVE_NOTIFY_FLOW":"NEGATIVE_NOTIFY_FLOW", JSON.toJson(notifyCmd)));
				CcrLog.debug(log);
			}else {
				CcrLog.getLogData().setMessage(success?  "POSITIVE_NOTIFY_FLOW":"NEGATIVE_NOTIFY_FLOW");
				CcrLog.info(log);
			}
			
			if(success) {
				eventService.sendEvent(message, ChcEvent.POSITIVE_NOTIFICATION_SENT, ChcEvent.CCR, ChcEvent.ORC, null, null, false, null, null);
			}else {
				eventService.sendEvent(message, ChcEvent.NEGATIVE_NOTIFICATION_SENT, ChcEvent.CCR, ChcEvent.ORC, null, null, false, null, null);
			}
			transactionTemplate.executeWithoutResult( t -> repository.setSubStatus(ClientMessageSubStatus.NOTIFICATION_SENT_TO_ORCHESTRATOR, message.getId()));	

		}catch (AmqpException e) {

			transactionTemplate.executeWithoutResult(t -> repository.setStatusAndLog(ClientMessageStatus.FAILED, e.getMessage(), message.getId()));

		}
	}
}
