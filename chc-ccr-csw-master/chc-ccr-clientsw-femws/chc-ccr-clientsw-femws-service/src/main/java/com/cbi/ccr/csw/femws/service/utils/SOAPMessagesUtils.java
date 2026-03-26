package com.cbi.ccr.csw.femws.service.utils;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
//import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Text;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cbi.ccr.csw.femws.service.FemsConfiguration;
import com.cbi.ccr.csw.femws.service.dto.FemsSession;
import com.cbi.ccr.csw.femws.service.dto.FemsWsHeaderIn;
import com.cbi.ccr.csw.femws.service.exception.MalformedHeaderException;
import com.cbi.ccr.csw.femws.service.exception.MalformedMessageException;
import com.cbi.ccr.csw.femws.service.exception.MaxMessageSizeException;
import com.cbi.ccr.csw.femws.service.exception.MissingHeaderException;
import com.cbi.ccr.csw.femws.service.exception.WrongAttachmentContentIdException;
import com.cbi.ccr.csw.femws.soap.FemsWsHeaders;

@Component
public class SOAPMessagesUtils {

	private final String WSSE_NS_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

	private final String WSSE_SECURITY_TAG_NAME = "Security";

	private final String FEM_WS_ACTOR = "urn:sia.eu:femsws";

	public static final String FEM_WS_OTHER_ERROR = "femsws:Client.OtherError";
	
	private final Logger log = LoggerFactory.getLogger(SOAPMessagesUtils.class);

	@Autowired
	private FemsConfiguration configuration;
	@Autowired
	private MessageUtils messageUtils;

	
	private SOAPMessagesUtils() {
	}

	public String getInputStreamToString(InputStream inputStream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();

		if (inputStream != null) {
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
				char[] charBuffer = new char[128];
				int bytesRead;
				while((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}
		}

		return stringBuilder.toString();
	}

	public SOAPMessage parse(HttpServletRequest request, FemsSession sessionWs, boolean isServer) throws IOException, SOAPException, MaxMessageSizeException, WrongAttachmentContentIdException  {
		InputStream in = request.getInputStream();
		MimeHeaders mimeHeaders = getHeaders(request);

		return getSoapMessage(sessionWs, in, mimeHeaders, isServer);
	}

//	public SOAPMessage parse(HttpResponse response, int maxSize, boolean storeFemsWsHeaderInSession,
//			boolean storeSoapVersionInSession, FemsSessionHeader sessionWs) throws Exception {
//		InputStream in = response.getEntity().getContent();
//		MimeHeaders mimeHeaders = getHeaders(response.getAllHeaders());
//		MessageUtils.printHeaders(response.getAllHeaders());
//
//		return getSoapMessage(storeFemsWsHeaderInSession, storeSoapVersionInSession, sessionWs, in, mimeHeaders);
//	}

	private SOAPMessage getSoapMessage(FemsSession sessionWs, InputStream in, MimeHeaders mimeHeaders, boolean isServer) throws SOAPException, MaxMessageSizeException, WrongAttachmentContentIdException, IOException  {
		if (in == null) {
			throw new SOAPException("Invalid null payload");
		}

		try(ByteArrayOutputStream baos = new ByteArrayOutputStream())  {
			IOUtils.copy(in, baos);

			byte[] msgBytes = baos.toByteArray();
			if (isCompressed(msgBytes)) {
				ByteArrayInputStream inToUnZip = new ByteArrayInputStream(msgBytes);
				in = new GZIPInputStream(inToUnZip);
				baos.reset();
				IOUtils.copy(in, baos);
				msgBytes = baos.toByteArray();
			} 

			MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);

			if (msgBytes.length > configuration.getMaxMessageSizeAllowed() * (1 << 20))
				throw new MaxMessageSizeException("Message size too big");

			
			sessionWs.setStream(msgBytes);

			sessionWs.setContentType(mimeHeaders.getHeader("content-type")[0]);

//			log.debug("--------------------------------------------");
//			log.debug("Response Message Incoming");
//			log.debug("Fems-Ws {} {}", isServer ? FemsConfiguration.TYPE_SERVER : FemsConfiguration.TYPE_CLIENT, configuration.getFemswsName());
//			log.debug("Content-Type = {}", mimeHeaders.getHeader("content-type")[0]);
//			if (mimeHeaders.getHeader("SOAPAction") == null)
//				log.debug("SOAPAction   = null");
//			else
//				log.debug("SOAPAction   = {}", mimeHeaders.getHeader("SOAPAction")[0]);
//			log.debug("--------------------------------------------");
//			String toSend = messageUtils.hideBodyTrace(new String(msgBytes));

			// if (toSend.length() > 1000) toSend = toSend.substring(0, 1000)+ " ... ...";
//			log.info("Message {}\n", toSend);

			SOAPMessage msg = null;
			try(ByteArrayInputStream bais = new ByteArrayInputStream(msgBytes)) {
				msg = msgFactory.createMessage(mimeHeaders, bais);
				/* sets the real SOAP version */
				sessionWs.setSoapVersion11(isSoap11(msg.getSOAPPart().getEnvelope()));
				sessionWs.setPrefixEnvelope(msg.getSOAPPart().getEnvelope().getPrefix());
				sessionWs.setLocalNameEnvelope(msg.getSOAPPart().getEnvelope().getLocalName());
				
			} 

			Set<String> duplicatedContentIds = getDuplicatedContentIds(msg);
			if (!duplicatedContentIds.isEmpty()) {
				throw new WrongAttachmentContentIdException(duplicatedContentIds);
			}

			return msg;
		} finally {
			in.close();
		}
	}

