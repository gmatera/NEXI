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
public class MQ1971SecGetMsgreq {
	
	public static final String PRIMITIVE_ID = "1971";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;
	private String baRem;
	private Integer priority;
	private Integer getMsgType;
	private String keyBALoc;
	private String keyBARem;
	private String keyMsgIdR;
	private Integer numElemOut;
	private byte[] filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
