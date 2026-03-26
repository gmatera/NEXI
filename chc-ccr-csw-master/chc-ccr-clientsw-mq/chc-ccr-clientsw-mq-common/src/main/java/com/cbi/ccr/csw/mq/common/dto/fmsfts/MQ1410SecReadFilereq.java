package com.cbi.ccr.csw.mq.common.dto.fmsfts;

import javax.validation.constraints.NotNull;

import com.cbi.ccr.csw.mq.common.dto.PrimitiveMQ;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MQ1410SecReadFilereq extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1410";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	@NotNull
	private String readType;
	@NotNull
	private Integer syncFlag;
	
	private String corrId;
	
	@NotNull
	private String vfn;
	@NotNull
	private String queueFileName;
	@NotNull
	private Integer lineSeparator;
	@NotNull
	private Integer rcvCharType;
	
	private byte[] localBaData;
	private String rcvBaFileDigestAlg;
	private Integer rcvBaFileDigestLen;
	private byte[] filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
