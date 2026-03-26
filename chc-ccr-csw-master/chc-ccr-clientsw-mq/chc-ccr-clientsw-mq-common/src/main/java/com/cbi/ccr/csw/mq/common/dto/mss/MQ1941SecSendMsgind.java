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
@NoArgsConstructor
@AllArgsConstructor
public class MQ1941SecSendMsgind extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1941";
	@NotNull
	private String id = PRIMITIVE_ID;
	
	private String baLoc;
	private String baRem;
	private Integer priority;
	private String tur;
	private String msgType;
	private String catAppl;
	private String corrId;
	private Integer udrLen;
	private String udr;
	private Integer extraDataLen;
	private String extraData;
	private byte[] filler1;
	private Integer result;
	private Integer rejReason;
	private String aggrIid;
	private String aggrIIid;
	private Integer msgId;
	
	private ZonedDateTime acceptTms;
	private ZonedDateTime hostFirstsubTms;
	private ZonedDateTime hostSubTms;
	private ZonedDateTime ferSubTms;
	private ZonedDateTime fenSubTms;
	private ZonedDateTime fenDlvTms;
	private ZonedDateTime ferDlvTms;
	private ZonedDateTime hostFirstDlvTms;
	private ZonedDateTime completeTms;

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
	private String sndSign;
	private String rcvCertificateLabel;
	private String rcvSignAlgo;
	private Integer rcvSignLen;
	private LocalDateTime rcvSignTms;
	private String rcvSignFemsId;
	private String rcvSignServerId;
	private String rcvSignCertSubject;
	private Integer rcvSignResult;
	private Integer checkRcvSignResult;
	private String rcvSign;
	private String mabDigestAlg;
	private Long mabDigestLen;
	private String mabDigest;
	private byte[] filler2;
	private String localAuthInfoAlg;
	private Long localAuthInfoLen;
	private String localAuthInfo;
	
}
