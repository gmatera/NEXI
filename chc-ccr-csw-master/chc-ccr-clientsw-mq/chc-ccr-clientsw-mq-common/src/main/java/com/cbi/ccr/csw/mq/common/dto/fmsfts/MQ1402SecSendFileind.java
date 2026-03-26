package com.cbi.ccr.csw.mq.common.dto.fmsfts;

import java.time.ZonedDateTime;

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
public class MQ1402SecSendFileind extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1402";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	@NotNull
	private Integer syncFlag;
	
	private String corrId;
	
	@NotNull
	private String vfn;
	
	private Integer adfLen;
	private String adf;
	private Integer udrLen;
	private String udr;
	private String tur;
	private byte[] locBaData;
	private Integer extraDataLen;
	private String extraData;
	private Integer result;
	private Integer rejReason;
	private String negDetailedOper;
	private String transferId;
	private Integer lineSeparator;
	private Integer recType;
	private Integer maxRecLen;
	private Integer baFileSize;
	private Integer charType;
	private String compressAlgo;
	private Integer netFileSize;
	
	@NotNull
	private ZonedDateTime acceptTms;
	
	private ZonedDateTime startCreateTms;
	private ZonedDateTime endCreateTms;
	private ZonedDateTime errorTms;
	private ZonedDateTime hostFirstSubTms;
	private ZonedDateTime hostSubTms;
	private ZonedDateTime hostLastSubTms;
	private ZonedDateTime ferFirstSubTms;
	private ZonedDateTime fenFirstSubTms;
	private ZonedDateTime fenFirstDlvTms;
	private ZonedDateTime ferFirstDlvTms;
	private ZonedDateTime ferLastSubTms;
	private ZonedDateTime fenLastSubTms;
	private ZonedDateTime fenLastDlvTms;
	private ZonedDateTime ferLastDlvTms;
	private ZonedDateTime hostFirstDlvTms;
	private ZonedDateTime fermsSubTms;
	private ZonedDateTime fenmsSubTms;
	private ZonedDateTime fenmsDlvTms;
	private ZonedDateTime fermsDlvTms;
	private ZonedDateTime firstAckMSTms;
	private ZonedDateTime completeTms;
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
	private String fileApplDataDigestAlg;
	private Integer fileApplDataDigestLen;
	private String fileApplDataDigest;
	private String netFileDigestAlg;
	private Integer netFileDigestLen;
	private String netFileDigest;
	private String mabDigestAlg;
	private Integer mabDigestLen;
	private String mabDigest;
	private byte[] filler;
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
