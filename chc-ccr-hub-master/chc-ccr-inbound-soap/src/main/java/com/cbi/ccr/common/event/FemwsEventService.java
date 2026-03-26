package com.cbi.ccr.common.event;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.udr.UdrUtils;
import com.cbi.ccr.dto.mq.command.dashboard.ChcEvent;
import com.cbi.ccr.dto.mq.command.dashboard.ChcTrackInfo;
import com.cbi.ccr.dto.mq.command.orch.ChcEventWrapper;
import com.cbi.ccr.dto.mq.command.orch.ChcId;
import com.cbi.ccr.dto.mq.command.orch.ONLMsgDescriptor;
import com.cbi.ccr.dto.mq.command.orch.UDRFmews;
import com.cbi.frw.common.json.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FemwsEventService {

	@Value("${dashboard_exchange}")
	private String dashboardExchange;
	
	@Value("${dashboard_routing_key}")
	private String dashboardRoutingKey;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public void sendEvent(boolean isEventSend, String chcId, String udr, List<ChcTrackInfo> trackInfos, String canale) {
		ChcEvent event = buildCommonEvent(isEventSend, chcId, udr, trackInfos, canale);
		ChcEventWrapper eventWrapper = new ChcEventWrapper(event);
		
		CcrLog.getLogData().setFunction("sendEvent");
		CcrLog.getLogData().setMessage("CCR-INBOUND FEMWS event created for DASH. Trying to send ....");
		CcrLog.debug(log);
		

		try {
			rabbitTemplate.convertAndSend(dashboardExchange, dashboardRoutingKey, JSON.toJson(eventWrapper));
			CcrLog.getLogData().setMessage(String.format("Sent message to DASH: %n %s %n", JSON.toJson(eventWrapper)));
			CcrLog.debug(log);
		} catch (Exception e) {
			CcrLog.getLogData().setMessage("Error sending to DASH = " + e.getLocalizedMessage() + e.getMessage());
			CcrLog.debug(log);
		}
	}

	private ChcEvent buildCommonEvent(boolean isEventSend, String chcId, String udr, List<ChcTrackInfo> trackInfos, String canale) {

		String eventType;
		String wfFromStation;
		String wfToStation;
		String wfProgress;
		String msgName;

		if(isEventSend) {
			eventType = "OnlineResponseSent";
			wfFromStation = ChcEvent.ORC;
			wfToStation = ChcEvent.CSW;
			wfProgress = "END";
			msgName = "Response";
		} else {
			eventType = "OnlineRequestReceived";
			wfFromStation = ChcEvent.CSW;
			wfToStation = ChcEvent.ORC;
			wfProgress = "BEGIN";
			msgName = "Request";
		}
		
		List<ChcId> chcIds =  Arrays.asList(ChcId.builder().id(chcId).build());
		
		UDRFmews parsedUdr = UdrUtils.parseUDRFemws(udr);
		
		ONLMsgDescriptor onlMsgDescriptor = ONLMsgDescriptor.builder()
				.chcPhyMsgId(chcIds.get(0).getId())
//				.canale("WSP") 
				.canale(canale)
				.msgName(msgName)
				.idE2E(parsedUdr.getIde2e())
				.csg(parsedUdr.getCsg())
				.qa(parsedUdr.getQa())
				.irs(parsedUdr.getIrs())
				.csc(parsedUdr.getCsc())
				.qtm(parsedUdr.getQtm())
				.cgm(parsedUdr.getCgm())
//				.cmf(parsedUdr.getCmf()) rimosso dalla documentazione
//				.cdf(parsedUdr.getCdf()) rimosso dalla documentazione
				.cml(parsedUdr.getCml())
				.cdl(parsedUdr.getCdl())
				.udr(udr)
				.uris(new ArrayList<>()) //empty by documentation
				.build();
		
		
		
		return ChcEvent.builder()
				.wfCurrentStation(ChcEvent.CCR)
				.evType(eventType) //OnlineResponseSent OnlineRequestReceived
				.evVersion(ChcEvent.COMMAND_VERSION)
				.evTS(Instant.now().toString())
				.wfFromStation(wfFromStation) //receive: CSW -- SEND: ORC
				.wfToStation(wfToStation) // Receive ORC -- Send: csw
				.wfProgress(wfProgress) //receive BEGIN - send END
				.msgName(msgName) // receive Request -- sent Response
				.chcIds(chcIds) // CHCID generato
				.onlMsgDescriptor(onlMsgDescriptor)
				.chcTrackInfos(trackInfos)
				.build();
	}
}
