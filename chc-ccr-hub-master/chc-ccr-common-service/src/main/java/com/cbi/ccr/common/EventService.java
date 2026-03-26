package com.cbi.ccr.common;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.udr.UdrUtils;
import com.cbi.ccr.domain.CommonEntity;
import com.cbi.ccr.dto.mq.command.dashboard.ChcEvent;
import com.cbi.ccr.dto.mq.command.dashboard.ChcTrackInfo;
import com.cbi.ccr.dto.mq.command.dashboard.ErrorDescriptor;
import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.ChcEventWrapper;
import com.cbi.ccr.dto.mq.command.orch.ChcId;
import com.cbi.ccr.dto.mq.command.orch.PhyFileDescriptor;
import com.cbi.ccr.dto.mq.command.orch.UDRunbulked;
import com.cbi.frw.common.json.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class EventService<T extends CommonEntity> {

	@Value("${dashboard_exchange}")
	private String dashboardExchange;
	
	@Value("${dashboard_routing_key}")
	private String dashboardRoutingKey;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public abstract CCRtransportData mapCCRTransportData(T message);
	public abstract PhyFileDescriptor mapPhyFileDescriptDto(T clientMessage, String eventType);
	
//	private ErrorDescriptor commonMapErrorDescriptor(T clientMessage) {
//		return ErrorDescriptor.builder()
//				.errorCode(clientMessage.getLog().substring(0, 10))
//				.errorDesc(clientMessage.getLog())
//				.build();
//	}

	protected CCRtransportData commonMapCCRTransportData(T clientMessage) {
		return CCRtransportData.builder().localBaId(clientMessage.getLocalBaId())
				.remoteBaId(clientMessage.getRemoteBaId()).build();
	}
	
	
	
	protected void addUdrToPhyFileDescriptor(String udr, PhyFileDescriptor fileDescriptor) {
		UDRunbulked parsedUdr = UdrUtils.parseUDR(udr);
		fileDescriptor.setIdE2E(parsedUdr.getIDE2E());
		fileDescriptor.setCsg(parsedUdr.getCSG());
		fileDescriptor.setQa(parsedUdr.getQA());
		fileDescriptor.setIrs(parsedUdr.getIRS());
		fileDescriptor.setCsc(parsedUdr.getCSC());
		fileDescriptor.setQtm(parsedUdr.getQTM());
		fileDescriptor.setPtm(parsedUdr.getPTM());
		fileDescriptor.setCgm(parsedUdr.getCGM());
		fileDescriptor.setCmf(parsedUdr.getSvcPhySender());
		fileDescriptor.setCdf(parsedUdr.getSvcPhyReceiver());
		fileDescriptor.setCml(parsedUdr.getSvcLogSender());
		fileDescriptor.setCdl(parsedUdr.getSvcLogReceiver());
	}

	protected PhyFileDescriptor commonMapPhyFileDescriptDto(T clientMessage) {
		return PhyFileDescriptor.builder()
				.chcPhyMsgId(clientMessage.getId().toString())
				.canale(clientMessage.getLocalBaId().substring(5, 8))
				.build();
	}
	
	private ChcEvent buildCommonEvent(T message, String eventType, String wfFromStation, String wfToStation, String wfProgress, ChcTrackInfo trackInfo, boolean hasTransportData, String errorCode, String errorMessage) {
		
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
		
		ChcEvent event = ChcEvent.builder()
				.evType(eventType)
				.wfFromStation(wfFromStation)
				.wfToStation(wfToStation)
				.evTS(formatter.format(Instant.now()))
				.evVersion(ChcEvent.COMMAND_VERSION)
				.chcIds(Arrays.asList(ChcId.builder().id(message.getChcId().toString()).build())) // CHCID correlation ID
				.phyFileDescriptor(mapPhyFileDescriptDto(message, eventType))
				.build();
		
		if(wfProgress!=null)
			event.setWfProgress(wfProgress);
		if(hasTransportData)
			event.setCcrTransportData(mapCCRTransportData(message));
		if(trackInfo != null)
			event.setChcTrackInfos(Arrays.asList(trackInfo));
		if(errorCode != null && errorMessage != null) {
			List<ErrorDescriptor> errorsDescriptor = new ArrayList<>();
			ErrorDescriptor error = ErrorDescriptor.builder()
			.errorCode(errorCode)
			.errorDesc(errorMessage)
			.build();
			errorsDescriptor.add(error);
			event.setErrorDescriptors(errorsDescriptor);
		}
		
		event.getPhyFileDescriptor().setSvcName(null);
		
		return event;
	}

	protected Long sumSize(Long fileSize, Integer messageSize) {
		return Long.sum(fileSize, messageSize);
	}
	
	public void sendEvent(T clientMessage, String eventType, String wfFromStation, String wfToStation, String wfProgress, ChcTrackInfo trackInfo, boolean hasTransportData, String errorCode, String errorMessage) {
		ChcEvent event = buildCommonEvent(clientMessage, eventType, wfFromStation, wfToStation, wfProgress, trackInfo, hasTransportData, errorCode, errorMessage);
		ChcEventWrapper eventWrapper = new ChcEventWrapper(event);
//		log.debug("CCR-INBOUND event created for DASH. Trying to send ....");
		
		try {
			//commentato per rilascio
			rabbitTemplate.convertAndSend(dashboardExchange, dashboardRoutingKey, JSON.toJson(eventWrapper));
			//log.debug("Sent message to DASH: \n {} \n", JSON.toJson(eventWrapper));
			
			CcrLog.getLogData().setMessage(String.format("Sent message to DASH: %s", JSON.toJson(eventWrapper)));
			CcrLog.debug(log);
			
		} catch (Exception e) {
			// TODO da completare
			
			CcrLog.getLogData().setMessage(String.format("Error sending to DASH: %s", e.toString()));
			CcrLog.error(log, e);
		}
	}
}
