package com.cbi.ccr.csw.dashboard.fms.dto;

import java.time.LocalDateTime;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendReceiveDTO;
import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.cbi.ccr.csw.domain.fms.FMSRecvStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FMSRecvDTO extends SendReceiveDTO{
	
	private String vfn;
	private String status;
	
	private Integer statusCodeBA;
	private Integer statusCodeSync;
	private Integer timeout;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime receiveTimestamp;
	private LocalDateTime msRecvTime;
	private String fileName;
	private String udr;
	private Integer priority;
	private String messageType;
	private LocalDateTime modTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime cswInsertTimestamp;
	
	//The length of MESSAGE must be equal to the MESSAGELEN value.
	private Integer messageLeng;
	
	private String catAppl;
	private String tur;
	private String baMsgId;
	private LocalDateTime receiveTime;
	private LocalDateTime ftsInsertTime;
	private LocalDateTime msConfTime;
	private LocalDateTime ftsDelivTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime baProcessedTmp;
	private String dataSetName;
	private LocalDateTime ferSubFstFtsTmp;
	private LocalDateTime ferSubLstFtsTmp;
	private LocalDateTime ferDlvFstFtsTmp;
	private LocalDateTime ferDlvLstFtsTmp;
	private LocalDateTime fenSubMssTmp;
	private LocalDateTime fenDlvMssTmp;
	private LocalDateTime crtSubMssTmp;
	private LocalDateTime crtDlvMssTmp;
	private LocalDateTime readStrFtsTmp;
	private LocalDateTime readEndFtsTmp;
	private String localData;
	private Integer certfReq;
	private LocalDateTime startRecvGmt;
	private Long fileSize;
	private String fileMD5;
	private Integer fBlockMoved; 
	private String fMap;
	private Integer complete;
	private Integer lastUpdate;
	private LocalDateTime readRequestTimestamp;
	private LocalDateTime readConfirmedTimestamp;
	private LocalDateTime readCompletedTimestamp;
	private LocalDateTime readNotifiedTimestamp;
	private LocalDateTime readErrorTimestamp;
	private String statusInfo;
	private String fileFormat;
	private String compressionAlgo;
	private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	private Integer cleanUpLot;
	private CleanUpType cleanUpType;
	private String fileDigestAlg;
	private String fileDigest;
	private String msgDigestAlg;
	private String msgDigest;
	private String localAuthInfoAlg;
	private String localAuthInfo;
	
}
