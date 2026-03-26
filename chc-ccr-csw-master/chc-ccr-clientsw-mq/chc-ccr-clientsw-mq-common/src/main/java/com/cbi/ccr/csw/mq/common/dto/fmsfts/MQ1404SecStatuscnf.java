package com.cbi.ccr.csw.mq.common.dto.fmsfts;

import java.time.LocalDateTime;
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
public class MQ1404SecStatuscnf {
	
	public static final String PRIMITIVE_ID = "1404";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String reqBaLoc;
	@NotNull
	private String reqBaRem;
	@NotNull
	private Integer reqSyncFlag;
	
	private Integer reqUdrLen;
	private String reqUdr;
	private String reqCorrId;
	private Integer reqWarnTime;
	private String reqVfn;
	private byte[] reqFiller;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;
	private Integer result;
	private String warnFlag;
	private Integer udrLen;
	private String udr;
	private String corrId;
	private String vfn;
	private Integer adfLen;
	private String adf;
	private String tur;
	private String msgType;
	private String catAppl;
	private Integer extraDataLen;
	private String extraData;
	private String operStatus;
	private Integer detailedStatus;
	private String transferId;
	
	private ZonedDateTime acceptTms;
	private ZonedDateTime startCreateTms;
	private ZonedDateTime endCreateTms;
	private ZonedDateTime errorTms;
	private ZonedDateTime hostFirstSubTms;
	private ZonedDateTime hostSubTms;
	private ZonedDateTime lastBHostSubTms;
	private ZonedDateTime ferFirstBSubTms;
	private ZonedDateTime fenFirstBSubTms;
	private ZonedDateTime fenFirstBDlvTms;
	private ZonedDateTime ferFirstBDlvTms;
	private ZonedDateTime ferLastSubTms;
	private ZonedDateTime fenLastBSubTms;
	private ZonedDateTime fenLastBDlvTms;
	private ZonedDateTime ferLastDlvTms;
	private ZonedDateTime hostFirstDlvTms;
	private byte[] fillerOne;
	private ZonedDateTime fermsSubTms;
	private ZonedDateTime fenmsSubTms;
	private ZonedDateTime fenmsDlvTms;
	private ZonedDateTime fermsDlvTms;
	private ZonedDateTime firstAckMSTms;
	private ZonedDateTime completeTms;
	private byte[] fillerTwo;
	private String fileApplDataDigestAlg;
	private Integer fileApplDataDigestLen;
	private String fileApplDataDigest;
	private byte[] fillerThree;
	private String netFileDigestAlg;
	private Integer netFileDigestLen;
	private String netFileDigest;
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
	private Integer rcvSignResult;
	private Integer checkRcvSignResult;
	private byte[] rcvSign;
	private byte[] fillerFour;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
