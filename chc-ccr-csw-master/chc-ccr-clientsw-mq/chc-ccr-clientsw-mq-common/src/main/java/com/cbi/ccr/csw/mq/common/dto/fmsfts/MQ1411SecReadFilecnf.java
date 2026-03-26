package com.cbi.ccr.csw.mq.common.dto.fmsfts;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MQ1411SecReadFilecnf {
	
	public static final String PRIMITIVE_ID = "1411";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String reqBaLoc;
	@NotNull
	private String reqBaRem;
	@NotNull
	private String reqReadType;
	@NotNull
	private Integer reqSyncFlag;
	
	private String reqCorrId;
	
	@NotNull
	private String reqVfn;
	@NotNull
	private String reqQueueFileName;
	@NotNull
	private Integer reqLineSeparatorRcv;
	
	private Integer reqRcvCharType;
	private byte[] reqLocalBaData;
	private String reqRcvBaFileDigestAlg;
	private Integer reqRcvBaFileDigestLen;
	private byte[] reqFiller;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;
	private ZonedDateTime acceptTms;
	private Integer result;
	private Integer rejReason;
	private byte[] filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
