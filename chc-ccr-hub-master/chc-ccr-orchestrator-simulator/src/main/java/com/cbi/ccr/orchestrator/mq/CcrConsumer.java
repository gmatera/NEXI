package com.cbi.ccr.orchestrator.mq;

import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;
import com.cbi.ccr.dto.mq.command.orch.ChcCommandWrapper;
import com.cbi.ccr.dto.mq.command.orch.CommandName;
import com.cbi.frw.common.json.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * Consumes messages sent from CCR
 * @author 
 *
 */
@Slf4j
@Profile("orch")
@Service
public class CcrConsumer {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private ModelMapper mapper;

	@Value("${ccr_exchange}")
	private String ccrExchange;
	
	@Value("${ccr_routing_key}")
	private String ccrRoutingKey;

	@RabbitListener(queues = "${orchestrator_queue}")
	public void receiver(String fileBody) {
		ChcCommandWrapper commandWrapper = JSON.fromJson(fileBody, ChcCommandWrapper.class);
		ChcCommand command = commandWrapper.getChcCommand();

		log.info("message: {}", JSON.toJson(fileBody));
		switch (command.getCmdName()) {
		case CommandName.BATCH_FLOW:
			sendToCCR(command);
			break;

		case CommandName.POSITIVE_NOTIFY_FLOW:
			sendNotification(command, CommandName.POSITIVE_NOTIFY_FLOW);
			break;

		case CommandName.NEGATIVE_NOTIFY_FLOW:
			sendNotification(command, CommandName.NEGATIVE_NOTIFY_FLOW);
			break;

		default:
			break;
		}

	}

	// forward the same command
	private void sendToCCR(ChcCommand command) {

		ChcCommand remoteBaSend = mapper.map(command, ChcCommand.class);
		remoteBaSend.setCmdName(CommandName.BATCH_FLOW);
		// invio per conferma message e consegna file alla BA destinataria

		// invertire ORC e CCR
		remoteBaSend.setWfReceiverSt(ChcCommand.CCR);
		remoteBaSend.setWfSenderSt(ChcCommand.ORC);
		
		if(remoteBaSend.getPhyFileDescriptor() != null) {
			remoteBaSend.getPhyFileDescriptor().setCsc("csc-orch");
			remoteBaSend.getPhyFileDescriptor().setQtm("qtm-orch");
		}
		
		
		ChcCommandWrapper commandWrapper = new ChcCommandWrapper(remoteBaSend);
		rabbitTemplate.convertAndSend(ccrExchange, ccrRoutingKey, JSON.toJson(commandWrapper));
		log.debug("ORC-SIMUTATOR ChcCommand BATCH_FLOW sent to CCR");
	}

	private void sendNotification(ChcCommand command, String flow) {
		ChcCommand ccrNotification = mapper.map(command, ChcCommand.class);
		
		ccrNotification.setCmdName(flow);
		ccrNotification.setCmdTS(Instant.now().toString());
		ccrNotification.setWfSenderSt(command.getWfReceiverSt());
		ccrNotification.setWfReceiverSt(command.getWfSenderSt());
		
		CCRtransportData transportData = command.getCcrTransportData();
		CCRtransportData notificationTransportData = mapper.map( command.getCcrTransportData(), CCRtransportData.class);
		notificationTransportData.setLocalBaId(transportData.getRemoteBaId());
		notificationTransportData.setRemoteBaId(transportData.getLocalBaId());
		
		ccrNotification.setCcrTransportData(notificationTransportData);
		
		ChcCommandWrapper commandWrapper = new ChcCommandWrapper(ccrNotification);
		rabbitTemplate.convertAndSend(ccrExchange, ccrRoutingKey, JSON.toJson(commandWrapper));
		log.debug("ORC-SIMUTATOR ChcCommand {} sent to CCR",flow);
	}
}
