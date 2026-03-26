package com.cbi.ccr.csw.mq.common.dto.fmsfts;

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
@NoArgsConstructor
@AllArgsConstructor
public class MQ1412SecReadFileind extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1412";
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
	
	private String aggrI;
	private String aggrII;
	private Integer adfLen;
	private String adf;
	private Integer result;
	private Integer rejReason;
	
	@NotNull
	private String queueFileName;
	@NotNull
	private byte[] groupId;
	@NotNull
	private Integer lineSeparatorRcv;
	@NotNull
	private Integer recType;
	@NotNull
	private Integer maxRecLen;
	private byte[] fillerOne;
	@NotNull
	private Long baFileSize;
	@NotNull
	private Integer sndCharType;
	@NotNull
	private Integer rcvCharType;
	
	private String compressAlgo;
	
	@NotNull
	private Long netFileSize;
	@NotNull
	private String transferId;
	@NotNull
	private ZonedDateTime hostFirstSubTms;
	@NotNull
	private ZonedDateTime ferFirstBSubTms;
	@NotNull
	private ZonedDateTime fenFirstBSubTms;
	@NotNull
	private ZonedDateTime fenFirstBDlvTms;
	@NotNull
	private ZonedDateTime ferFirstBDlvTms;
	@NotNull
	private ZonedDateTime ferLastBSubTms; 
	@NotNull
	private ZonedDateTime fenLastBSubTms;
	@NotNull
	private ZonedDateTime fenLastBDlvTms;
	@NotNull
	private ZonedDateTime ferLastBDlvTms;
	@NotNull
	private ZonedDateTime hostFirstDlvTms;
	@NotNull
	private ZonedDateTime ftsEndReceiveTms;
	@NotNull
	private ZonedDateTime acceptTms;
	@NotNull
	private ZonedDateTime startCreateTms;
	@NotNull
	private ZonedDateTime endCreateTms;
	private byte[] fillerTwo;
	
	private byte[] localBaData;
	private Integer extraDataLen;
	private String extraData;
	private Integer signExist;
	private String signatureData;
	private String sndCertificateLabel;
	private String sndSignAlgo;
	private Integer sndSignLen;
	private LocalDateTime sndSignTms;
	private String sndSignFemsId;
	private String sndSignServerId;
	private String sndSignCertSubject;
	private Integer sndSignResult;
	private Integer checkSndSignResult;
	private byte[] sndSign;
	private byte[] fillerThree;
	private String fileApplDataDigestAlg;
	private Long fileApplDataDigestLen;
	private String fileApplDataDigest;
	private String rcvBaFileDigestAlg;
	private Long rcvBaFileDigestLen;
	
	@NotNull
	private String rcvBaFileDigest;
	
	private String netFileDigestAlg;
	private Long netFileDigestLen;
	private String netFileDigest;
	private byte[] fillerFour;
	private String localAuthInfoAlg;
	private Long localAuthInfoLen;
	private String localAuthInfo;
	
}
