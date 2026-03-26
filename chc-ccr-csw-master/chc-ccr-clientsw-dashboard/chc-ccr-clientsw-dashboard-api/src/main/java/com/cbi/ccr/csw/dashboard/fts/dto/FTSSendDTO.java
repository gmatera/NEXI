package com.cbi.ccr.csw.dashboard.fts.dto;

import java.time.LocalDateTime;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendReceiveDTO;
import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.fts.FTSSendStatus;
import com.cbi.ccr.csw.domain.fts.TraspType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FTSSendDTO extends SendReceiveDTO{
	
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
	private Integer updateMark;
	private Long fileSize;
	private String fileMD5;
	private Integer fBlockMoved;
	private String fileMap;
	private LocalDateTime operationTimestamp;
	private String operation;
	private String reactivate;
	private String refDate;
	private String actReqTime;
	private String easReqTime;
	private String quequeInsTime;
	private String startTime;
	private String easComplTime;
	private String baProcTime;
	private String easElabTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime baInsertTimestamp;   // DEFAULT sys_extract_utc(systimestamp),
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime cswInsertTimestamp;
	private LocalDateTime sendAcceptedTimestamp;
	private LocalDateTime sendGftRequestTimestamp;
	private LocalDateTime sendRequestTimestamp;
	private LocalDateTime sendConfirmedTimestamp;
	private LocalDateTime sendCompletedTimestamp;
	private LocalDateTime sendErrorTimestamp;
	private String statusInfo;
	private String applicativeDataField;
	private Integer applicativeDataFieldLength;
	private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	private Integer cleanUpLot;
	private CleanUpType cleanUpType;
	private String fileDigestAlg;
	private String fileDigest;
	private String localAuthInfoAlg;
	private String localAuthInfo;
	private String localAuthInfoFs;
	private Long version;
	private RouteInterface ftsInterface = RouteInterface.DB;
}
