package com.cbi.ccr.csw.mq.common.dto.mss;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import com.cbi.ccr.csw.mq.common.dto.PrimitiveMQ;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MQ1921SecSendMsgcnf extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1921";
	@NotNull
	private String id = PRIMITIVE_ID;
	
	private ZonedDateTime acceptTms;
	
	@NotNull
	private Integer result;
	private Integer rejReason;
	private String aggrI;
	private String aggrII;
	private Integer msgId;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;
	
	private byte[] filler1;
	private byte[] locBaData;
	
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	@NotNull
	private Integer priority;
	
	private String tur;
	
	private LocalDateTime baReqTms;
	
	@NotNull
	private String msgType;
	
	private String catAppl;
	private String corrId;
	private Integer udrLen;
	private String udr;
	private Integer extraDataLen;
	private String extraData;
	private String mabDigestAlg;
	private Integer mabDigestLen;
	private String mabDigest;
	private byte[] filler2;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	
	@NotNull
	private Integer mabLen;
	@NotNull
	private String mab;

	private String localAuthInfo;
	
}
