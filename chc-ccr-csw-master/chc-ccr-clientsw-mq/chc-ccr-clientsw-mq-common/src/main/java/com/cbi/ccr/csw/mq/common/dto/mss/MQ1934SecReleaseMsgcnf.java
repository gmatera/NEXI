package com.cbi.ccr.csw.mq.common.dto.mss;

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
public class MQ1934SecReleaseMsgcnf {
	
	public static final String PRIMITIVE_ID = "1934";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String reqBaLoc;
	@NotNull
	private String reqBaRem;
	@NotNull
	private Integer reqPriority;
	
	private String reqMsgIdR;
	private Integer reqUdrLen;
	private String reqUdr;
	private LocalDateTime reqBaProcessTms;
	private byte[] fillerOne;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;

	private String reqLocalAuthInfo;
	
	@NotNull
	private Integer result;
	
	private Integer rejReason;
	private ZonedDateTime siStdProcessTms;
	private byte[] fillerTwo;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
