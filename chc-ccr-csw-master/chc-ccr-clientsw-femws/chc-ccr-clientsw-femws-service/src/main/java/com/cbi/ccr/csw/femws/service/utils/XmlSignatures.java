package com.cbi.ccr.csw.femws.service.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cbi.ccr.csw.femws.soap.FemsWsHeaders;

/**
 * Provides utility methods for validating XML signatures.
 */
public final class XmlSignatures {

	private XmlSignatures() {
	}

	private static final String WSSE_NS_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

	private static final String XMLDSIG_NS_URI = "http://www.w3.org/2000/09/xmldsig#";

	private static final String SOAP_ENV_PREFIX = "SOAP-ENV";

	private static final String ID_ATTRIBUTE_NAME = "Id";

	private static final String WSSE_SECURITY_TAG_NAME = "Security";

	private static final String WSSE_SECURITY_PREFIX = "wsse";

	private static final String XMLDSIG_PREFIX = "ds";

	private static final String XMLDSIG_SIGNATURE_TAG_NAME = "Signature";

	private static final String XMLDSIG_SIGNED_INFO_TAG_NAME = "SignedInfo";

	private static final String XMLDSIG_SIGNATURE_METHOD_TAG_NAME = "SignatureMethod";

	private static final String XMLDSIG_ALGORITHM_ATTRIBUTE_NAME = "Algorithm";

	private static final String SOAP_ENVELOPE_TAG_NAME = "Envelope";

	private static final String SOAP_HEADER_TAG_NAME = "Header";

	private static final String SOAP_BODY_TAG_NAME = "Body";

	private static final String FEMS_WS_HEADER_TAG_NAME = "FEMSWSHeader";

	private static final String FEMS_WS_HEADER_PREFIX = "femsws";

	private static final String LAU_LABEL_TAG_NAME = "LAULabel";

	private static final String SOAP_11_ACTOR_ATTRIBUTE_NAME = "actor";

	private static final String SOAP_12_ACTOR_ATTRIBUTE_NAME = "role";

	private static final String FEM_WS_ACTOR = "urn:sia.eu:femsws";

	// http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0.pdf
	enum FaultCode {

		UNSUPPORTED_SECURITY_TOKEN, UNSUPPORTED_ALGORITHM, INVALID_SECURITY, INVALID_SECURITY_TOKEN, FAILED_AUTHENTICATION, FAILED_CHECK, SECURITY_TOKEN_UNAVAILABLE;

		boolean matches(SOAPFault fault) {
			if (SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(fault.getNamespaceURI())) {
				return matches(StringUtils.defaultString(fault.getFaultCode()));
			} else {
				for (Iterator<?> i = fault.getFaultSubcodes(); i.hasNext();) {
					QName qName = (QName) i.next();
					if (matches(qName.getPrefix() + ":" + qName.getLocalPart())) {
						return true;
					}
				}
				return false;
			}
		}

		private boolean matches(String code) {
			return code.equals(toCode());
		}

		public String toCode() {
			StringBuffer buffer = new StringBuffer("wsse:");
			for (String token : StringUtils.split(name(), "_")) {
				buffer.append(StringUtils.capitalize(token.toLowerCase()));
			}
			return buffer.toString();
		}

	}

	enum MacAlgorithm {
		// HMAC_SHA1(SignatureMethod.HMAC_SHA1),
		// HMAC_SHA384("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384"),
		// HMAC_SHA512("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512"),
		HMAC_SHA256("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256");

		private String uri;

		MacAlgorithm(String uri) {
			this.uri = uri;
		}

		String getUri() {
			return uri;
		}

		private static MacAlgorithm findAlgorithm(XMLSignature sig) {
			String uri = findAlgorithmUri(sig);
			if (uri != null) {
				for (MacAlgorithm algo : values()) {
					if (algo.uri.equals(uri)) {
						return algo;
					}
				}
			}
			return null;
		}

		private static String findAlgorithmUri(XMLSignature sig) {
			SignedInfo si = sig.getSignedInfo();
			SignatureMethod method = si.getSignatureMethod();
			if (method != null) {
				return method.getAlgorithm();
			}
			return null;
		}

		private static String findAlgorithm(Element signatureElem) throws XMLSignatureException {

			/* locates the SignedInfo element */
			Element signedInfoElem = getChildElementByTagNameNS(signatureElem, XMLDSIG_NS_URI,
					XMLDSIG_SIGNED_INFO_TAG_NAME, null);
			if (signedInfoElem == null) {
				throw new XMLSignatureException(
						"Unable to locate the SignedInfo element as child of the Signature element");
			}

			/* locates the SignatureMethod element */
			Element signatureMethodElem = getChildElementByTagNameNS(signedInfoElem, XMLDSIG_NS_URI,
					XMLDSIG_SIGNATURE_METHOD_TAG_NAME, null);
			if (signatureMethodElem == null) {
				throw new XMLSignatureException(
						"Unable to locate the SignatureMethod element as child of the SignedInfo element");
			}
			return signatureMethodElem.getAttribute(XMLDSIG_ALGORITHM_ATTRIBUTE_NAME);
		}
	}

