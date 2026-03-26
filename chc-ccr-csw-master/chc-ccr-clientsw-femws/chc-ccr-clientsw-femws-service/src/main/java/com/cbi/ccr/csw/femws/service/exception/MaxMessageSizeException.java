package com.cbi.ccr.csw.femws.service.exception;

@SuppressWarnings("serial")
public class MaxMessageSizeException extends Exception {
	public MaxMessageSizeException(String exceptionMessage) {
		super(exceptionMessage);
	}
}
