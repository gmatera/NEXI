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
public class MQ1982SecMsgStatuscnf {

	public static final String PRIMITIVE_ID = "1982";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String reqBaLoc;
	@NotNull
	private String reqBaRem;
	@NotNull
	private Integer reqPriority;

	private String reqMsgId;
	private Integer reqUdrLen;
	private String reqUdr;
	private String reqCorrId;
	private Integer reqWarnTime;
	private byte[] reqFiller;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;

	@NotNull
	private Integer result;

	private Integer rejReason;
	private String warnFlag;
	private String msgId;
	private byte[] fillerOne;
	private Integer udrLen;
	private String udr;
	private String corrId;
	private String tur;
	private String msgType;
	private String catAppl;
	private String aggrI;
	private String aggrII;
	private Integer extraDataLen;
	private String extraData;
	private String operStatus;
	private Integer detailedStatus;
	private ZonedDateTime acceptTms;
	private ZonedDateTime hostFirstSubTms;
	private ZonedDateTime hostSubTms;
	private ZonedDateTime ferSubTms;
	private ZonedDateTime fenSubTms;
	private ZonedDateTime fenDlvTms;
	private ZonedDateTime ferDlvTms;
	private ZonedDateTime hostFirstDlvTms;
	private ZonedDateTime completeTms;
	private byte[] fillerTwo;
	private String mabDigestAlg;
	private Integer mabDigestLen;
	private String mabDigest;
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
	private String rcvCertificateLabel;
	private String rcvSignAlgo;
	private Integer rcvSignLen;
	private ZonedDateTime rcvSignTms;
	private String rcvSignFemsId;
	private String rcvSignServerId;
	private String rcvSignCertSubject;
	private String rcvSignResult;
	private Integer checkRcvSignResult;
	private byte[] rcvSign;
	private byte[] fillerThree;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
}
