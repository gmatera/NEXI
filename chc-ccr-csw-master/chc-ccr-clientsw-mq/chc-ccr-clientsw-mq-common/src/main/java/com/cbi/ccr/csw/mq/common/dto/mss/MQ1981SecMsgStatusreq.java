package com.cbi.ccr.csw.mq.common.dto.mss;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MQ1981SecMsgStatusreq {
	
	public static final String PRIMITIVE_ID = "1981";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	@NotNull
	private Integer priority;
	
	private String msgId;
	private Integer udrLen;
	private String udr;
	private String corrId;
	private Integer warnTime;
	private byte[] filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
