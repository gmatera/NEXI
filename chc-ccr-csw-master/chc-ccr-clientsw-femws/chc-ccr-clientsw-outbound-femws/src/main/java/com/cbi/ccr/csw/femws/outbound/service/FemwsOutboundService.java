package com.cbi.ccr.csw.femws.outbound.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.cbi.ccr.csw.femws.InboundControllerPathSoap;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundRequestDTO;
import com.cbi.ccr.csw.femws.dto.FemsWsOutboundRequestDTO;
import com.cbi.ccr.csw.femws.service.FemsConfiguration;
import com.cbi.ccr.csw.femws.service.FemsWsService;
import com.cbi.ccr.csw.femws.service.dto.FemsSession;
import com.cbi.ccr.csw.femws.service.dto.FemsWsHeaderIn;
import com.cbi.ccr.csw.femws.service.exception.FemwsException;
import com.cbi.ccr.csw.femws.service.exception.MalformedHeaderException;
import com.cbi.ccr.csw.femws.service.exception.MaxMessageSizeException;
import com.cbi.ccr.csw.femws.service.exception.WrongAttachmentContentIdException;
import com.cbi.ccr.csw.femws.service.utils.EventLog;
import com.cbi.ccr.csw.femws.service.utils.EventLog.WsEvent;
import com.cbi.ccr.csw.femws.service.utils.FemsWsHeaderUtils;
import com.cbi.ccr.csw.femws.service.utils.MessageUtils;
import com.cbi.ccr.csw.femws.service.utils.SOAPMessagesUtils;
import com.cbi.ccr.csw.femws.service.utils.XmlSignatures;
import com.cbi.ccr.csw.femws.soap.FemsWsHeaders;
import com.cbi.ccr.csw.service.common.JweJwtService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.dto.InboundControllerPath;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.util.DateUtils;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtils;
import com.cbi.frw.http.HttpUtilsProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FemwsOutboundService extends FemsWsService {
	@Value("${csw_version}")
	private String cswVersion;

	@Autowired
	private FemsConfiguration configuration;

	@Autowired
	private EventLog eventLog;
	@Autowired
	private JweJwtService jweJwtService;

	@Autowired
	private HttpUtilsProxy httpUtilsProxy;
	
	@Autowired
	private MessageUtils messageUtils;
	
	private static final String FEMS_AGENT = "FemsWsServlet Agent";
	private static final String SOAP_ACTION = "SOAPAction";
	private static final String UNKNOWN = "unknown";
	
	private void sessionFromParameters(FemsSession femsSession, HttpServletRequest req, SOAPMessage inputSoapMex) throws SOAPException, MalformedHeaderException {
		
		femsSession.setHeaderWs(new FemsWsHeaderIn());
		if (req.getParameterValues("CNC") != null)
			femsSession.getHeaderWs().setClientNetCode(req.getParameterValues("CNC")[0]);
		if (req.getParameterValues("SNC") != null)
			femsSession.getHeaderWs().setServerNetCode(req.getParameterValues("SNC")[0]);
		if (req.getParameterValues("AP") != null)
			femsSession.getHeaderWs().setApplCode(req.getParameterValues("AP")[0]);
		if (req.getParameterValues("ENV") != null)
			femsSession.getHeaderWs().setEnv(req.getParameterValues("ENV")[0]);
		if (req.getParameterValues("UDRALG") != null)
			femsSession.setUdrAlgo(req.getParameterValues("UDRALG")[0]);
		if (req.getParameterValues("LAU") != null)
			femsSession.getHeaderWs().setLau(req.getParameterValues("LAU")[0]);
		soapMessagesUtils.composeFemsWsHeaderByUrlPath(femsSession, inputSoapMex);
		inputSoapMex.saveChanges();
	}
	
	protected SOAPMessage buildSOAPMessage(HttpServletRequest req, FemsSession femsSession)
			throws FemwsException, SOAPException, IOException {

		try {
			SOAPMessage inputSoapMex = soapMessagesUtils.parse(req, femsSession, false);
			/* Recupero header dalla request per chiamate con porta di dominio (PDD) */
			if (Boolean.TRUE.equals(configuration.getFemswsHeaderHandler())) {
				CswLog.debug(log, String.format("request URLPATH In: %s", req.getRequestURI()));
				
				femsSession.setRequestPath(req.getRequestURI());
				sessionFromParameters(femsSession, req, inputSoapMex);
			} else {
				femsSession.setHeaderWs(FemsWsHeaderUtils.read(inputSoapMex.getSOAPHeader()));
			}
			return inputSoapMex;
		} catch (MalformedHeaderException e) {

			throw FemwsException.builder()
					.traceEntry("femsws:Client.MalformedHeaderError - FemsWsHeader not correctly formed")
					.femsSession(femsSession).faultCode("femsws:Client.MalformedHeaderError").build();

		} catch (WrongAttachmentContentIdException e) {
			throw FemwsException.builder().traceEntry(e.getTraceEntry()).femsSession(femsSession)
					.faultCode(e.getFaultCode()).build();

		} catch (MaxMessageSizeException e) {
			femsSession.setSoapVersion11(req.getContentType().toLowerCase().contains("text/xml"));

			Integer maxMessageSize = 1024 * 1024 * configuration.getMaxMessageSizeAllowed();

			throw FemwsException.builder()
					.traceEntry("femsws:Client.MaxMessageSizeError - The size of the soap message is greater than "
							+ maxMessageSize + " bytes")
					.femsSession(femsSession).faultCode("femsws:Client.MaxMessageSizeError").build();

		}
	}

	private void checkLAU(String lauLabel, SOAPMessage inputSoapMex, FemsSession femsSession)
			throws FemwsException, SOAPException {
		if (lauLabel != null) {
			String lauKey = ""; // TODO get from the database

			byte[] keyBytes = lauKey.getBytes(StandardCharsets.UTF_8);
			Document doc = inputSoapMex.getSOAPPart();
			try {
				XmlSignatures.validate(doc, keyBytes, soapMessagesUtils);
				XmlSignatures.removeSecurityElements(inputSoapMex, soapMessagesUtils);
			} catch (XMLSignatureException e) {

				throw FemwsException.builder()
						.traceEntry("femsws:Client.LocalAuthenticationError - Error validating XML signature")
						.femsSession(femsSession).faultCode("femsws:Client.LocalAuthenticationError").build();
			}

		}
	}

	private String getPayloadAdnSetHeaders(SOAPMessage inputSoapMex, FemsSession femsSession, HttpServletRequest req,
			Map<String, String> headers) throws SOAPException, IOException {

		String payload = null;
		try (InputStream is = new ByteArrayInputStream(femsSession.getStream())) {
			if (ServletFileUpload.isMultipartContent(req)) {
				// lettura dell'intero messaggio
				String toSend = soapMessagesUtils.getInputStreamToString(is);
				payload = patchXml(toSend, femsSession.getPrefixEnvelope() + ":" + femsSession.getLocalNameEnvelope(),
						payload);

				String contentType = getContentType(req);
				femsSession.setContentType(contentType);
				headers.put(HttpUtils.ACCEPT, contentType);
				headers.put(HttpUtils.CONTENT_TYPE, contentType);
				headers.put(SOAP_ACTION, femsSession.getSoapAction());
			} else {
				headers.put(HttpUtils.ACCEPT, femsSession.getContentType());
				headers.put(HttpUtils.CONTENT_TYPE, femsSession.getContentType());
				headers.put(SOAP_ACTION, femsSession.getSoapAction());
				payload = soapMessagesUtils.soapMessagePrettyPrinter(inputSoapMex);
			}

			return payload;
		}
	}

	public void processOutbound(HttpServletRequest req, HttpServletResponse response) {

		SOAPHeader soapHeader = null;
		FemsSession femsSession = new FemsSession();
		String payload = null;
		UUID uuid = UUID.randomUUID();
		String nonce = uuid.toString().replace("-", "");
		femsSession.setId(nonce);

		try {

			SOAPMessage inputSoapMex = buildSOAPMessage(req, femsSession);

			soapHeader = inputSoapMex.getSOAPHeader();

			// Controlli formali
			soapMessagesUtils.isMalformed(femsSession, inputSoapMex, false, false);

			String lauLabel = soapMessagesUtils.getLauLabel(soapHeader);

			LocalDateTime now = LocalDateTime.now();

			femsSession.setAbHostName(getClientIpAddr(req));
			femsSession.setClientFemWs(configuration.getFemsWsId());
			femsSession.setClientFengId(configuration.getFengId());
			femsSession.setStartGmtChar(DateUtils.formatISODateTimeUTC(now));
			femsSession.setStartTz(DateUtils.formatISODateTimeUTC(now));
			femsSession.setRequestGMTChar(DateUtils.formatISODateTimeUTC(now));
			//femsSession.setRequestAttachmentSize(soapMessagesUtils.getAttachmentsSize(inputSoapMex));
			femsSession.getHeaderWs().setReqST(DateUtils.formatISODateTimeUTC(now));

			String suid = nonce;
			femsSession.setSuid(suid);
			inputSoapMex.saveChanges();

			checkLAU(lauLabel, inputSoapMex, femsSession);

			FemsWsHeaderUtils.append(femsSession, FemsWsHeaders.SUID_TAG_NAME, suid);
			FemsWsHeaderUtils.append(femsSession, FemsWsHeaders.REQ_ST, femsSession.getRequestGMTChar());
			inputSoapMex.saveChanges();

			femsSession.setContentType(soapMessagesUtils.getFirstHeaderValue(inputSoapMex, HttpUtils.CONTENT_TYPE));

			if (inputSoapMex.getMimeHeaders().getHeader(FemsWsHeaderUtils.SOAP_ACTION) == null) {
				femsSession.setSoapAction("");
			} else {
				femsSession.setSoapAction(
						soapMessagesUtils.getFirstHeaderValue(inputSoapMex, FemsWsHeaderUtils.SOAP_ACTION));
			}

			Map<String, String> headers = new HashMap<>();

			payload = getPayloadAdnSetHeaders(inputSoapMex, femsSession, req, headers);

			// passo al server tutto quello che abbiamo appuntato in sessione
			femsSession.setStream(null);
			headers.put(FemsWsOutboundRequestDTO.SESSION_WS, JSON.toJson(femsSession));
			headers.put(HttpUtils.USER_AGENT, FEMS_AGENT);

			FemsWsOutboundRequestDTO messageDTO = FemsWsOutboundRequestDTO.builder().headers(headers).payload(payload)
					.udr(soapMessagesUtils.getUDR(false, soapHeader))
					.clientNetCode(soapMessagesUtils.getClientNetCode(soapHeader))
					.serverNetCode(soapMessagesUtils.getServerNetCode(soapHeader))
					.applCode(soapMessagesUtils.getApplCode(soapHeader)).env(soapMessagesUtils.getEnv(soapHeader))
					.build();


//			// sending to CCR
			Map<String, String> headersToSend = new HashMap<>();
			headersToSend.put("x-chc-t0", femsSession.getHeaderWs().getReqST());

			FemsWsInboundRequestDTO resp;
			if (jweJwtService.isSecurityEnabled()) { 
				
				headersToSend.put(JweJwtService.PROP_TOKEN_HEADER, jweJwtService.getJwt());
				headersToSend.put(HttpUtils.CONTENT_TYPE, JweJwtService.CONTENT_TYPE_PLAIN_TEXT);
				
				String serializedEncryptedJWE = jweJwtService.getSerializedEncryptedJWE(JSON.toJson(messageDTO));
				
				HttpResponse<String> jweResponse = httpUtilsProxy.sendRequestWithBody(
						String.format("%s%s", ccrHost, InboundControllerPath.BASE + InboundControllerPathSoap.SOAP_MESSAGE + InboundControllerPathSoap.SOAP_INBOUND),
						headersToSend, serializedEncryptedJWE, String.class, 20000, HttpUtils.POST);
				resp = decryptJwe(jweResponse.getResponse());
			} else {
				HttpResponse<FemsWsInboundRequestDTO> httpResponse = httpUtilsProxy.postRequestWithBody(
						String.format("%s%s", ccrHost, InboundControllerPath.BASE + InboundControllerPathSoap.SOAP_MESSAGE + InboundControllerPathSoap.SOAP_INBOUND),
						headersToSend, messageDTO, FemsWsInboundRequestDTO.class, 20000);
				resp = httpResponse.getResponse();
			}
			
			if(log.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String,String> entry : headers.entrySet()) {
				    String key = entry.getKey();
				    Object value = entry.getValue();
				    sb.append(key);
					sb.append(":");
					sb.append(value);
					sb.append("\n");
				  }
				
				CswLog.debug(log, String.format("message sent to CCR headers: %s payload:%s", sb.toString(), messageUtils.hideBodyTrace(payload)));
			}
			sendResponse(resp, response);

		} catch (FemwsException e) {

			payload = getSoapFault(femsSession, e, e.getFaultCode(), e.getTraceEntry());
			sendErrorResponse(femsSession, payload, response);

		} catch (ChcStubException e) {
			payload = handleCCRError(e, femsSession);
			sendErrorResponse(femsSession, payload, response);

		} catch (SOAPException e) {
			Exception ex = e;
			if ((e.getCause() instanceof Exception) && !StringUtils.isEmpty(e.getCause().getMessage())) {
				ex = (Exception) e.getCause();
			}
			payload = "Unexpected exception " + ex.getClass().getName() + ": " + ex.getLocalizedMessage();
			sendErrorResponse(femsSession, payload, response);
		} catch (Exception e) {
			payload = getSoapFault(femsSession, e, SOAPMessagesUtils.FEM_WS_OTHER_ERROR,
					SOAPMessagesUtils.FEM_WS_OTHER_ERROR);
			sendErrorResponse(femsSession, payload, response);

		}
	}

	private FemsWsInboundRequestDTO decryptJwe(String response) throws ChcException {
		if (jweJwtService.isSecurityEnabled()) {
			String json = jweJwtService.getDeserializedDecriptedJWE(response);
			
			return JSON.fromJson(json, FemsWsInboundRequestDTO.class);
		} else {
			return JSON.fromJson(response, FemsWsInboundRequestDTO.class);
		}
	}


	private void sendErrorResponse(FemsSession femsSession, String payload, HttpServletResponse response) {
		try {
			if (payload == null)
				payload = "";

			String contentType;
			int contentLength = payload.getBytes(StandardCharsets.UTF_8).length;
			contentType = femsSession.getContentType();

			if (femsSession.getSoapAction() != null) {
				response.setHeader(SOAP_ACTION, femsSession.getSoapAction());
			}
			
			response.setContentType(contentType);
			response.setContentLength(contentLength);
			response.addHeader("FEMS-WS-version", cswVersion);
			response.setHeader(HttpUtils.CONTENT_TYPE, "" + contentType); // added
			response.setHeader("Content-Length", "" + contentLength); // added
			response.setHeader(HttpUtils.USER_AGENT, FEMS_AGENT);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(payload);
			response.flushBuffer();
			
			StringBuilder sb = new StringBuilder();
			
			for (String key : response.getHeaderNames()) {
				String val = response.getHeader(key);
				sb.append(key);
				sb.append(":");
				sb.append(val);
				sb.append("\n");
			}
			
			
		} catch (IOException ex) {
			CswLog.error(log, String.format("Erro while sending response: %s", ex.toString()));
		}
	}


	private void sendResponse(FemsWsInboundRequestDTO respDTO, HttpServletResponse response) {
	
		for (Map.Entry<String, String> entry : respDTO.getHeaders().entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			response.addHeader(key, val);
		}
		response.addHeader("FEMS-WS-version", cswVersion);
		response.setHeader(HttpUtils.USER_AGENT, FEMS_AGENT);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		try {
			response.getWriter().write(respDTO.getPayload());
			response.flushBuffer();
		} catch (IOException e) {
			CswLog.error(log, String.format( "IOException while replying to client: %s", e.toString()));
			eventLog.error(WsEvent.WSCLIENT_HANDLER_IO_ERROR, "IOException while replying to client:" + e.getMessage());
		}
	}

	private String handleCCRError(ChcStubException ex, FemsSession femsSession) {

		String faultFemsWs = "femsws:Client";
		String faultCode = "";

		switch (ex.getError().getHttpStatus()) {
		case HttpStatus.SC_SERVICE_UNAVAILABLE:
			faultCode = faultFemsWs + ".ServerCommunicationError";
			break;
		case HttpStatus.SC_REQUEST_TIMEOUT:
			faultCode = faultFemsWs + ".TimeoutError";
			break;
		default:
			faultCode = faultFemsWs + ".OtherError";
			break;
		}

		String errorMessage = ex.getError().getLocalizedMessage();


		try {
			SOAPMessage soapMsg = soapMessagesUtils.buildSOAPFault(femsSession, faultCode, errorMessage,
					configuration.getFemswsName(), false);

			Objects.requireNonNull(soapMsg).saveChanges();
			// return the payload
			return soapMessagesUtils.soapMessagePrettyPrinter(soapMsg);

		} catch (Exception e) {

			String payload = "femsws:Client.OtherError - Error generating soap fault message";
			
			CswLog.error(log, String.format( "CCR call error: %s", e.toString()));
			eventLog.error(EventLog.WsEvent.WSCLIENT_OTHER_ERROR, e.getMessage());

			try {
				payload = getSoapFault(femsSession, e, "femsws:Client.OtherError", "femsws:Client.OtherError");
			} catch (Exception e1) {
				CswLog.error(log, String.format( "CCR call error: %s", e.toString()));
			}

			// return the payload
			return payload;
		}
	}

	

	/**
	 * costruisce una SOAP fault a partire dai dati di un'eccezione. Esegue le
	 * stampe di tracciamento necessarie.
	 *
	 * @param femsSession dati della sessione FEMS-WS
	 * @param e           eccezione da gestire
	 * @param faultCode   codice di errore
	 * @param log         logger su cui scrivere
	 * @param traceEntry  messaggio da tracciare
	 * @return una stringa con il testo della SOAP fault
	 * @throws SOAPException
	 * @throws Exception
	 */
	public String getSoapFault(FemsSession femsSession, Exception e, String faultCode, String traceEntry) {

		String payload = "";
		eventLog.error(EventLog.WsEvent.SOAP_FAULT, traceEntry + "; " + e.getMessage());

		String faultActor = configuration.getFemswsName();

		SOAPMessage sm = soapMessagesUtils.buildSOAPFault(femsSession, faultCode, e.toString(), faultActor,
				false);

		try {
			/* DOPPIO FUNZIONAMENTO */
			if (Boolean.TRUE.equals(configuration.getFemswsHeaderHandler())) {
				SOAPHeaderElement femsWsElem = soapMessagesUtils.getFemsWsHeader(sm.getSOAPHeader());
				if (femsWsElem != null) {
					femsWsElem.detachNode();
					log.info("FEMSWSHeader removed");
				}
				sm.saveChanges();
			}
			/* DOPPIO FUNZIONAMENTO */

			payload = soapMessagesUtils.soapMessagePrettyPrinter(sm);
		} catch (SOAPException e1) {
			return "Generic SOAP error occurred " + e1.toString();
		}
		return payload;
	}

	/**
	 * stampa gli header ricevuti con la richiesta
	 *
	 * @param request il flusso della richiesta http in arrivo
	 * @return restituisce il content/type http
	 */
	private static String getContentType(HttpServletRequest request) {
		String requestContentType = null;
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			if (headerName.equalsIgnoreCase("content-type")) {
				requestContentType = request.getHeader(headerName);
			}
		}
		return requestContentType;
	}

	protected static String patchXml(String xmlString, String elementToRemove, String xmlTextToInsert) {
		int firstOccurrence = xmlString.indexOf("<" + elementToRemove);
		int lastOccurrence = xmlString.lastIndexOf(elementToRemove + ">") + elementToRemove.length() + 1;
		String prologue = xmlString.substring(0, firstOccurrence);
		String epilogue = xmlString.substring(lastOccurrence);

		String xmlToInsert = xmlTextToInsert;
		boolean hasXmlHeader = xmlToInsert.indexOf("<?xml") > -1;
		if (hasXmlHeader)
			xmlToInsert = xmlToInsert.substring(xmlToInsert.indexOf("?>") + 2);

		return prologue + xmlToInsert + epilogue;
	}

	/**
	 * Recupera l'indirizzo del client dagli header
	 *
	 * @param request la richiesta http
	 * @return una stringa con l'indirizzo del client
	 */
	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr() + ":" + request.getRemotePort();
		}
		return ip;
	}
}
