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
@AllArgsConstructor
@NoArgsConstructor
public class MQ1401SecSendFilecnf extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1401";
	@NotNull
	private String id = PRIMITIVE_ID;

	private byte[] filler1;
	private byte[] filler2;
	
	private ZonedDateTime acceptTms;
	private Integer result;
	private Integer rejReason;
	
	private String actualLocalAuthInfoAlg;
	private Integer actualLocalAuthInfoLen;
	private String actualLocalAuthInfo;
	
 //================
	
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	
	private Integer sendType;
	
	@NotNull
	private Integer syncFlag;
	
	private String corrId;
	private String vfn;
	
	@NotNull
	private Integer baFileSize;
	@NotNull
	private String queueFileName;
	@NotNull
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
