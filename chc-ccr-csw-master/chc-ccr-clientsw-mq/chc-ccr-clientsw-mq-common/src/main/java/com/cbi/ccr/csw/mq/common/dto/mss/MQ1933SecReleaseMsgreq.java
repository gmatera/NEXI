package com.cbi.ccr.csw.mq.common.dto.mss;

import java.time.LocalDateTime;

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
public class MQ1933SecReleaseMsgreq extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1933";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	@NotNull
	private Integer priority;
	
	private String msgIdR;
	private Integer udrLen;
	private String udr;
	private LocalDateTime baProcessTms;
	private byte[] filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
}
