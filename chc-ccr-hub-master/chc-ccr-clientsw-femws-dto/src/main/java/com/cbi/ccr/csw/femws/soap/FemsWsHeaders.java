package com.cbi.ccr.csw.femws.soap;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Provides utility methods for FEMS-WS headers.
 */
public final class FemsWsHeaders {

	/** The FEMS-WS header namespace URI. */
	public static final String NS_URI = "urn:sia.eu:femsws:header:v1.0";

	/** The default FEMS-WS prefix. */
	public static final String DEFAULT_PREFIX = "femsws";

	/** The local name of the header element itself. */
	public static final String HEADER_TAG_NAME = "FEMSWSHeader";

	/** The local name of the element storing the client net code. */
	public static final String CLIENT_NET_CODE_TAG_NAME = "ClientNetCode";

	/**
	 * The local name of the element storing the label of the local authentication.
	 */
	public static final String LAU_LABEL_TAG_NAME = "LAULabel";

	/** The local name of the element storing the server net code. */
	public static final String SERVER_NET_CODE_TAG_NAME = "ServerNetCode";

	/** The local name of the element storing the application code. */
	public static final String APPL_CODE_TAG_NAME = "ApplCode";

	/** The local name of the element storing the env code. */
	public static final String ENV_TAG_NAME = "Env";

	/** The local name of the element storing the UDR code. */
	public static final String UDR_TAG_NAME = "UDR";

	/** The local name of the element storing the SUID. */
	public static final String SUID_TAG_NAME = "SUID";

	/** The local name of the element storing ReqST. */
	public static final String REQ_ST = "ReqST";

	/** The local name of the element storing ReqDT. */
	public static final String REQ_DT = "ReqDT";

	/** The local name of the element storing RspDT. */
	public static final String RSP_DT = "RspDT";

	/** The local name of the element storing RspST. */
	public static final String RSP_ST = "RspST";

	private FemsWsHeaders() {
	}

	/**
	 * verifica la correttezza di uno header FEMS-WS
	 * 
	 * @param node
	 *            il nodo in input
	 * @param localName
	 *            il LocalName del nodo da esaminare
	 * @return true se lo header è corretto
	 */
	public static boolean isFemsWsElement(Node node, String localName) {
		if (node instanceof Element) {
			Element elem = (Element) node;
			return NS_URI.equals(elem.getNamespaceURI()) && localName.equals(elem.getLocalName());
		}
		return false;
	}

	/**
	 * costruisce un QName appropriato
	 * 
	 * @param localName
	 *            la stringa che definisce il nome
	 * @return l'oggetto QName costruito correttamente
	 */
	public static QName getQName(String localName) {
		return new QName(NS_URI, localName, DEFAULT_PREFIX);
	}
}
