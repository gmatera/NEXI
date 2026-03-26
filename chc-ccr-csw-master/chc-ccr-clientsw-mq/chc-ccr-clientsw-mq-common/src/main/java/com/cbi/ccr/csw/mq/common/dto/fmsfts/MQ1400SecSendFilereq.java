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
public class MQ1400SecSendFilereq extends PrimitiveMQ {

	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1400";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	
	private Integer sendType;
	
	@NotNull
	private Integer syncFlag;
	
	private String corrId;
	private String vfn;
	
	private Integer baFileSize;
	@NotNull
	private String queueFileName;
	
	private byte[] groupId;
	@NotNull
	private Integer lineSeparator;
	@NotNull
	private Integer recType;
	@NotNull
	private Integer maxRecLen;
	@NotNull
	private Integer charType;
	@NotNull
	private String compressAlgo;
	
	private Integer adfLen;
	private String adf;
	private Integer udrLen;
	private String udr;
	private String tur;
	private String msgType;
	private String catAppl;
	private byte[] localBaData;
	private Integer extraDataLen;
	private String extraData;
	private String sndBaFileDigestAlg;
	private Long sndBaFileDigestLen;
	private String sndBaFileDigest;
	private String mabDigestAlg;
	private Long mabDigestLen;
	private String mabDigest;
	private byte[] filler;
	private Integer mabLen;
	private String mab;
	private String localAuthInfoAlg;
	private Long localAuthInfoLen;
	private String localAuthInfo;
	
}
