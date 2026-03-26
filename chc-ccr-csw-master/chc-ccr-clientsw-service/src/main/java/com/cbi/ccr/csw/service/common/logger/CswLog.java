package com.cbi.ccr.csw.service.common.logger;

import org.slf4j.Logger;

import com.cbi.frw.common.logging.Log;

public class CswLog {

	private CswLog() {
		throw new IllegalAccessError();
	}
	
	public static void setLogData(CswLogData rd) {
		Log.setLogData(rd);
	}

	public static CswLogData getLogData() {
		if (Log.getLogData() == null)
			setLogData(new CswLogData());

		return (CswLogData) Log.getLogData();
	}

	public static void uset() {
		Log.uset();
	}
	
	
	public static void info(Logger logger, String message) {
		getLogData().setMessage(message);
		Log.info(logger, getLogData());
	}
		
	public static void debug(Logger logger, String message) {
		getLogData().setMessage(message);
		Log.debug(logger, getLogData());
	}
	
	public static void error(Logger logger, Exception exception) {
		Log.error(logger, getLogData(), exception);
	}
	
	public static void error(Logger logger, String message) {
		getLogData().setMessage(message);
		Log.error(logger, getLogData());
	}
	
}
