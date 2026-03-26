package com.cbi.ccr.csw.femws.service.exception;

import com.cbi.ccr.csw.femws.service.dto.FemsSession;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class FemwsException extends Exception {
	
	@NonNull
	private final String payload;
	@NonNull
	private final transient FemsSession femsSession; 
	@NonNull
	private final String faultCode;
	@NonNull
	private final String traceEntry;
	
}
