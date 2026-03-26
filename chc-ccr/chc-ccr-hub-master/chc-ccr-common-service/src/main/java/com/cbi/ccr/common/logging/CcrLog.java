package com.cbi.ccr.common.logging;

import org.slf4j.Logger;

import com.cbi.frw.common.logging.Log;
import com.cbi.frw.common.logging.LogLevel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CcrLog {
	
	private CcrLog() {
		throw new IllegalAccessError();
	}
	
	static ObjectMapper objectMapper = new ObjectMapper();
	
	private static final InheritableThreadLocal<CcrLogData> THREAD_LOCAL = new InheritableThreadLocal<>();

	public static void setLogData(CcrLogData rd) {
		THREAD_LOCAL.set(rd);
	}

	public static CcrLogData getLogData() {
		if (THREAD_LOCAL.get() == null)
			setLogData(new CcrLogData());

		return THREAD_LOCAL.get();
	}

	public static void uset() {
		THREAD_LOCAL.remove();
	}
	
	public static void info(Logger logger) {
		Log.info(logger, getLogData());
		
	}
	
	public static void debug(Logger logger) {
		Log.debug(logger, getLogData());
		
	}
	
	public static void error(Logger logger, Exception exception) {
		Log.error(logger, getLogData(), exception);
		
	}
	public static void error(Logger logger) {
		Log.error(logger, getLogData());
		
	}

}
