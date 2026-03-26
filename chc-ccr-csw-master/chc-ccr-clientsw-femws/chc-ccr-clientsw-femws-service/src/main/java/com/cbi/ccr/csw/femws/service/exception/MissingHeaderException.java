package com.cbi.ccr.csw.femws.service.exception;

@SuppressWarnings("serial")
public class MissingHeaderException extends Exception {
	public MissingHeaderException(String exceptionMessage) {
		super(exceptionMessage);
	}
}