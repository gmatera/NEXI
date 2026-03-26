package com.cbi.ccr.csw.mq.outbound.exception;

import com.cbi.frw.common.Msg;

import lombok.Getter;

public class PrimitiveLengthMismatchException extends Exception{
	private static final long serialVersionUID = 2622034179717936656L;
	
	@Getter
	private final String code;
	
	public PrimitiveLengthMismatchException(String code, String message) {
		super(message);
		this.code = code;
	}
	
	public PrimitiveLengthMismatchException(Enum<?> codeI18n) {
		this(codeI18n, Msg.getMessage(codeI18n.name()));
	}

	public PrimitiveLengthMismatchException(Enum<?> codeI18n, Throwable cause) {
		super(Msg.getMessage(codeI18n.name()), cause);
		this.code = codeI18n.name();
	}
	
	public PrimitiveLengthMismatchException(Enum<?> codeI18n, Throwable cause, Object... params) {
		super(Msg.getMessage(codeI18n.name(), params), cause);
		this.code = codeI18n.name();
	}

	public PrimitiveLengthMismatchException(Enum<?> codeI18n, Object... params) {
		this(codeI18n.name(), Msg.getMessage(codeI18n.name(), params));
		
	}
	
	@Override
	public String toString() {
		return code + " "+getMessage();
	}

}
