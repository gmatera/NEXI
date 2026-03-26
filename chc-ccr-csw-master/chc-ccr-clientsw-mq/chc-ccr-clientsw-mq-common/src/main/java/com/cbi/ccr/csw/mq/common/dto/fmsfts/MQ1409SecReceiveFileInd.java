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
public class MQ1409SecReceiveFileInd extends PrimitiveMQ {
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMITIVE_ID = "1409";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String baLoc;
	@NotNull
	private String baRem;
	@NotNull
	private Integer syncFlag;
	@NotNull
	private String vfn;
	
	private Integer adfLen;
	private String adf;
	private String aggrI;
	private String aggrII;
	private byte[] fillerOne;
	
	@NotNull
	private Integer recType;
	@NotNull
	private Integer maxRecLen;
	private byte[] fillerTwo;
	@NotNull
	private Integer charType;
	
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
	private ZonedDateTime ferLastBDeliveryTms;
	@NotNull
	private ZonedDateTime hostFirstDlvTms;
	@NotNull
	private ZonedDateTime ftsEndReceiveTms;
	@NotNull
	private ZonedDateTime fermsSubTms;
	@NotNull
	private ZonedDateTime fenmsSubTms;
	@NotNull
	private ZonedDateTime fenmsDlvTms;
	@NotNull
	private ZonedDateTime fermsDlvTms;
	
	private Integer extraDataLen;
	private String extraData;
	private byte[] fillerThree;
	private String localAuthInfoAlg;
	private Long localAuthInfoLen;
	private String localAuthInfo;
	
}
