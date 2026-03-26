package com.cbi.ccr.outbound.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.I18nCcrSoapOutbound;
import com.cbi.ccr.common.I18nCccrCommon;
import com.cbi.ccr.common.jwe.jwt.CCRJweJwtService;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.routing.BaUrlService;
import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundRequestDTO;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundResponseDTO;
import com.cbi.ccr.csw.femws.soap.FemsWsHeaders;
import com.cbi.ccr.domain.BaUrl;
import com.cbi.ccr.domain.BaUrlRepository;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtils;
import com.cbi.frw.http.HttpUtilsProxy;

import liquibase.pro.packaged.he;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CCROutboundServiceFemWs {

	@Autowired
	private BaUrlService baUrlRepository;
	
	@Autowired
	private CCRJweJwtService ccrJweJwtService;
	
	@Autowired
	private HttpUtilsProxy httpUtilsProxy;
	@Autowired
	private SOAPMessagesUtils soapMessagesUtils;
	
	public static final String X_CHC_ID = "x-chc-id";
	
	public static final String X_CHC =  "x-chc";
	
	private void logError(String message) {
		CcrLog.getLogData().setMessage(String.format("SOAP error. %s", message));
		CcrLog.error(log);
	}
	
	
	private String getPayload(HttpServletRequest request) throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(request.getInputStream(), out);
		
		String payload = out.toString();
		
		if(log.isDebugEnabled()) {
			CcrLog.getLogData().setMessage(String.format("SOAP. Got Femws message payload: %s", payload));
			CcrLog.debug(log);
		}
		return payload;
	}
	
	private String sendAndReturnResponse(SOAPMessage msg, Map<String, String> soapHeaders, String payload, HttpServletResponse httpResponse) throws ChcStubException, ChcException, SOAPException {
		
		String baiId = getBaId(msg);
		BaUrl baData = getBaData(baiId);
		
		if(baData.getClientId() == null)
			throw new ChcException(I18nCcrSoapOutbound.MISSING_CLIENT_ID);
		
		String[] baSplit = baData.getUrl().split(":");
		
		String host = baSplit[0] + ":" +baSplit[1]; // deve contenere anche il protocollo
		String port = baSplit[2];
					
		FemsWsInboundRequestDTO messageDTO = FemsWsInboundRequestDTO.builder()
				.baId(baiId)
				.headers(soapHeaders) // should be passed from the Orchestrator
				.payload(payload)
				.build();
		
		Map<String, String> restHeaders = new HashMap<>();
		restHeaders.put("clientidCSW", baData.getClientId());
		restHeaders.put("hostCSW", host);
		restHeaders.put("portCSW", port);
		if(ccrJweJwtService.isSecurityEnabled()) {
			restHeaders.put(CCRJweJwtService.PROP_TOKEN_HEADER, ccrJweJwtService.getJwt());
		}
		
		// invia al SecurityGateway, che crea il jwe e lo invial al ClientSW di destinazione
		HttpResponse<FemsWsInboundResponseDTO> hr = httpUtilsProxy.postRequestWithBody(
				String.format("%s%s%s", baData.getUrl(), CSWInboundControllerPath.BASE, CSWInboundControllerPath.SOAP_INBOUND), 
				restHeaders, messageDTO, FemsWsInboundResponseDTO.class, 20000);
		
		CcrLog.getLogData().setMessage(String.format("Message sent to:%s, clientId:%s", 
				baData.getUrl(), 
				baData.getClientId()));
		CcrLog.info(log);
		
		FemsWsInboundResponseDTO responseDTO = hr.getResponse();
		String responseToSendBack = responseDTO.getPayload();
		
		for (Map.Entry<String, String> entry : responseDTO.getHeaders().entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			// filtriamo solo gli headers che hanno il prefisso X_CHC
			if(key.contains(X_CHC) || key.equalsIgnoreCase(HttpUtils.CONTENT_TYPE)) {
				httpResponse.addHeader(key, val);
			}
		}
		return responseToSendBack;
	}
	
	public void processMessage(HttpServletRequest request, HttpServletResponse httpResponse) throws ChcException, ChcStubException {
		
		SOAPMessage msg = null;
		boolean isError = false;
		String responseToSendBack = null;
		
		LocalDateTime ts2 = LocalDateTime.now();
		
		try {
			
			Map<String, String> soapHeaders = new HashMap<>();
			
			MimeHeaders mimeHeaders = new MimeHeaders();
			
			Enumeration<String> headersName = request.getHeaderNames();
			while (headersName.hasMoreElements()) {
				String header = headersName.nextElement();
				// filtriamo solo gli headers che hanno il prefisso X_CHC
				if(header.contains(X_CHC) || header.equalsIgnoreCase(HttpUtils.CONTENT_TYPE)) {
					soapHeaders.put(header, request.getHeader(header));
				}				
				
				mimeHeaders.addHeader(header, request.getHeader(header));
			}
			
			String payload = getPayload(request);
			
			try(ByteArrayInputStream bais = new ByteArrayInputStream(payload.getBytes())) {
				
				MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
				
				msg = msgFactory.createMessage(mimeHeaders, bais);		
				
				responseToSendBack = sendAndReturnResponse(msg, soapHeaders, payload, httpResponse);
			}
			
		} catch (ChcException | SOAPException e) {
			responseToSendBack = e.toString();
			isError = true;
		} catch (ChcStubException e) {
			responseToSendBack = String.format("Error sending message to destination BA %s", e.toString());
			isError = true;
		} catch (IOException e) {
			responseToSendBack = String.format("Error reading InputStream  %s", e.toString());
			isError = true;
		}
		
		LocalDateTime ts3 = LocalDateTime.now();
		
		String chcTrackInfoHeadersString = String.format("TS2:%s;TS3:%s;", 
				ts2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SS.ss'Z'")), 
				ts3.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SS.ss'Z'")));
		
		
		httpResponse.addHeader(X_CHC_ID, request.getHeader(X_CHC_ID));
		httpResponse.addHeader("x-chc-track-info", chcTrackInfoHeadersString);

		if(isError) {
			responseToSendBack = handleErrorAndBuildResponse(responseToSendBack, msg);
		}
		
		try{
			
			if(log.isDebugEnabled()) {
				CcrLog.getLogData().setMessage(String.format("Response recevied from remote ClienwtSW is:%n %s", responseToSendBack));
				CcrLog.debug(log);
			}
					
			httpResponse.getOutputStream().write(responseToSendBack.getBytes());
			
			if(!isError && log.isInfoEnabled()) {
				CcrLog.getLogData().setMessage(String.format("SOAP response sent back to ORCH X_CHC_ID:%s chcTrackInfo-headers:%s", 
						request.getHeader(X_CHC_ID), 
						chcTrackInfoHeadersString));
				CcrLog.info(log);
			}
			
		} catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_GENERIC, "SOAP Generic Error "+e.toString());
		}
		
	}
	
	private String handleErrorAndBuildResponse(String responseToSendBack, SOAPMessage msg) throws ChcException {
		logError(responseToSendBack);
		// convert the error in soap fault
		try {
			if(msg ==  null) {
				throw new ChcException(I18nCommon.ERR_GENERIC, "Input SOAP message is null ");
			}
			return soapMessagesUtils.buildSOAPFault(soapMessagesUtils.isSoap11(msg.getSOAPHeader()), "SOAP-ERROR", responseToSendBack, true, getFemsWsHeader(msg.getSOAPHeader()));
			
		} catch (SOAPException e) {
			throw new ChcException(I18nCommon.ERR_GENERIC, "SOAP Error " +e.toString());
		}
	}
	
	private String getBaId(SOAPMessage msg) throws ChcException, SOAPException {
			
		SOAPHeader soapHeader = msg.getSOAPHeader();
		
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);
		
		String serverNetCode = getServerNetCode(femsWsHeader);
		String applCode = getApplCode(femsWsHeader);
		String env = getEnv(femsWsHeader);
		
		return String.format("%s%s%s", serverNetCode, applCode, env);
	}
	
	private BaUrl getBaData(String localBaId) throws ChcException {
		BaUrl localBaUrl = baUrlRepository.findFirstByBaId(localBaId);
		if (localBaUrl == null)
			throw new ChcException(I18nCccrCommon.MISSING_BA_URL_CONFIGURATION, localBaId);
		return localBaUrl;
	}
	
	
	private SOAPHeaderElement getFemsWsHeader(SOAPHeader soapHeader) throws ChcException {

		SOAPHeaderElement femsWsHeader = null;

		Iterator<?> it = soapHeader.getChildElements();

		while (it.hasNext()) {
			Object tmp = it.next();
			if (tmp instanceof SOAPHeaderElement) {
				SOAPHeaderElement temp = (SOAPHeaderElement) tmp;

				if (temp.getLocalName().equalsIgnoreCase("FEMSWSHeader")) {
					femsWsHeader = temp;
				}
			}
		}
		if(femsWsHeader == null) {
			throw new ChcException(I18nCcrSoapOutbound.FEMWS_HEADER_NOT_FOUND);
		}
		return femsWsHeader;
	}
	
	private String getClientNetCode(SOAPHeaderElement femsWsHeader) throws ChcException{
		String clientNetCode = null;

	
		for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
			if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j),
					FemsWsHeaders.CLIENT_NET_CODE_TAG_NAME)) {
				clientNetCode = femsWsHeader.getChildNodes().item(j).getTextContent();
			}

		}
		if(clientNetCode == null) {
			throw new ChcException(I18nCcrSoapOutbound.FEMWS_HEADER_NOT_FOUND);
		}
		return clientNetCode;
	}
	
	private String getServerNetCode(SOAPHeaderElement femsWsHeader) throws ChcException{
		String serverNetCode = null;

		for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
			if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j),
					FemsWsHeaders.SERVER_NET_CODE_TAG_NAME)) {
				serverNetCode = femsWsHeader.getChildNodes().item(j).getTextContent();
			}

		}
		if(serverNetCode == null) {
			throw new ChcException(I18nCcrSoapOutbound.FEMWS_SERVER_NET_CODE_NOT_FOUND);
		}
		return serverNetCode;
	}
	
	private String getApplCode(SOAPHeaderElement femsWsHeader) throws ChcException{
		String applCode = null;

		for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
			if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j),
					FemsWsHeaders.APPL_CODE_TAG_NAME)) {
				applCode = femsWsHeader.getChildNodes().item(j).getTextContent();
			}

		}
		if(applCode == null) {
			throw new ChcException(I18nCcrSoapOutbound.FEMWS_APPL_CODE_NOT_FOUND);
		}
		return applCode;
	}
	private String getEnv(SOAPHeaderElement femsWsHeader) throws ChcException{

		String env = null;
		for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
			if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j), FemsWsHeaders.ENV_TAG_NAME)) {
				env = femsWsHeader.getChildNodes().item(j).getTextContent();
			}

		}
		if(env == null) {
			throw new ChcException(I18nCcrSoapOutbound.FEMWS_ENV_NOT_FOUND);
		}
		return env;
	}
}
