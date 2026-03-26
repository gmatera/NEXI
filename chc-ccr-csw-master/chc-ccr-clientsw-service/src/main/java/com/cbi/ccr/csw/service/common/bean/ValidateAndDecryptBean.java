package com.cbi.ccr.csw.service.common.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
public class ValidateAndDecryptBean {

	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
