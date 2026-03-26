package com.cbi.ccr.csw.femws.service.exception;

import java.util.Set;

@SuppressWarnings("serial")
public class WrongAttachmentContentIdException extends Exception {
	private static final String FAULT_CODE = "femsws:Client.WrongAttachmentContentIdError";

	public WrongAttachmentContentIdException(Set<String> contentIds) {
		super(String.format("Each of the following contentID(s) references two or more attachments: %s",
				contentIds.toString()));
	}

	public String getTraceEntry() {
		return String.format("%s - %s", getFaultCode(), getMessage());
	}

	public String getFaultCode() {
		return FAULT_CODE;
	}
}