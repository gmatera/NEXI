package com.cbi.ccr.csw.femws.service.utils;

import java.util.Map;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cbi.ccr.csw.femws.service.FemsConfiguration;

/**
 * Utility class per esporre il contenuto di un messaggio
 * 
 * @author TFSCostabileMichele
 *
 */
@Component
public class MessageUtils {
	private static final Logger logger = LoggerFactory.getLogger(MessageUtils.class);

	@Autowired
	private FemsConfiguration configuration;
	

	public String hideBodyTrace(String full) {
		if (Boolean.FALSE.equals(configuration.getTraceBody())) {
			full = full.substring(0, Math.min(2048, full.length()));
			if (full != null && full.toLowerCase().contains("body>") && !full.contains(":Fault>")) {
				int first = full.toLowerCase().indexOf("body>");
				return full.substring(0, first + 5) + " ...";
			}
		}
		return full;
	}
	/**
	 * Stampa nel log il contenuto degli header HTTP di un messaggio
	 * 
	 * @param headers
	 *            array di header http
	 */
	public void printHeaders(Map<String, String> headers) {
		if (headers.size() == 0) {
			return;
		}
		logger.debug("=== HEADERS ===");
		for (String header : headers.keySet()) {
			logger.debug("{} : {}", header, headers.get(header));
		}
		logger.debug("=== /HEADERS ===\r\n");

	}
	public void printHeaders(Header[] headers) {
		if (headers.length == 0) {
			return;
		}
		logger.debug("=== HEADERS ===");
		for (Header header : headers) {
			logger.debug(header.getName() + ": " + header.getValue());
		}
		logger.debug("=== /HEADERS ===\r\n");
	}
}