	public boolean isCompressed(byte[] bytes) throws IOException {
		if ((bytes == null) || (bytes.length < 2)) {
			return false;
		} else {
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
					&& (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
		}
	}

	public String soapMessagePrettyPrinter(SOAPMessage soapMessage) throws SOAPException {
		// Get the Envelope Source
		Source src = soapMessage.getSOAPPart().getContent();

		try {
			// Transform the Source into a StreamResult to get the XML
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");

			try (StringWriter sw = new StringWriter()) {
				StreamResult result = new StreamResult(sw);
				transformer.transform(src, result);
				return result.getWriter().toString();
			} catch (IOException e) {
				log.error("Errore in creazione StringWriter " + e.getLocalizedMessage());
			}
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			log.error("Errore in formattazione del pacchetto " + e.getLocalizedMessage());
		}
		return src.toString();
	}

	// ** getFemsWsHeader - START**//
	public SOAPHeaderElement getFemsWsHeader(SOAPHeader soapHeader) throws SOAPException {

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

	public SOAPMessage buildSOAPFault(FemsSession session, String faultCode, String faultString,
			String faultActor, boolean isServer) {
		SOAPMessage soapMsg = null;
		try {
			soapMsg = MessageFactory.newInstance(
					session.isSoapVersion11() ? SOAPConstants.SOAP_1_1_PROTOCOL : SOAPConstants.SOAP_1_2_PROTOCOL)
					.createMessage();

			SOAPBody soapBody = soapMsg.getSOAPBody();
			SOAPFault fault = soapBody.addFault();

			String faultCodeNs = "";

			if (!isServer) {
				faultCodeNs = "FemsWsClient";
			}else {
				faultCodeNs = "FemsWsServer";
			}
			
			if (session.isSoapVersion11()) {
				QName faultName = new QName(faultCodeNs, faultCode);
				fault.setFaultCode(faultName);
			} else {
				QName faultName = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Sender");
				fault.setFaultCode(faultName);
				QName faultSubCodeName = new QName(faultCodeNs, faultCode);
				fault.appendFaultSubcode(faultSubCodeName);
			}

			if (faultString == null)
				faultString = "Check the Exception";
			if (session.isSoapVersion11()) {
				fault.setFaultString(faultString);
			} else {
				fault.addFaultReasonText(faultString, Locale.US);
			}
			if (session.isSoapVersion11()) {
				//session.setCONTENT_TYPE("text/xml; charset=utf-8");
				session.setContentType("text/xml; charset=utf-8");
			} else {
				session.setContentType("application/soap+xml; charset=utf-8");
			}

			fault.setFaultActor(faultActor);
			createHeader(soapMsg, session.getHeaderWs());
			soapMsg.saveChanges();
		} catch (SOAPException e) {
			return null;
		}
		return soapMsg;
	}

	
	public SOAPMessage buildSOAPFault(boolean isSoapVersion12, String faultCode, String faultString,
			String faultActor, boolean isServer) {
		SOAPMessage soapMsg = null;
		try {
			soapMsg = MessageFactory.newInstance(
					isSoapVersion12 ? SOAPConstants.SOAP_1_2_PROTOCOL : SOAPConstants.SOAP_1_1_PROTOCOL)
					.createMessage();

			SOAPBody soapBody = soapMsg.getSOAPBody();
			SOAPFault fault = soapBody.addFault();

			String faultCodeNs = "";

			if (!isServer) {
				faultCodeNs = "FemsWsClient";
			}else {
				faultCodeNs = "FemsWsServer";
			}
			
			if (!isSoapVersion12) {
				QName faultName = new QName(faultCodeNs, faultCode);
				fault.setFaultCode(faultName);
			} else {
				QName faultName = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Sender");
				fault.setFaultCode(faultName);
				QName faultSubCodeName = new QName(faultCodeNs, faultCode);
				fault.appendFaultSubcode(faultSubCodeName);
			}

			if (faultString == null)
				faultString = "Check the Exception";
			if (!isSoapVersion12) {
				fault.setFaultString(faultString);
			} else {
				fault.addFaultReasonText(faultString, Locale.US);
			}
			fault.setFaultActor(faultActor);

			soapMsg.saveChanges();
		} catch (SOAPException e) {
			return null;
		}
		return soapMsg;
	}
	
	protected MimeHeaders getHeaders(HttpServletRequest req) {
		Enumeration<String> unum = req.getHeaderNames();
		MimeHeaders headers = new MimeHeaders();

		while (unum.hasMoreElements()) {
			String headerName = unum.nextElement();
			String headerValue = req.getHeader(headerName);

			for (String value : headerValue.split(","))
				headers.addHeader(headerName, value.trim());
		}

		return headers;
	}

	protected MimeHeaders getHeaders(Header[] header) {
		MimeHeaders headers = new MimeHeaders();

		for (Header head : header) {
			String headerName = head.getName();
			String headerValue = head.getValue();
			if (!headerName.equalsIgnoreCase("sessionWs")) {

				for (String value : headerValue.split(","))
					headers.addHeader(headerName, value.trim());
			}
		}

		return headers;
	}

	public boolean isSoap11(SOAPHeader soapHeader) {
		return SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(soapHeader.getElementQName().getNamespaceURI());
	}

	public boolean isSoap11(SOAPEnvelope soapEnv) {
		return SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(soapEnv.getElementQName().getNamespaceURI());
	}

	public boolean isSoap11(Document doc) {
		if (doc.getDocumentElement() != null) {
			return SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(doc.getDocumentElement().getNamespaceURI());
		}
		return false;
	}

	private Set<String> getDuplicatedContentIds(SOAPMessage soapMsg) throws SOAPException {
		Set<String> contentIds = new HashSet<String>();
		Set<String> duplicatedContentIds = new HashSet<String>();
		for (Iterator<?> i = soapMsg.getAttachments(); i.hasNext();) {
			AttachmentPart attachment = (AttachmentPart) i.next();
			if (attachment.getContentType().startsWith("multipart/")) {
				for (String contentId : getSubAttachmentContentIds(attachment)) {
					if (!contentIds.add(contentId)) {
						duplicatedContentIds.add(contentId);
					}
				}
			} else {
				String contentId = attachment.getContentId();
				if (!contentIds.add(contentId)) {
					duplicatedContentIds.add(contentId);
				}
			}
		}
		return duplicatedContentIds;
	}

	private List<String> getSubAttachmentContentIds(AttachmentPart attachment) {
		List<String> contentIds = new ArrayList<>();
		String subBoundary = getBoundary(attachment.getContentType());
		if (subBoundary == null) {
			return Collections.emptyList();
		}
//		MIMEMessage msg = new MIMEMessage(attachment.getRawContent(), subBoundary, new MIMEConfig());
//		for (MIMEPart part : msg.getAttachments()) {
//			contentIds.add(part.getContentId());
//		}
//		msg.close();
		return contentIds;
	}

	public String getBoundary(String contentType) {
		return getHeaderParameterValue(contentType, "boundary");
	}

	private String getHeaderParameterValue(String s, String name) {
		if (s == null) {
			return null;
		}
		String value = null;
		String prefix = name + "=";
		int pos = s.indexOf(prefix);
		if (pos == -1) {
			return null;
		}
		pos = pos + prefix.length();
		if (s.indexOf(';', pos) == -1) {
			value = s.substring(pos).trim();
		} else {
			value = s.substring(pos, s.indexOf(';', pos)).trim();
		}
		if (value.startsWith("\"")) {
			value = value.substring(1, value.length() - 1); // strip quotes
		}

		return value;
	}

	public String getServerNetCode(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
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

	public boolean isNextActor(String actor, boolean soap11) {
		if (soap11) {
			return SOAPConstants.URI_SOAP_ACTOR_NEXT.equals(actor);
		} else {
			return SOAPConstants.URI_SOAP_1_2_ROLE_NEXT.equals(actor);
		}
	}

	public String getClientNetCode(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
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

	public String getApplCode(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
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

	public String getSuid(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
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

	public String getReqSt(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
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

	public String getReqDt(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
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

	public String isLAUEnabled(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {
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

	public String getLAU(SOAPMessage soapMessage) throws SOAPException {
		String lau = "";
		SOAPHeader header = soapMessage.getSOAPHeader();
		SOAPHeaderElement security = null;

		Iterator<?> it = header.getChildElements();
		while (it.hasNext()) {
			SOAPHeaderElement temp = (SOAPHeaderElement) it.next();

			if (temp.getLocalName().equalsIgnoreCase("Security")) {
				security = temp;
			}

		}
		for (int i = 0; i < security.getFirstChild().getChildNodes().getLength(); i++) {

			if (security.getFirstChild().getChildNodes().item(i).getNodeName().equalsIgnoreCase("wsse:Password")) {
				lau = security.getFirstChild().getChildNodes().item(i).getTextContent();
			}
		}

		return lau;
	}

	public String removeLAUHeader(String soapMessageAsString) throws Exception {
		try (InputStream inputStream = new ByteArrayInputStream(
				soapMessageAsString.getBytes((Charset.forName("UTF-8"))))) {
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

				NodeList elemList3 = femsWsHeader.getChildNodes();
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

				String result = stringWriter.getBuffer().toString();

				// inputStream.close();

				return result;
			}
		}
	}

	public String getEnv(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {

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

	public String getUDR(boolean isResponse, SOAPHeader soapHeader)
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

	public Integer getNodeSize(Node node) {
		String nodeAsString = null;
		Integer nodeSize = 0;

		try {
			Source source = new DOMSource(node);
			try (StringWriter stringWriter = new StringWriter()) {
				Result result = new StreamResult(stringWriter);
				TransformerFactory factory = TransformerFactory.newInstance();
				factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
				factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
				Transformer transformer = factory.newTransformer();
				transformer.transform(source, result);
				nodeAsString = stringWriter.getBuffer().toString();
				nodeSize = nodeAsString.getBytes(Charset.forName("UTF-8")).length;
			}
		} catch (Exception e) {
			log.error("error ", e);
		}

		return nodeSize;
	}

	public Integer getBodySize(Node node) {
		String nodeAsString = null;
		Integer nodeSize = 0;
		StringWriter stringWriter = null;
		try {
			Source source = new DOMSource(node);
			stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			nodeAsString = stringWriter.getBuffer().toString();

			// Estrazione del contenuto del Body al byte
			int index = nodeAsString.toLowerCase().indexOf("body");
			nodeAsString = nodeAsString.substring(index);
			index = nodeAsString.toLowerCase().indexOf(">");
			nodeAsString = nodeAsString.substring(index + 1);
			index = nodeAsString.toLowerCase().lastIndexOf("body");
			if (index != -1) {
				for (int i = index; nodeAsString.toLowerCase().charAt(i) != '<'; i--)
					index = i;
				nodeAsString = nodeAsString.substring(0, index - 1);
			}
			nodeSize = nodeAsString.getBytes(Charset.forName("UTF-8")).length;
		} catch (Exception e) {
			log.error("error ", e);
		} finally {
			if (stringWriter != null)
				try {
					stringWriter.close();
				} catch (IOException e) {
					log.error("error ", e);
				}
		}
		return nodeSize;
	}

	public boolean checkMessageSize(InputStream in, String encoding, float size) throws IOException {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024 * 4];
			long count = 0;
			int n = 0;
			while (-1 != (n = in.read(buffer))) {
				byteOut.write(buffer, 0, n);
				count += n;
				if (count > size) {
					return false;
				}
			}
		}

		return true;
	}

	public String getLauLabel(SOAPHeader soapHeader) throws MissingHeaderException, SOAPException {

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

	public void isMalformed(FemsSession session, SOAPMessage messageSOAP, boolean isResponse, boolean isFWServer)
			throws MalformedHeaderException, MissingHeaderException, MalformedMessageException, SOAPException {

		log.info("Check FemsWsServlet Header...");

		try {
			// verifico di avere un solo blocco SOAPHeader e un solo blocco Body!
			SOAPHeaderElement femsWsHeader = null;
			int countHeader = 0;
			int countBody = 0;
			int counter = -1;
			for (int j = 0; j < messageSOAP.getSOAPPart().getEnvelope().getChildNodes().getLength(); j++) {
				Node node = messageSOAP.getSOAPPart().getEnvelope().getChildNodes().item(j);

				if (node instanceof Text) {
					counter++;
					String str = ((Text) node).getValue();
					if (!str.matches("[\n\t ]*"))
						throw new MalformedMessageException("Text unexpected");
				}

				if (node instanceof SOAPHeader) {
					countHeader++;
					counter++;
				}

				if (node instanceof SOAPBody) {
					countBody++;
					counter++;
				}

				if (counter != j)
					throw new MalformedMessageException("Element unexpected");

			}

			if (countHeader == 0)
				throw new MalformedMessageException("SOAP header is null");

			if (countHeader > 1)
				throw new MalformedMessageException("Only one Header is allowed");

			if (countBody == 0) // non e' un controllo specifico dell'header
				throw new MalformedMessageException("SOAP body is null");

			if (countBody != 1) // non e' un controllo specifico dell'header
				throw new MalformedMessageException("Only one Body is allowed");

			if (countBody + countHeader != 2)
				throw new MalformedMessageException("Soap Element unexpected");

			// verifico di avere un solo blocco FEMSWSHeader!
			int count = 0;
			SOAPHeader soapHeader = messageSOAP.getSOAPHeader();
			Iterator<javax.xml.soap.Node> it = soapHeader.getChildElements();
			while (it.hasNext()) {

				Object tmp = it.next();
				if (tmp instanceof SOAPHeaderElement) {
					SOAPHeaderElement temp = (SOAPHeaderElement) tmp;

					if (temp.getLocalName().equalsIgnoreCase("FEMSWSHeader")) {
						femsWsHeader = temp;
						count++;
					}
				}
			}

			if (count == 0)
				throw new MissingHeaderException("FEMSWSHEader missing");

			if (count > 1)
				throw new MalformedHeaderException("Malformed FEMSWSHeader-FEMSWSHeader duplicated");

			String femswsNamespace = femsWsHeader.getNamespaceURI();

			if (femswsNamespace == null)
				throw new MalformedHeaderException("Malformed FEMSWSHeader-FEMSWSHeader namespace not valid");

			if (!FemsWsHeaders.NS_URI.equalsIgnoreCase(femswsNamespace))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-FEMSWSHeader namespace not valid");

			// Controllo il numero di argomenti
			NamedNodeMap elemMap = femsWsHeader.getAttributes();

			if (elemMap.getLength() != 3)
				throw new MalformedHeaderException("Malformed FEMSWSHeader-Attributes not valid");

			String actor = femsWsHeader.getActor();

			if (actor == null)
				throw new MalformedHeaderException("Malformed FEMSWSHeader-Attribute actor not valid");

			if (!isNextActor(actor, isSoap11(soapHeader)))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-Attribute actor not valid");

			Boolean mustUnderstand = femsWsHeader.getMustUnderstand();

			if (Boolean.FALSE.equals(mustUnderstand))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-Attribute mustUnderstand not valid");

			Integer clientNetCodeCount = 0;
			Integer serverNetCodeCount = 0;
			Integer udrCount = 0;
			Integer envCount = 0;
			Integer suidCount = 0;
			Integer reqStCount = 0;
			Integer reqDtCount = 0;
			Integer applCodeCount = 0;
			Integer lauLabelCount = 0;
			counter = -1;
			for (int j = 0; j < femsWsHeader.getChildNodes().getLength(); j++) {

				Node node = femsWsHeader.getChildNodes().item(j);

				if (node instanceof Text) {
					counter++;
					String str = ((Text) node).getValue();
					if (!str.matches("[\n\t ]*"))
						throw new MalformedHeaderException("Malformed FEMSWSHeader-Text unexpected");

				}

				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.CLIENT_NET_CODE_TAG_NAME)) {
					clientNetCodeCount++;
					counter++;
				}
				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.LAU_LABEL_TAG_NAME)) {
					lauLabelCount++;
					counter++;
				}
				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.SERVER_NET_CODE_TAG_NAME)) {
					serverNetCodeCount++;
					counter++;
				}
				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.APPL_CODE_TAG_NAME)) {
					applCodeCount++;
					counter++;
				}
				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.ENV_TAG_NAME)) {
					envCount++;
					counter++;
				}
				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.UDR_TAG_NAME)) {
					udrCount++;
					counter++;
				}
				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.SUID_TAG_NAME)) { // solo server
					suidCount++;
					counter++;
				}
				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.REQ_ST)) { // solo server
					reqStCount++;
					counter++;
				}
				if (FemsWsHeaders.isFemsWsElement(node, FemsWsHeaders.REQ_DT)) { // solo server
					reqDtCount++;
					counter++;
				}

				if (counter != j)
					throw new MalformedHeaderException("Malformed FEMSWSHeader-Element unexpected");

			}

			// ClientNetCodeCount
			if (clientNetCodeCount != 1)
				throw new MalformedHeaderException(extendCause(clientNetCodeCount, "ClientNetCode"));

			String clientNetCode = getClientNetCode(soapHeader);

			if (isResponse && !clientNetCode.equalsIgnoreCase(session.getHeaderWs().getClientNetCode()))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-ClientNetCode not valid");

			if (!isResponse && !clientNetCode.matches("[0-9A-Z_]{5}"))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-ClientNetCode not valid");

			// ServerNetCodeCount
			if (serverNetCodeCount != 1)
				throw new MalformedHeaderException(extendCause(serverNetCodeCount, "ServerNetCode"));

			String serverNetCode = getServerNetCode(soapHeader);

			if (isResponse && !serverNetCode.equalsIgnoreCase(session.getHeaderWs().getServerNetCode()))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-ServerNetCode not valid");

			if (!isResponse && !serverNetCode.matches("[0-9A-Z_]{5}"))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-ServerNetCode not valid");

			// applcode
			if (applCodeCount != 1)
				throw new MalformedHeaderException(extendCause(applCodeCount, "ApplCode"));

			String applCode = getApplCode(soapHeader);

			if (isResponse && !applCode.equalsIgnoreCase(session.getHeaderWs().getApplCode()))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-ApplCode not valid");

			if (!isResponse && !applCode.matches("[0-9A-Z_]{5}"))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-ApplCode not valid");

			// env
			if (envCount != 1)
				throw new MalformedHeaderException(extendCause(envCount, "Env"));

			String env = getEnv(soapHeader);

			if (isResponse && !env.equalsIgnoreCase(session.getHeaderWs().getEnv()))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-Env not valid");

			if (!isResponse && !env.matches("[0-9A-Z_]{2}"))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-Env not valid");

			// Lau
			if (isResponse && lauLabelCount > 0)
				throw new MalformedHeaderException(extendCause(lauLabelCount, "LAULabel"));

			if (!isResponse && lauLabelCount > 1)
				throw new MalformedHeaderException(extendCause(lauLabelCount, "LAULabel"));

			// Udr
			if (udrCount != 1)
				throw new MalformedHeaderException(extendCause(udrCount, "UDR"));

			String udr = getUDR(true, soapHeader);

			if (!udr.matches("[0-9A-Za-z_]{1,80}"))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-UDR not valid");

			// suid
			if (isFWServer && suidCount != 1)
				throw new MalformedHeaderException(extendCause(suidCount, "SUID"));

			if (!isFWServer && suidCount != 1 && isResponse)
				throw new MalformedHeaderException(extendCause(suidCount, "SUID"));
