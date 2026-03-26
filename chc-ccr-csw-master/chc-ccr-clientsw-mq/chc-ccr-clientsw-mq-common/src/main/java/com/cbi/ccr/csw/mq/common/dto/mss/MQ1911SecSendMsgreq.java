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
public class MQ1911SecSendMsgreq extends PrimitiveMQ{
	
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1911";
	
	private String id = PRIMITIVE_ID;
	
	private byte[] filler1;
	private byte[] locBaData;
	
	
	private String baLoc;
	
	private String baRem;
	
	private Integer priority;
	
	private String tur;
	
	private LocalDateTime baReqTms;
	
	
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
	
	
	private Integer mabLen;
	
	private String mab;

	private String localAuthInfo;
	
}
