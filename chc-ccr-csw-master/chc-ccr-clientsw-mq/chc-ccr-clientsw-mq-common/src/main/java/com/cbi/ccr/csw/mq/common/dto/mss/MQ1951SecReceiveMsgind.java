package com.cbi.ccr.csw.mq.common.dto.mss;

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
public class MQ1951SecReceiveMsgind {

	public static final String PRIMITIVE_ID = "1951";
	@NotNull
	private String id = PRIMITIVE_ID;
	
	private String baLoc;
	private String baRem;
	private Integer priority;
	private Integer msgId;
	private Integer msgIdR;
	private Integer udrLen;
	private String udr;
	private String tur;
	private String msgType;
	private String catAppl;
	private String aggrI;
	private String aggrII;
	
	private ZonedDateTime hostFirstSubTms;
	private ZonedDateTime ferSubTms;
	private ZonedDateTime fenSubTms;
	private ZonedDateTime fenDlvTms;
	private ZonedDateTime ferDlvTms;
	private ZonedDateTime hostFirstDelTms;
	private ZonedDateTime firstBADlvTms;
	private Integer extraDataLen;
	private String extraData;
	private Integer signExist;
	private String signatureData;
	private String sndCertificateLabel;
	private String sndSignAlgo;
	private Integer sndSignLen;
	private ZonedDateTime sndSignTms;
	private String sndSignFemsId;
	private String sndSignServerId;
	private String sndSignCertSubject;
	private Integer sndSignResult;
	private Integer checkSndSignResult;
	private byte[] sndSign;
	private byte[] fillerOne;
	private String mabDigestAlg;
	private Integer mabDigestLen;
	private String mabDigest;
	private byte[] fillerTwo;
	private Integer mabLen;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String mab;
	private String localAuthInfo;

	
	
	
}
