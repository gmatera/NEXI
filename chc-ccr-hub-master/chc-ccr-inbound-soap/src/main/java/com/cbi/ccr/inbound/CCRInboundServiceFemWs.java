package com.cbi.ccr.inbound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.ccr.I18nCcrSoapInbound;
import com.cbi.ccr.common.event.FemwsEventService;
import com.cbi.ccr.common.jwe.jwt.CCRJweJwtService;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.udr.UdrUtils;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundRequestDTO;
import com.cbi.ccr.csw.femws.dto.FemsWsOutboundRequestDTO;
import com.cbi.ccr.dto.mq.command.dashboard.ChcTrackInfo;
import com.cbi.ccr.dto.mq.command.dashboard.ChcTrackInfos;
import com.cbi.ccr.dto.mq.command.orch.UDRFmews;
import com.cbi.ccr.inbound.service.CCRIdServiceGeneratorStub;
import com.cbi.frw.common.Msg;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.http.HttpUtilsNoProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CCRInboundServiceFemWs {

	private static final String X_CHC_ID = "x-chc-id";

	@Value("${orchestrator_femws_endpoint}")
	private String orchestratorFemwsEndpoint;
	
	@Autowired
	private CCRJweJwtService ccrJweJwtService;
	
	@Autowired
	private FemwsEventService femwsEventService;
	
	@Autowired
	private CCRIdServiceGeneratorStub chcIdGeneratorStub;
	
	@Autowired
	private HttpUtilsNoProxy httpUtilsNoProxy;
	
	private void logError(String message) {
		CcrLog.getLogData().setMessage(String.format("FEM-WS error. %s", message));
		CcrLog.debug(log);
		
	}
	
	public FemsWsInboundRequestDTO sendToORchestrator(String xChcT1, FemsWsOutboundRequestDTO message) throws ChcException, ChcStubException {
		
		try {
			
			if(!StringUtils.isEmpty(xChcT1)) {
				xChcT1 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			}
			
			
			String canale = message.getApplCode().substring(0, 3);
			
			String chcId = chcIdGeneratorStub.getId();
			UDRFmews parsedUDR = UdrUtils.parseUDRFemws(message.getUdr());
			
			message.getHeaders().put("x-sss-udr", message.getUdr());
			message.getHeaders().put(X_CHC_ID, chcId);
			message.getHeaders().put("x-chc-physical-sender", parsedUDR.getCgm());
			message.getHeaders().put("x-chc-logical-sender", parsedUDR.getCml());
			message.getHeaders().put("x-chc-message-type", parsedUDR.getCsc());
			
			CcrLog.getLogData().setFunction("sendToORchestrator");
			CcrLog.getLogData().setUdr(message.getUdr());
			
			if(log.isDebugEnabled()) {
				CcrLog.getLogData().setMessage(String.format("FEM-WS. Got Femws message %s", message.getHeaders()));
				CcrLog.debug(log);
			}		

			if(ccrJweJwtService.isSecurityEnabled()) {
				message.getHeaders().put(CCRJweJwtService.PROP_TOKEN_HEADER, ccrJweJwtService.getJwt());
			}
			
			//sending event to dashboard
			ChcTrackInfo trackInfo = ChcTrackInfo.builder()
					.type("T1")
					.ts(xChcT1)
					.build();
			
			femwsEventService.sendEvent(false, chcId, message.getUdr(), Arrays.asList(trackInfo), canale);
						
			if(log.isDebugEnabled()) {
				CcrLog.getLogData().setMessage(String.format("Sending to ORCH at: %s message:  %s", orchestratorFemwsEndpoint, JSON.toJson(message)));
				CcrLog.debug(log);			
			}
			
			try(CloseableHttpClient client = httpUtilsNoProxy.httpClientFactory(20000); 
					CloseableHttpResponse resp = httpUtilsNoProxy.postLowLevel(client, orchestratorFemwsEndpoint, message.getHeaders(), new ByteArrayInputStream(message.getPayload().getBytes()));){
				
				Map<String, String> responseHeaders = new HashMap<>();
				
				Header[] headers = resp.getAllHeaders();
				for (int i = 0; i < headers.length; i++) {
					
					if(log.isDebugEnabled()) {
						CcrLog.getLogData().setMessage(String.format("RESPONSE: header name: %s, header value: %s", headers[i].getName(), headers[i].getValue()));
						CcrLog.debug(log);
					}
					
					responseHeaders.put(headers[i].getName(), headers[i].getValue());
				}
				
				
				if(responseHeaders.get(X_CHC_ID) == null || !responseHeaders.get(X_CHC_ID).equals(chcId)) 
					throw new ChcException(I18nCcrInbound.ERR_INVALID_SERVICE_DATA, "invalid x-chc-id");

				
				ChcTrackInfos chcTrackInfos = new ChcTrackInfos();
				chcTrackInfos.setChcTrackInfo(new ArrayList<>());
				String[] timeStamps = resp.getFirstHeader("x-chc-track-info").getValue().split(";");
				for(String nameValuePairTS: timeStamps) {
					String type = nameValuePairTS.substring(0, nameValuePairTS.indexOf(":"));
					String ts = nameValuePairTS.substring(nameValuePairTS.indexOf(":") + 1);
					chcTrackInfos.getChcTrackInfo().add(new ChcTrackInfo(type, ts));
				}
				
				// TODO ELIMINARE
//				chcTrackInfos.getChcTrackInfo().add(ChcTrackInfo.builder()
//						.type("T4")
//						.ts(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
//						.build());
				
				femwsEventService.sendEvent(true, chcId, message.getUdr(), chcTrackInfos.getChcTrackInfo(), canale);
				
				StatusLine sl = resp.getStatusLine();
				
				InputStream in = resp.getEntity().getContent();
				try(ByteArrayOutputStream bos = new ByteArrayOutputStream();){
					IOUtils.copy(in, bos);
					FemsWsInboundRequestDTO response = FemsWsInboundRequestDTO.builder()
							.headers(responseHeaders)
							.payload(new String(bos.toByteArray(), StandardCharsets.UTF_8))
							.build();
					if(log.isDebugEnabled()) {
						CcrLog.getLogData().setMessage(String.format("ORCH Response: %s", JSON.toJson(response)));
						CcrLog.debug(log);
					}
					return response;
				}
			}
			
		} catch (ChcStubException e) {
			logError(String.format("Error sending message to Orchestrator %s", e.toString()));
		
			throw new ChcStubException(ErrorMessage.builder()
					.localizedMessage(Msg.getMessage(I18nCcrSoapInbound.FEMWS_ERR_ORCHESTRATOR_NOT_AVAILABLE.name()))
					.errorCode(I18nCcrSoapInbound.FEMWS_ERR_ORCHESTRATOR_NOT_AVAILABLE.name())
					.exceptionClass(ChcStubException.class.getName())
					.httpStatus(e.getError().getHttpStatus())
					.build());
		} catch (UnsupportedOperationException | IOException e) {
			logError(String.format("Error sending message to Orchestrator %s", e.toString()));
			
			throw new ChcStubException(ErrorMessage.builder()
					.localizedMessage(Msg.getMessage(I18nCcrSoapInbound.FEMWS_ERR_ORCHESTRATOR_NOT_AVAILABLE.name()))
					.errorCode(I18nCcrSoapInbound.FEMWS_ERR_ORCHESTRATOR_NOT_AVAILABLE.name())
					.exceptionClass(ChcStubException.class.getName())
					.httpStatus(HttpStatus.SC_SERVICE_UNAVAILABLE)
					.build());
		}
		
	}
}