	enum DigestAlgorithm {

		SHA1(DigestMethod.SHA1), SHA256("http://www.w3.org/2001/04/xmlenc#sha256"), SHA384(
				"http://www.w3.org/2001/04/xmldsig-more#sha384"), SHA512("http://www.w3.org/2001/04/xmlenc#sha512");

		private String uri;

		DigestAlgorithm(String uri) {
			this.uri = uri;
		}

		String getUri() {
			return uri;
		}

		public static String findAlgorithmUri(Reference ref) {
			DigestMethod method = ref.getDigestMethod();
			if (method != null) {
				return method.getAlgorithm();
			}
			return null;
		}

		private static DigestAlgorithm findAlgorithm(Reference ref) {
			String uri = findAlgorithmUri(ref);
			if (uri != null) {
				for (DigestAlgorithm algo : values()) {
					if (algo.uri.equals(uri)) {
						return algo;
					}
				}
			}
			return null;
		}
	}

	/**
	 * Validates the XML signature included in the document.
	 * 
	 * @param doc
	 *            the document to validate.
	 * @param keyBytes
	 *            the key the document must be validated against.
	 * @return {@code true} if the included signature is valid.
	 * @throws XMLSignatureException
	 *             if an error occurs validating the signature.
	 */
	public static void validate(Document doc, byte[] keyBytes, SOAPMessagesUtils soapMessagesUtils) throws XMLSignatureException {
		boolean soap11 = soapMessagesUtils.isSoap11(doc);
		String soapEnvUri = soap11 ? SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE : SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;

		/* locates the SOAP envelope */
		Element envelope = getChildElementByTagNameNS(doc, soapEnvUri, SOAP_ENVELOPE_TAG_NAME, null);
		if (envelope == null) {
			throw new XMLSignatureException("Unable to locate the SOAP envelope");
		}

		/* locates the SOAP header */
		Element soapHeader = getChildElementByTagNameNS(envelope, soapEnvUri, SOAP_HEADER_TAG_NAME, null);
		if (soapHeader == null) {
			throw new XMLSignatureException("Unable to locate the SOAP header");
		}
		String soapHeaderId = StringUtils.defaultString(soapHeader.getAttribute("Id"));
		if (soapHeaderId.equals("")) {
			throw new XMLSignatureException("Unable to locate the Id attribute in SOAP header");
		}

		/* locates the SOAP body */
		Element soapBody = getChildElementByTagNameNS(envelope, soapEnvUri, SOAP_BODY_TAG_NAME, null);
		if (soapBody == null) {
			throw new XMLSignatureException("Unable to locate the SOAP body");
		}
		String soapBodyId = StringUtils.defaultString(soapBody.getAttribute("Id"));
		if (soapBodyId.equals("")) {
			throw new XMLSignatureException("Unable to locate the Id attribute in SOAP body");
		} else if (soapBodyId.equals(soapHeaderId)) {
			throw new XMLSignatureException("Duplicate Id attribute in SOAP body and SOAP header");
		}

		/* locates the WSSE security element */
		ActorPredicate actorPred = soap11 ? ActorPredicate.SOAP_11 : ActorPredicate.SOAP_12;
		Element wsseSecurity = getChildElementByTagNameNS(soapHeader, WSSE_NS_URI, WSSE_SECURITY_TAG_NAME, actorPred);
		if (wsseSecurity == null) {
			throw new XMLSignatureException(String.format(
					"Unable to locate the WSSE Security element with %s='urn:sia.eu:femsws' as child of the SOAP header",
					soap11 ? "actor" : "role"));
		}

		/* locates the Signature element */
		Element signatureElem = getChildElementByTagNameNS(wsseSecurity, XMLDSIG_NS_URI, XMLDSIG_SIGNATURE_TAG_NAME,
				null);
		if (signatureElem == null) {
			throw new XMLSignatureException(
					"Unable to locate the Signature element as child of the WSSE Security element");
		}
//		try {
//			/* prepares the secret key */
//			SecretKeySpec sks = new SecretKeySpec(keyBytes, MacAlgorithm.findAlgorithm(signatureElem));
//
//			DOMValidateContext valContext;
//			/* prepares the validation context */
//			if (signatureElem instanceof ElementImpl) {
//				/* We need to cast the signature element because it appears to be wrapped and the validation method,
//				* when doing the digest of a reference, doesn't manage to find the signature element and remove it
//				* as expected for the embedded signature*/
//				valContext = new DOMValidateContext(sks, ((ElementImpl) signatureElem).getDomElement());
//			}else{
//				valContext = new DOMValidateContext(sks, signatureElem);
//				/* remove the signature element from the xml before validating
//				 * this is to protect against a bug of the transform enveloped-signature
//				 * */
//				wsseSecurity.removeChild(signatureElem);
//			}
//			XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance();
//			XMLSignature sig = sigFactory.unmarshalXMLSignature(valContext);
//
//			/* checks if the Mac algorithm is supported */
//			MacAlgorithm macAlgo = MacAlgorithm.findAlgorithm(sig);
//			if (macAlgo == null) {
//				String algoURI = MacAlgorithm.findAlgorithmUri(sig);
//				throw new XMLSignatureException("Unsupported signature algorithm: " + algoURI);
//			}
//
//			/* checks if the digest algorithms are supported */
//			Set<String> missingIds = new HashSet<>();
//			missingIds.add(soapHeaderId);
//			missingIds.add(soapBodyId);
//			Set<String> unexpectedIds = new HashSet<>();
//			List<?> refs = sig.getSignedInfo().getReferences();
//			for (Object ref1 : refs) {
//				Reference ref = (Reference) ref1;
//				DigestAlgorithm digestAlgo = DigestAlgorithm.findAlgorithm(ref);
//				if (digestAlgo == null) {
//					String algoUri = DigestAlgorithm.findAlgorithmUri(ref);
//					throw new XMLSignatureException("Unsupported digest algorithm:" + algoUri);
//				}
//				String uri = ref.getURI();
//				if (uri != null) {
//					if (uri.startsWith("#")) {
//						uri = uri.substring(1);
//					}
//					if (!missingIds.remove(uri)) {
//						unexpectedIds.add(uri);
//					}
//				}
//			}
//			if (!missingIds.isEmpty()) {
//				throw new XMLSignatureException(
//						"The signature must reference the SOAP body and the SOAP header but the following Ids are missing from it: "
//								+ missingIds);
//			} else if (!unexpectedIds.isEmpty()) {
//				throw new XMLSignatureException(
//						"The signature must reference the SOAP body and the SOAP header but the following unexpected Ids are referenced too: "
//								+ unexpectedIds);
//			}
//
//			/* performs the validation */
//			boolean valid = sig.validate(valContext);
//			if (!valid) {
//				throw new XMLSignatureException("The signature is not valid");
//			}
//		} catch (MarshalException e) {
//			throw new XMLSignatureException(e);
//		}
	}