//TODO
//			if (!isFWServer && suidCount != 0 && !isResponse)
//				throw new MalformedHeaderException("Malformed FEMSWSHeader-SUID not valid");

			String suid = getSuid(soapHeader);

			if (isResponse && !suid.equalsIgnoreCase(session.getHeaderWs().getSuid()))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-SUID not valid");

			// reqSt
			if (isFWServer && reqStCount != 1)
				throw new MalformedHeaderException(extendCause(reqStCount, "ReqST"));

			if (!isFWServer && reqStCount != 1 && isResponse)
				throw new MalformedHeaderException(extendCause(reqStCount, "ReqST"));
//TODO
///			if (!isFWServer && reqStCount != 0 && !isResponse)
//				throw new MalformedHeaderException("Malformed FEMSWSHeader-ReqST not valid");

			String reqSt = getReqSt(soapHeader);

			if (isResponse && !reqSt.equalsIgnoreCase(session.getHeaderWs().getReqST()))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-ReqST not valid");

			// reqDt
			if (isResponse && reqDtCount != 1)
				throw new MalformedHeaderException(extendCause(reqDtCount, "ReqDT"));

			String reqDt = getReqDt(soapHeader);

			if (isResponse && !reqDt.equalsIgnoreCase(session.getHeaderWs().getReqDT()))
				throw new MalformedHeaderException("Malformed FEMSWSHeader-ReqDT not valid");

			log.info("FemsWsServlet Header Checked...");

		} catch (MalformedHeaderException e) {
			log.info("Check FemsWsServlet Header Fails...");
			throw e;
		} catch (MissingHeaderException e) {
			log.info("Check FemsWsServlet Header Fails...");
			throw e;
		} catch (MalformedMessageException e) {
			log.info("Check FemsWsServlet Header Fails...");
			throw e;
		} catch (SOAPException e) {
			log.info("Check FemsWsServlet Header Fails...");
			throw e;
		}
	}

	private String extendCause(int count, String tag) {
		if (count == 0) {
			return "Malformed FEMSWSHeader-Missing " + tag;
		} else if (count > 1) {
			return "Malformed FEMSWSHeader-Multiple " + tag;
		} else {
			return "";
		}
	}

	public SOAPElement getChildElementByTagNameNS(SOAPElement elem, String nsUri, String localName,
			Predicate predicate) {
		for (Iterator<?> i = elem.getChildElements(); i.hasNext();) {
			Node child = (Node) i.next();
			if (child instanceof SOAPElement) {
				if (Objects.equals(child.getLocalName(), localName)
						&& Objects.equals(child.getNamespaceURI(), nsUri)) {
					return (SOAPElement) child;
				}
			}
		}
		return null;
	}

	public String getFirstHeaderValue(SOAPMessage msg, String headerName) {
		String[] value = msg.getMimeHeaders().getHeader(headerName);
		if (value.length == 0) {
			return "";
		} else {
			return value[0];
		}
	}
	
	public void createHeader(SOAPMessage responseSOAP, FemsWsHeaderIn headersWs) throws SOAPException {
		SOAPHeader header = responseSOAP.getSOAPHeader();
		if (header == null) {
			header = responseSOAP.getSOAPPart().getEnvelope().addHeader();
		}

		log.info("Recovering headers...");

		Name femswsHeaderName = responseSOAP.getSOAPPart().getEnvelope().createName("FEMSWSHeader", "femsws",
				"urn:sia.eu:femsws:header:v1.0");

		if (headersWs == null) {
			return;
		}
		Map<String, Object> headers;
		SOAPHeaderElement femsWsElem;
		Element udrElem;
		
		femsWsElem = getFemsWsHeader(header);
		if (femsWsElem == null) {
			femsWsElem = header.addHeaderElement(femswsHeaderName);
		}
		if (isSoap11(header)) {
			femsWsElem.addAttribute(
					new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "actor",
							responseSOAP.getSOAPPart().getEnvelope().getPrefix()),
					"http://schemas.xmlsoap.org/soap/actor/next");
			femsWsElem.addAttribute(new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "mustUnderstand",
					responseSOAP.getSOAPPart().getEnvelope().getPrefix()), "1");
		} else {
			femsWsElem.addAttribute(
					new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "role",
							responseSOAP.getSOAPPart().getEnvelope().getPrefix()),
					"http://www.w3.org/2003/05/soap-envelope/role/next");
			femsWsElem.addAttribute(new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "mustUnderstand",
					responseSOAP.getSOAPPart().getEnvelope().getPrefix()), "true");
		}

		headers = new LinkedHashMap<>();

		if (headersWs.getClientNetCode() != null)
			headers.put("ClientNetCode", headersWs.getClientNetCode());

		if (headersWs.getServerNetCode() != null)
			headers.put("ServerNetCode", headersWs.getServerNetCode());

		if (headersWs.getUDR() != null)
			headers.put("UDR", headersWs.getUDR());

		if (headersWs.getApplCode() != null)
			headers.put("ApplCode", headersWs.getApplCode());

		if (headersWs.getEnv() != null)
			headers.put("Env", headersWs.getEnv());

		if (headersWs.getSuid() != null)
			headers.put("SUID", headersWs.getSuid());

		if (headersWs.getLau() != null)
			headers.put("lau", headersWs.getLau());

		if (headersWs.getReqST() != null)
			headers.put("ReqST", headersWs.getReqST());

		if (headersWs.getReqDT() != null)
			headers.put("ReqDT", headersWs.getReqDT());

		if (headersWs.getResST() != null)
			headers.put("ResST", headersWs.getResST());

		if (headersWs.getResDT() != null)
			headers.put("ResDT", headersWs.getResDT());

		Set<String> keys = headers.keySet();
		Iterator<?> it = keys.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();

			if (!key.equals("actor") && !key.equals("mustUnderstand") && !key.equals("role")) {
				Object valueObj = headers.get(key);
				String value = (valueObj != null) ? valueObj.toString() : "";
				if (!"".equals(value)) {
					if (!femsWsElem.getChildElements(FemsWsHeaders.getQName(key)).hasNext()) {
						SOAPElement temp2 = femsWsElem.addChildElement(FemsWsHeaders.getQName(key));
						temp2.setValue(headers.get(key).toString());
					}
				}
			}
		}

		for (int j = 0; j < femsWsElem.getChildNodes().getLength(); j++) {
			if (femsWsElem.getChildNodes().item(j) instanceof Element) {
				if (FemsWsHeaders.isFemsWsElement(femsWsElem.getChildNodes().item(j), FemsWsHeaders.UDR_TAG_NAME)) {
					udrElem = (Element) femsWsElem.getChildNodes().item(j);
				}
			}
		}

		log.info("Headers recovered");
	}


	public void composeFemsWsHeaderByUrlPath(FemsSession femsSession, SOAPMessage responseSOAP)
			throws MalformedHeaderException, SOAPException {
		try {

			String udr = "NA";
			String cnc = femsSession.getHeaderWs().getClientNetCode();
			String snc = femsSession.getHeaderWs().getServerNetCode();
			String ap = femsSession.getHeaderWs().getApplCode();
			String env = femsSession.getHeaderWs().getEnv();
			String udralgo = femsSession.getUdrAlgo();
			String lau = femsSession.getHeaderWs().getLau();

//			if (!femsSession.getRequestPath().equalsIgnoreCase(FemsConfiguration.femsWsHeaderHandlerRequestPath))
//				throw new MalformedHeaderException("Malformed FEMSWSHeader-Wrong request Path");

			if (cnc == null || snc == null || ap == null || env == null || udralgo == null)
				throw new MalformedHeaderException(
						"Malformed FEMSWSHeader-Missing or duplicated required parameter in query string");

			SOAPHeader header = responseSOAP.getSOAPHeader();

			if (header == null) {
				header = responseSOAP.getSOAPPart().getEnvelope().addHeader();
			}

			log.info("Building headers from urlPath...");

			Name femswsHeaderName = responseSOAP.getSOAPPart().getEnvelope().createName("FEMSWSHeader", "femsws",
					"urn:sia.eu:femsws:header:v1.0");

			if (!udralgo.equalsIgnoreCase("PDD") && !udralgo.equalsIgnoreCase("DEF"))
				throw new MalformedHeaderException(
						"Malformed FEMSWSHeader-Unforeseen value for parameter UDRALG='" + udralgo + "'");

			// * RECUPERO UDR
			if (udralgo.equalsIgnoreCase("PDD")) {
				try {
					SOAPHeaderElement elem = getHeader4UdrElement(header);

					if (elem != null) {

						String identificativoDominio = null;
						String codiceContestoPagamento = null;
						String identificativoUnivocoVersamento = null;

						for (int j = 0; j < elem.getChildNodes().getLength(); j++) {

							if (elem.getChildNodes().item(j) instanceof Element) {

								if (elem.getChildNodes().item(j).getLocalName()
										.equalsIgnoreCase("identificativoDominio"))
									identificativoDominio = elem.getChildNodes().item(j).getTextContent();

								if (elem.getChildNodes().item(j).getLocalName()
										.equalsIgnoreCase("identificativoUnivocoVersamento"))
									identificativoUnivocoVersamento = elem.getChildNodes().item(j).getTextContent();

								if (elem.getChildNodes().item(j).getLocalName()
										.equalsIgnoreCase("codiceContestoPagamento"))
									codiceContestoPagamento = elem.getChildNodes().item(j).getTextContent();
							}
						}

						if (codiceContestoPagamento == null)
							log.warn("codiceContestoPagamento not found");
						if (identificativoUnivocoVersamento == null)
							log.warn("identificativoUnivocoVersamento not found");
						udr = identificativoDominio + "_" + identificativoUnivocoVersamento + "_"
								+ codiceContestoPagamento;
						udr = udr.replaceAll("[^a-zA-Z0-9_]", "");

						if (udr.length() > 80)
							udr = udr.substring(0, 80);

						log.debug("udr builded=" + udr);
					}
				} catch (Exception ex) {
					udr = "";
				//	EventLog.error(UDR_CALC_ERR, ex.getMessage());  TODO
				}
			}

			Map<String, Object> headers = new LinkedHashMap<>();;
			SOAPHeaderElement femsWsElem;
			Element udrElem;

			femsSession.getHeaderWs().setUDR(udr);

			if (femsSession.getHeaderWs().getClientNetCode() != null)
				headers.put("ClientNetCode", femsSession.getHeaderWs().getClientNetCode());

			if (femsSession.getHeaderWs().getServerNetCode() != null)
				headers.put("ServerNetCode", femsSession.getHeaderWs().getServerNetCode());

			if (femsSession.getHeaderWs().getUDR() != null)
				headers.put("UDR", femsSession.getHeaderWs().getUDR());

			if (femsSession.getHeaderWs().getApplCode() != null)
				headers.put("ApplCode", femsSession.getHeaderWs().getApplCode());

			if (femsSession.getHeaderWs().getEnv() != null)
				headers.put("Env", femsSession.getHeaderWs().getEnv());

			if (femsSession.getHeaderWs().getSuid() != null)
				headers.put("SUID", femsSession.getHeaderWs().getSuid());

			if (femsSession.getHeaderWs().getLau() != null)
				headers.put("lau", femsSession.getHeaderWs().getLau());

			if (femsSession.getHeaderWs().getReqST() != null)
				headers.put("ReqST", femsSession.getHeaderWs().getReqST());

			if (femsSession.getHeaderWs().getReqDT() != null)
				headers.put("ReqDT", femsSession.getHeaderWs().getReqDT());

			if (femsSession.getHeaderWs().getResST() != null)
				headers.put("ResST", femsSession.getHeaderWs().getResST());

			if (femsSession.getHeaderWs().getResDT() != null)
				headers.put("ResDT", femsSession.getHeaderWs().getResDT());

			femsWsElem = getFemsWsHeader(header);
			if (femsWsElem == null) {
				femsWsElem = header.addHeaderElement(femswsHeaderName);
			} else {
				throw new MalformedHeaderException("Malformed FEMSWSHeader-FEMSWSHeader not allowed ");
			}

			if (headers.get("actor") != null && !"".equals(headers.get("actor"))) {
				femsWsElem.setActor((String) headers.get("actor"));
			}

			if (headers.get("mustUnderstand") != null) {
				femsWsElem.setMustUnderstand((Boolean) headers.get("mustUnderstand"));
			}

			femsWsElem.addChildElement(FemsWsHeaders.getQName("ClientNetCode")).setValue(cnc);
			femsWsElem.addChildElement(FemsWsHeaders.getQName("ServerNetCode")).setValue(snc);
			femsWsElem.addChildElement(FemsWsHeaders.getQName("UDR")).setValue(udr);
			femsWsElem.addChildElement(FemsWsHeaders.getQName("ApplCode")).setValue(ap);
			femsWsElem.addChildElement(FemsWsHeaders.getQName("Env")).setValue(env);
			if (lau != null)
				femsWsElem.addChildElement(FemsWsHeaders.getQName("LAULabel")).setValue(lau);

			if (isSoap11(header)) {
				femsWsElem.addAttribute(
						new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "actor",
								responseSOAP.getSOAPPart().getEnvelope().getPrefix()),
						"http://schemas.xmlsoap.org/soap/actor/next");
				femsWsElem.addAttribute(new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "mustUnderstand",
						responseSOAP.getSOAPPart().getEnvelope().getPrefix()), "1");
			} else {
				femsWsElem.addAttribute(
						new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "role",
								responseSOAP.getSOAPPart().getEnvelope().getPrefix()),
						"http://www.w3.org/2003/05/soap-envelope/role/next");
				femsWsElem.addAttribute(new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "mustUnderstand",
						responseSOAP.getSOAPPart().getEnvelope().getPrefix()), "true");
			}

			log.info("Headers builded");

		} catch (MalformedHeaderException e) {
			log.info("Unable to create FEMS-WS header using query string", e);
			throw e;
		}

	}


	public static SOAPHeaderElement getHeader4UdrElement(SOAPHeader soapHeader) throws SOAPException {

		SOAPHeaderElement result = null;

		Iterator<?> it = soapHeader.getChildElements();

		while (it.hasNext()) {

			Object tmp = it.next();
			if (tmp instanceof SOAPHeaderElement) {
				SOAPHeaderElement temp = (SOAPHeaderElement) tmp;

				if (temp.getLocalName().equalsIgnoreCase("intestazionePPT")
						&& temp.getNamespaceURI().equalsIgnoreCase("http://ws.pagamenti.telematici.gov/ppthead")) {
					result = temp;
				}
			}
		}

		return result;
	}
}
