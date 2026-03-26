package com.cbi.ccr.csw.domain.csw.config;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ConfigurationFMSFTSCommon extends ConfigurationFMSFTSMSSCommon{

	
	@Enumerated(EnumType.STRING)
	@Column(name = "SND_CODE_PAGE")
	private CodePage sndCodePage = CodePage.BINARY;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SND_LINE_SEPARATOR")
	private LineSeparator sndLineSeparator = LineSeparator.NONE;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "HUB_CODE_PAGE")
	private CodePage hubCodePage = CodePage.BINARY;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "HUB_LINE_SEPARATOR")
	private LineSeparator hubLineSeparator = LineSeparator.CRLF_0X0D0A;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SND_RECORD_FORMAT")
	private RecordFormat sndRecordFormat = RecordFormat.VARIABLE;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "RCV_RECORD_FORMAT")
	private RecordFormat rcvRecordFormat = RecordFormat.VARIABLE;


	/*
	 * Valid values are from 1 to 32760 (for fixed) or 32752(for variable).
	 */
	@Column(name = "SND_MAX_REC_LENGHT")
	private Integer sndMaxRecLength;
	
	@Column(name = "RCV_MAX_REC_LENGHT")
	private Integer rcvMaxRecLength;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "RCV_CODE_PAGE")
	private CodePage rcvCodePage = CodePage.BINARY;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "RCV_LINE_SEPARATOR")
	private LineSeparator rcvLineSeparator = LineSeparator.NONE;
	
	
	@Column(name = "RCV_PATH")
	private String rcvPath;
	
//	//only db
//	@Column(name = "RCV_DSN_PREFIX")
//	@Size(max = 44)
//	private String rcvDsnPrefix;
//	
//	@Column(name = "RCV_DSN_CREATION_ALGO")
////	@Enumerated(EnumType.ORDINAL)
//	private DSNCreationAlgo rcvDnsCreationAlgo = DSNCreationAlgo.DSN_MINUS_1;
	
}
