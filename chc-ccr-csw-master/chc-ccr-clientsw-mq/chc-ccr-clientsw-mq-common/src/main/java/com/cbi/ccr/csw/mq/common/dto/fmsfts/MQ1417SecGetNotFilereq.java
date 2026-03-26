package com.cbi.ccr.csw.mq.common.dto.fmsfts;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MQ1417SecGetNotFilereq {
	
	public static final String PRIMITIVE_ID = "1417";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;

	private String baRem;
	
	@NotNull
	private Integer  getNotType;
	@NotNull
	private Integer syncFlag;

	private String keyBaLoc;
	private String keyBaRem;
	private String vfn;
	// private String filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
