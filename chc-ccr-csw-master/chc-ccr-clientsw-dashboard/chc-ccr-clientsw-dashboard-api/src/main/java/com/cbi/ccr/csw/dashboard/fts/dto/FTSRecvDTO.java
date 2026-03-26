package com.cbi.ccr.csw.dashboard.fts.dto;

import java.time.LocalDateTime;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendReceiveDTO;
import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.fts.FTSRecvStatus;
import com.cbi.ccr.csw.domain.fts.TraspType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class FTSRecvDTO  extends SendReceiveDTO {
	

	private String vfn;
	private String fileName;

	private String status;
	
	private String easStatus;
	private Integer stsCode;
	private Integer complete;
	private TraspType traspType = TraspType.EAS;
	private String createDate;
	private String lastUpdate;
	private Integer applCheck;
	private String originalFileName;
	private Integer retryCnt;
	private Integer updateMark;
	private Long fileSize;
	private String fileMD5;
	private Integer fBlockMoved;
	private String fileMap;
	private String refDate;
	private String actReqTime;
	private String easReqTime;
	private String quequeInsTime;
	private String startTime;
	private String easComplTime;
	private String firstDelTime;
	private String lastDelTime;
	private String firstReadTime;
	private String lastReadTime;
	private String baProcTime;
	private String easElabTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime receiveTimestamp; // recvRequestTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime cswInsertTimestamp;
	private LocalDateTime recvErrorTimestamp;
	private LocalDateTime recvCompleteTimestamp;
	private LocalDateTime recvDeliveredTimestamp;
	private String statusInfo;
	private String applicativeDataField;
	private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	private Integer cleanUpLot;
	private CleanUpType cleanUpType;
	private String fileDigestAlg;
	private String fileDigest;
	private String localAuthInfoAlg;
	private String localAuthInfo;
	private String localAuthInfoFs;
	private RouteInterface ftsInterface = RouteInterface.DB;

	
}
