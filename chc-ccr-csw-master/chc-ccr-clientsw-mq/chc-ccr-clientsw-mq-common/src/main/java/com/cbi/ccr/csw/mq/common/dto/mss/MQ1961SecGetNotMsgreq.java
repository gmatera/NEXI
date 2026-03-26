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
public class MQ1961SecGetNotMsgreq {
	public static final String PRIMITIVE_ID = "1961";
	@NotNull
	private String id = PRIMITIVE_ID;
	
	private String baLoc;
	private String baRem;
	private Integer priority;
	private Integer getNotType;
	private String keyBaLoc;
	private String keyBaRem;
	private String keyMsgId;
	private Integer numeElemOut;
	private String filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
