package com.cbi.ccr.csw.femws.service.exception;

@SuppressWarnings("serial")
public class LocalAuthenticationException extends Exception {
	public LocalAuthenticationException(String exceptionMessage) {
		super(exceptionMessage);
	}

	public LocalAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

}
