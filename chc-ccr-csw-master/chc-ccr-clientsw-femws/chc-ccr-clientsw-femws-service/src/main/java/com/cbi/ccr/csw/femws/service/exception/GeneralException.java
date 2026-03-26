package com.cbi.ccr.csw.femws.service.exception;

@SuppressWarnings("serial")
public class GeneralException extends Exception {

	// EmptyConfigurationException
	//
	public GeneralException(String exceptionMessage) {
		super(exceptionMessage);
	}

	public GeneralException(String exceptionMessage, Exception e) {
		super(exceptionMessage, e);
	}
}
