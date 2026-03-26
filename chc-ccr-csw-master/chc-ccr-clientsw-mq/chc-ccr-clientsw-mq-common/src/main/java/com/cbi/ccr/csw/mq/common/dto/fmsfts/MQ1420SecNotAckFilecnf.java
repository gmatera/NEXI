package com.cbi.ccr.csw.mq.common.dto.fmsfts;

import java.time.LocalDateTime;
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
public class MQ1420SecNotAckFilecnf {
	
	public static final String PRIMITIVE_ID = "1420";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String reqBaLoc;
	@NotNull
	private String reqBaRem;
	@NotNull
	private Integer reqSyncFlag;
	
	private Integer reqUdrLen;
	private String reqUdr;
	private String reqVfn;
	private LocalDateTime reqBaProcessTms;
	private byte[] reqFiller;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;
	
	@NotNull
	private Integer result;
	
	private Integer rejReason;
	private ZonedDateTime siStdProcessTms;
	private byte[] filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
	
}