	private static Element getChildElementByTagNameNS(Node node, String nsUri, String localName, Predicate predicate) {
		NodeList children = node.getChildNodes();
		int n = children.getLength();
		for (int i = 0; i < n; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (Objects.equals(child.getLocalName(), localName)
						&& Objects.equals(child.getNamespaceURI(), nsUri)) {
					if ((predicate == null) || predicate.evaluate(child)) {
						return (Element) child;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Removes the {@code LAULabel} and {@code wsse:Security} elements from the SOAP
	 * message.
	 * 
	 * @param soapMsg
	 *            the SOAP message.
	 * @throws SOAPException
	 *             in an error occurs removing the elements.
	 */
	public static void removeSecurityElements(SOAPMessage soapMsg, SOAPMessagesUtils soapMessagesUtils) throws SOAPException {

		/* locates the SOAP header */
		SOAPHeader header = soapMsg.getSOAPPart().getEnvelope().getHeader();

		/* removes the LAU label element */
		SOAPElement femsWsHeader = soapMessagesUtils.getChildElementByTagNameNS(header, FemsWsHeaders.NS_URI,
				FEMS_WS_HEADER_TAG_NAME, null);
		SOAPElement lauLabelElem = soapMessagesUtils.getChildElementByTagNameNS(femsWsHeader, FemsWsHeaders.NS_URI,
				LAU_LABEL_TAG_NAME, null);
		femsWsHeader.removeChild(lauLabelElem);

		/* removes the WSSE Security element */
		ActorPredicate actorPred = soapMessagesUtils.isSoap11(header) ? ActorPredicate.SOAP_11 : ActorPredicate.SOAP_12;
		SOAPElement wsseSecurityElem = soapMessagesUtils.getChildElementByTagNameNS(header, WSSE_NS_URI,
				WSSE_SECURITY_TAG_NAME, actorPred);
		header.removeChild(wsseSecurityElem);

		/* saves the message */
		soapMsg.saveChanges();
	}

	public static void addSecurityElements(SOAPMessage soapMsg, String lauLabel, byte[] keyBytes, SOAPMessagesUtils soapMessagesUtils) throws SOAPException,
			NoSuchAlgorithmException, InvalidAlgorithmParameterException, MarshalException, XMLSignatureException {

		/* locates the SOAP header */
		SOAPHeader header = soapMsg.getSOAPPart().getEnvelope().getHeader();
		String soapEnvUri = soapMessagesUtils.isSoap11(header) ? SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE
				: SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
		String soapActorAttrName = soapMessagesUtils.isSoap11(header) ? SOAP_11_ACTOR_ATTRIBUTE_NAME
				: SOAP_12_ACTOR_ATTRIBUTE_NAME;

		/* locates the FemsWS header */
		SOAPElement femsWsHeader = soapMessagesUtils.getChildElementByTagNameNS(header, FemsWsHeaders.NS_URI,
				FEMS_WS_HEADER_TAG_NAME, null);

		/* adds the LAU label element */
		SOAPElement lauLabelElem = femsWsHeader.addChildElement(LAU_LABEL_TAG_NAME, FEMS_WS_HEADER_PREFIX,
				FemsWsHeaders.NS_URI);
		lauLabelElem.setTextContent(lauLabel);

		/* adds the WSSE security element */
		SOAPElement wsseSecurityElem = header.addChildElement(WSSE_SECURITY_TAG_NAME, WSSE_SECURITY_PREFIX,
				WSSE_NS_URI);
		wsseSecurityElem.addAttribute(
				soapMsg.getSOAPPart().getEnvelope().createName(soapActorAttrName, SOAP_ENV_PREFIX, soapEnvUri),
				FEM_WS_ACTOR);
		wsseSecurityElem.addAttribute(
				soapMsg.getSOAPPart().getEnvelope().createName("mustUnderstand", SOAP_ENV_PREFIX, soapEnvUri), "1");

		/* adds the {@code Id} attribute to the SOAP body */
		Name idAttrName = soapMsg.getSOAPPart().getEnvelope().createName(ID_ATTRIBUTE_NAME);
		SOAPBody body = soapMsg.getSOAPBody();
		String bodyId = body.getAttributeValue(idAttrName);
		if (StringUtils.isEmpty(bodyId)) {
			bodyId = "femsWsSoapBody";
			body.addAttribute(idAttrName, bodyId);
		}

		/* adds the {@code Id} attribute to the SOAP header */
		String headerId = header.getAttributeValue(idAttrName);
		if (StringUtils.isEmpty(headerId)) {
			headerId = "femsWsSoapHeader";
			header.addAttribute(idAttrName, headerId);
		}

		/* adds the signature */
		SecretKeySpec sks = new SecretKeySpec(keyBytes, MacAlgorithm.HMAC_SHA256.getUri());
		XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance();
		Reference bodyRef = sigFactory.newReference("#" + bodyId, sigFactory.newDigestMethod(DigestMethod.SHA256, null),
				Collections.singletonList(sigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
				null, null);
		Reference headerRef = sigFactory.newReference("#" + headerId,
				sigFactory.newDigestMethod(DigestMethod.SHA256, null),
				Collections.singletonList(sigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
				null, null);
		SignedInfo signedInfo = sigFactory.newSignedInfo(
				sigFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
				sigFactory.newSignatureMethod(MacAlgorithm.HMAC_SHA256.getUri(), null),
				Arrays.asList(bodyRef, headerRef));
		XMLSignature sig = sigFactory.newXMLSignature(signedInfo, null);
		DOMSignContext sigContext = new DOMSignContext(sks, wsseSecurityElem);
		sigContext.putNamespacePrefix(XMLSignature.XMLNS, XMLDSIG_PREFIX);
		sigContext.setIdAttributeNS(body, null, ID_ATTRIBUTE_NAME);
		sigContext.setIdAttributeNS(header, null, ID_ATTRIBUTE_NAME);
		sig.sign(sigContext);

		/* saves the message */
		soapMsg.saveChanges();
	}

	public static boolean isWsseSecurityFault(SOAPFault fault) throws SOAPException {
		return getWsseSecurityFaultCode(fault) != null;
	}

	public static String getWsseSecurityFaultCode(SOAPFault fault) {
		for (FaultCode code : FaultCode.values()) {
			if (code.matches(fault)) {
				return code.toCode();
			}
		}
		return null;
	}

	private enum ActorPredicate implements Predicate {

		SOAP_11(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, SOAP_11_ACTOR_ATTRIBUTE_NAME),

		SOAP_12(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, SOAP_12_ACTOR_ATTRIBUTE_NAME);

		private final String soapEnvUri;

		private final String soapActorAttrName;

		ActorPredicate(String soapEnvUri, String soapActorAttrName) {
			this.soapEnvUri = soapEnvUri;
			this.soapActorAttrName = soapActorAttrName;
		}

		@Override
		public boolean evaluate(Object obj) {
			if (obj instanceof Element) {
				Element elem = (Element) obj;
				return FEM_WS_ACTOR.equals(elem.getAttributeNS(soapEnvUri, soapActorAttrName));
			}
			return false;
		}

	}
}
