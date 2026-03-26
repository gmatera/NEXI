package com.cbi.ccr.outbound.service;


import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SOAPMessagesUtils {

	public static final String FEM_WS_OTHER_ERROR = "femsws:Client.OtherError";
	

	public String soapMessagePrettyPrinter(SOAPMessage soapMessage) throws SOAPException {
		// Get the Envelope Source
		Source src = soapMessage.getSOAPPart().getContent();

		try {
			// Transform the Source into a StreamResult to get the XML
			
			TransformerFactory factory = javax.xml.transform.TransformerFactory.newInstance();
			
			// SONAR to be compliant, prohibit the use of all protocols by external entities:
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			return transform(transformer, src);
			
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			log.error("Errore in formattazione del pacchetto {}", e.getLocalizedMessage());
		}
		return src.toString();
	}
	
	private String transform(Transformer transformer, Source src ) throws TransformerException {
		try (StringWriter sw = new StringWriter()) {
			StreamResult result = new StreamResult(sw);
			transformer.transform(src, result);
			return result.getWriter().toString();
		} catch (IOException e) {
			log.error("Errore in creazione StringWriter {}", e.getLocalizedMessage());
		}
		return src.toString();
	}


	public String buildSOAPFault(boolean isSoapVersion11, String faultCode, String faultString,
			boolean isServer, SOAPHeaderElement femsWsElem) {
		SOAPMessage soapMsg = null;
		try {
			soapMsg = MessageFactory.newInstance(
					isSoapVersion11 ? SOAPConstants. SOAP_1_1_PROTOCOL: SOAPConstants.SOAP_1_2_PROTOCOL)
					.createMessage();

			SOAPBody soapBody = soapMsg.getSOAPBody();
			SOAPFault fault = soapBody.addFault();

			SOAPHeader header = soapMsg.getSOAPHeader();
			if (header == null) {
				header = soapMsg.getSOAPPart().getEnvelope().addHeader();
			}
			
			header.addChildElement(femsWsElem);
			
			String faultCodeNs = "";

			if (!isServer) {
				faultCodeNs = "FemsWsClient";
			}else {
				faultCodeNs = "FemsWsServer";
			}
			
			if (isSoapVersion11) {
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
			if (isSoapVersion11) {
				fault.setFaultString(faultString);
			} else {
				fault.addFaultReasonText(faultString, Locale.US);
			}
			fault.setFaultActor("CCR");

			soapMsg.saveChanges();
			
			return soapMessagePrettyPrinter(soapMsg);
			
		} catch (SOAPException e) {
			return null;
		}
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

	
}
