package com.cbi.ccr.csw.mq.common.dto.mss;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MQ1962SecGetNotMsgcnf {

	public static final String PRIMITIVE_ID = "1962";
	@NotNull
	private String id = PRIMITIVE_ID;
	
	private String reqBaLoc;
	private String reqBaRem;
	private Integer reqpriority;
	private Integer reqGetNotType;
	private String reqKeyBaLoc;
	private String reqKeyBARem;
	private String reqKeyMsgId;
	private Integer reqNumElemOut;
	private byte[] reqFiller;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;
	private Integer result;
	private Integer rejReason;
	private Integer numElemOutOut;
	private List<NumElemOutOut> elements;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
	
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public class NumElemOutOut {
		private String baLoc;
		private String baRem;
		private Integer priority;
		private String msgId;
		private String udrLen;
		private String udr;
		private String tur;
		private ZonedDateTime completeTms;
		private byte[] filler;



	}
}
