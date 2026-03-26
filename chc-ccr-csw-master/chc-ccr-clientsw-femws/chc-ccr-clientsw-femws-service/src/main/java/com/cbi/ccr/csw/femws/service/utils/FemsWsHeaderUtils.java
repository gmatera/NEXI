package com.cbi.ccr.csw.femws.service.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.cbi.ccr.csw.femws.service.dto.FemsSession;
import com.cbi.ccr.csw.femws.service.dto.FemsWsHeaderIn;
import com.cbi.ccr.csw.femws.service.exception.MissingHeaderException;
import com.cbi.ccr.csw.femws.soap.FemsWsHeaders;
import com.cbi.ccr.csw.service.common.logger.CswLog;

public class FemsWsHeaderUtils {

	private static final Logger logger = LoggerFactory.getLogger(FemsWsHeaderUtils.class);

	public static final String SOAP_ACTION = "SOAPAction";
	
	public static final String AVOID = "AVOID %s";
	
	private FemsWsHeaderUtils() {
	}

	public static FemsWsHeaderIn read(SOAPHeader soapHeader) {
		try {
			return doRead(soapHeader);
		} catch (Exception e) {
			CswLog.error(logger, String.format("Unable to store into session FEMS-WS header information", e.getMessage()));
			return null;
		}
	}

	private static FemsWsHeaderIn doRead(SOAPHeader soapHeader) {

		String actor = "";
		Boolean mustUnderstand = null;
		String clientNetCode = "";
		String serverNetCode = "";
		String suid = "";
		String applCode = "";
		String env = "";
		String req_udr = "";

		try {
			SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);
			actor = femsWsHeader.getActor();
			mustUnderstand = femsWsHeader.getMustUnderstand();
		} catch (Throwable e) {
			CswLog.error(logger, String.format(AVOID, e.getMessage()));
		}
		try {
			clientNetCode = getClientNetCode(soapHeader);
		} catch (MissingHeaderException | SOAPException e) {
			CswLog.error(logger, String.format(AVOID, e.getMessage()));
		} 
		try {
			serverNetCode = getServerNetCode(soapHeader);
		} catch (MissingHeaderException | SOAPException e) {
			CswLog.error(logger, String.format(AVOID, e.getMessage()));
		} 
		try {
			applCode = getApplCode(soapHeader);
		} catch (MissingHeaderException | SOAPException e) {
			CswLog.error(logger, String.format(AVOID, e.getMessage()));
		} 
		try {
			env = getEnv(soapHeader);
		} catch (MissingHeaderException | SOAPException e) {
			CswLog.error(logger, String.format(AVOID, e.getMessage()));
		} 
		try {
			req_udr = getUDR(false, soapHeader);
		} catch (MissingHeaderException | SOAPException e) {
			CswLog.error(logger, String.format(AVOID, e.getMessage()));
		}
		try {
			suid = getSuid(soapHeader);
		} catch (MissingHeaderException | SOAPException e) {
			CswLog.error(logger, String.format(AVOID, e.getMessage()));
		} 

