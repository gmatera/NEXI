package com.cbi.ccr.csw.dashboard.mss.dto;

import java.time.LocalDateTime;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendReceiveDTO;
import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.cbi.ccr.csw.domain.mss.MSSSendStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MSSSendDTO extends SendReceiveDTO{
	
	private String messageType;
	private String catAppl;
	private String tur;
	private String msgId;
	private String remoteRef;
	private Integer priority;
	private Integer certfReq;
	private Integer stsCode;
	private String status;
	private String easStatus;
	private Integer complete;
	private Integer applCheck;
	private String createDate;
	private String lastUpdate;
	private Integer updateMark;
	private Integer seqId;
	private Integer messageLeng;
	private String baReqTime;
	private String barAcqTime;
	private Integer femsiRetryNumber;
	private String firstEasSubTime;
	private String lastEasSubTime;
	private String ferSubTime;
	private String fenDelTime;
	private String ferDelTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime baInsertTimestamp;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime cswInsertTimestamp;
	private LocalDateTime loadTimestamp;
	private LocalDateTime sendReqTimestamp;
	private LocalDateTime sendErrTimestamp;
	private LocalDateTime sendCnfTimestamp;
	private LocalDateTime sendScTimestamp;
	private String statusInfo;
	private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	private Integer cleanUpLot;
	private CleanUpType cleanUpType;
	private String netMsgId;
	private String msgDigestAlg;
	private String msgDigest;
	private String localAuthInfoAlg;
	private String localAuthInfo;
	private String udr;

}
