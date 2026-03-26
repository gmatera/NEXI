package com.cbi.ccr.csw.dashboard.fms.dto;

import java.time.LocalDateTime;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendReceiveDTO;
import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FMSSendDTO extends SendReceiveDTO{

	private String vfn;
	private String fileName;
	private String status;
	//private SendStatusMQ status;

	// Reserved to ClientSW
	private Integer statusCodeBA;
	private Integer statusCodeSync;
	private String udr;
	private Integer priority;
	private String baMsgId;
	
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime acceptTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ftsSendTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ftsCompleteTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime msSendTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime msCompleteTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime notifyTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime modTime;
	
	private Integer userDataRemoteLen;
	private String tur;
	private Integer messageLeng;
	private String messageType;
	private String catAppl;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime errorTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ferSubFstFtsTmp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ferSubLstFtsTmp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ferDlvFstFtsTmp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime ferDlvLstFtsTmp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fenSubMssTmp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fenDlvMssTmp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime crtSubMssTmp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime crtDlvMssTmp;
	private String dataSetName;
	private String localData;
	private Integer certfReq;
	private Long fileSize;
	private String fileHash;
	private Integer fBlockMoved;
	private String fMap;
	private Integer complete;
	private Integer lastUpdate;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime baInsertTimestamp;
		
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime cswInsertTimestamp;
		
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sendAcceptedTimestamp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sendRequestTimestamp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sendConfirmedTimestamp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sendCompletedTimestamp;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sendErrorTimestamp;
	private String statusInfo;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime operationTimestamp;
	
	private String operation;
	private String reactivate;
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