		FemsWsHeaderIn fems = new FemsWsHeaderIn();
		fems.setActor(actor);
		fems.setMustUnderstand(mustUnderstand);
		fems.setClientNetCode(clientNetCode);
		fems.setServerNetCode(serverNetCode);
		fems.setUDR(req_udr);
		fems.setApplCode(applCode);
		fems.setEnv(env);
		fems.setSuid(suid);
		return fems;
	}

	public static SOAPHeaderElement getFemsWsHeader(SOAPHeader soapHeader) throws SOAPException {
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

		return femsWsHeader;
	}

	public static String getClientNetCode(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
		String clientNetCode = "";
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {

			throw new MissingHeaderException("Header femsWs not present");
		} else {

			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j),
						FemsWsHeaders.CLIENT_NET_CODE_TAG_NAME)) {
					clientNetCode = femsWsHeader.getChildNodes().item(j).getTextContent();
				}
			}
		}

		return clientNetCode;
	}

	public static String getLauLabel(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
		String lauLabel = null;
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {

			throw new MissingHeaderException("Header femsWs not present");
		} else {

			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j),
						FemsWsHeaders.LAU_LABEL_TAG_NAME)) {
					lauLabel = femsWsHeader.getChildNodes().item(j).getTextContent();
				}
			}
		}

		return lauLabel;
	}

	public static String getServerNetCode(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
		String serverNetCode = "";
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {

			throw new MissingHeaderException("Header femsWs not present");
		} else {

			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j),
						FemsWsHeaders.SERVER_NET_CODE_TAG_NAME)) {
					serverNetCode = femsWsHeader.getChildNodes().item(j).getTextContent();
				}
			}
		}

		return serverNetCode;
	}

	public static String getApplCode(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
		String applCode = "";
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {
			throw new MissingHeaderException("Header femsWs not present");
		} else {
			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j),
						FemsWsHeaders.APPL_CODE_TAG_NAME)) {
					applCode = femsWsHeader.getChildNodes().item(j).getTextContent();
				}

			}
		}

		return applCode;
	}

	public static String getEnv(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {

		String env = "";

		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {

			throw new MissingHeaderException("Header femsWs not present");
		} else {

			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j), FemsWsHeaders.ENV_TAG_NAME)) {
					env = femsWsHeader.getChildNodes().item(j).getTextContent();
				}

			}
		}

		return env;
	}

	public static String getUDR(boolean isResponse, SOAPHeader soapHeader)
			throws MissingHeaderException, SOAPException {
		String udr = "";
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {
			if (!isResponse) {
				throw new MissingHeaderException("Header femsWs not present");
			} else {
				udr = "";
			}
		} else {
			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j), FemsWsHeaders.UDR_TAG_NAME)) {
					udr = femsWsHeader.getChildNodes().item(j).getTextContent();
				}
			}
			if (udr == null) {
				udr = "";
			}
		}

		return udr;
	}

	public static String getSuid(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {

		String suid = "";

		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {

			throw new MissingHeaderException("Header femsWs not present");
		} else {
			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j), FemsWsHeaders.SUID_TAG_NAME)) {
					suid = femsWsHeader.getChildNodes().item(j).getTextContent();
				}
			}
		}
		return suid;
	}

	public static String getReqSt(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
		String reqSt = "";
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {
			throw new MissingHeaderException("Header femsWs not present");
		} else {
			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j), FemsWsHeaders.REQ_ST)) {
					reqSt = femsWsHeader.getChildNodes().item(j).getTextContent();
				}
			}
		}

		return reqSt;
	}

	public static String getReqDt(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
		String reqDt = "";
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {
			throw new MissingHeaderException("Header femsWs not present");
		} else {

			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (FemsWsHeaders.isFemsWsElement(femsWsHeader.getChildNodes().item(j), FemsWsHeaders.REQ_DT)) {
					reqDt = femsWsHeader.getChildNodes().item(j).getTextContent();
				}
			}
		}

		return reqDt;
	}

	public static String isLAUEnabled(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
		String lauLabel = "disabled";
		SOAPHeaderElement femsWsHeader = getFemsWsHeader(soapHeader);

		if (femsWsHeader == null) {
			throw new MissingHeaderException("Header femsWs not present");
		} else {

			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {
				if (femsWsHeader.getChildNodes().item(j).getNodeName().contains("LAU")) {
					lauLabel = femsWsHeader.getChildNodes().item(j).getTextContent();
				}
			}
		}

		return lauLabel;
	}

	public static String getLAU(SOAPMessage soapMessage) throws SOAPException {
		String lau = "";
		SOAPHeader header = soapMessage.getSOAPHeader();
		SOAPHeaderElement security = null;

		Iterator it = header.getChildElements();
		while (it.hasNext()) {
			SOAPHeaderElement temp = (SOAPHeaderElement) it.next();

			if (temp.getLocalName().equalsIgnoreCase("Security")) {
				security = temp;
			}
		}
		for (int i = 0; i < Objects.requireNonNull(security).getFirstChild().getChildNodes().getLength(); i++) {

			if (security.getFirstChild().getChildNodes().item(i).getNodeName().equalsIgnoreCase("wsse:Password")) {
				lau = security.getFirstChild().getChildNodes().item(i).getTextContent();
			}
		}

		return lau;
	}

	public static String removeLAUHeader(String soapMessageAsString) throws ParserConfigurationException, SAXException, IOException, TransformerException{
		try (InputStream inputStream = new ByteArrayInputStream(
				soapMessageAsString.getBytes(StandardCharsets.UTF_8))) {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			Node node = document.getDocumentElement();
			Node header = null;
			NodeList elemList = node.getChildNodes();
			for (int i = 0; i < elemList.getLength(); i++) {
				if (elemList.item(i).getNodeName().contains("Header")) {
					header = elemList.item(i);
				}
			}
			if (header != null) {
				Node femsWsHeader = null;
				NodeList elemList2 = header.getChildNodes();
				for (int i = 0; i < elemList2.getLength(); i++) {
					if (elemList2.item(i).getNodeName().contains("FEMSWS")) {
						femsWsHeader = elemList2.item(i);
					}
				}

				NodeList elemList3 = Objects.requireNonNull(femsWsHeader).getChildNodes();
				for (int j = 0; j < elemList3.getLength(); j++) {
					Node temp = elemList3.item(j);
					if (temp.getNodeName().contains("LAU")) {
						femsWsHeader.removeChild(temp);
					}
				}
			}

			try (StringWriter stringWriter = new StringWriter()) {
				Source xmlSource = new DOMSource(document);
				Result outputTarget = new StreamResult(stringWriter);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.transform(xmlSource, outputTarget);

				return stringWriter.getBuffer().toString();
			}
		}
	}

	public static void append(String key, String value) {
		// TODO che cosa dovrebbe fare?
	}

	public static void append(FemsSession femsSession, String key, String value) {
		try {
			if (key.equalsIgnoreCase("suid")) {
				femsSession.getHeaderWs().setSuid(value);
			} else if (key.equalsIgnoreCase(FemsWsHeaders.RSP_DT)) {
				femsSession.getHeaderWs().setResDT(value);
			} else if (key.equalsIgnoreCase(FemsWsHeaders.RSP_ST)) {
				femsSession.getHeaderWs().setResST(value);
			} else if (key.equalsIgnoreCase(FemsWsHeaders.REQ_ST)) {
				femsSession.getHeaderWs().setReqST(value);
			} else if (key.equalsIgnoreCase(FemsWsHeaders.REQ_DT)) {
				femsSession.getHeaderWs().setReqDT(value);
			} else
				logger.info("Unable to store into session FEMS-WS header, Unknown key <{}>",key);
		} catch (Exception e) {
			logger.info("Unable to store into session FEMS-WS header <" + key + ">", e);
		}
	}
}
