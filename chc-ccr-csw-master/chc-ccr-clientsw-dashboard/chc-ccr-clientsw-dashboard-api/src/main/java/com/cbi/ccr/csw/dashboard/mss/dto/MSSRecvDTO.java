package com.cbi.ccr.csw.dashboard.mss.dto;

import java.sql.Blob;
import java.time.LocalDateTime;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendReceiveDTO;
import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class MSSRecvDTO extends SendReceiveDTO{
	

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
	private Integer messageLeng; //msgSize
	private String baReqTime;
	private String barAcqTime;
	private String easSubTime;
	private String ferSubTime;
	private String fenDelTime;
	private String ferDelTime;
	private String firstBarSubTime;
	private String lastBarSubTime;
	private LocalDateTime recvRncTimestamp;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime receiveTimestamp; // recvRequestTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime cswInsertTimestamp;
	private String statusInfo;
	private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	private Integer cleanUpLot;
	private CleanUpType cleanUpType;
	private String netMsgId;
	private String msgDigestAlg;
	private String msgDigest;
	private String localAuthInfoAlg;
	private String localAuthInfo;
	private Blob message;
	private String udr;
	
}
