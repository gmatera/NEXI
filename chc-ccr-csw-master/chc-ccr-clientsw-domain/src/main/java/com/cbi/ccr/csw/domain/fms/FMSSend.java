package com.cbi.ccr.csw.domain.fms;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.i.CswEntityOutWithFile;
import com.cbi.ccr.csw.domain.i.HasMessage;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "SYNC_SEND")
public class FMSSend implements CswEntityOutWithFile, HasMessage {

	@EqualsAndHashCode.Include
	@Id
	@Column(name = "ID_SYNC_SEND")
	@GeneratedValue(generator="SYNC_SEND_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SYNC_SEND_SEQ", sequenceName="SYNC_SEND_SEQ", allocationSize=1)
	private Long id;
	
	@Column(name = "VFN")
	@Size(max = 32)
	private String vfn;
	
	@NotNull
	@Column(name = "FNAME")
	@Size(max = 1024)
	private String fileName;
	
	@NotNull
	@Column(name = "LOCALBA_ID")
	@Size(max = 12)
	private String localBaId;

	@NotNull
	@Column(name = "REMOTEBA_ID")
	@Size(max = 12)
	private String remoteBaId;
	
	
	@Column(name = "STATUS_BA")
	private FMSSendStatus status = FMSSendStatus.SUBMITTED;
	
	// not used
	@Column(name = "STATUS_SYNC")
	private String statusSync;
	
	// Reserved to ClientSW
	@Enumerated(EnumType.STRING)
	@Column(name = "CSW_STATUS", length = 60)
	private ClientTaskStatus cswStatus;
	
	@Column(name = "STCODE_BA")
	@NotNull
	private Integer statusCodeBA;
	
	@Column(name = "STCODE_SYNC")
	@NotNull
	private Integer statusCodeSync;
	
	@Column(name = "RETRY_CNT")
	private Integer retryCounter;
	
	@NotNull
	@Column(name = "USERDATAREMOTE")
	@Size(max = 80)
	private String udr;
	
	@Column(name = "PRIORITY")
	private Integer priority;
	
	@Column(name = "BAMSG_ID")
	@Size(max = 30)
	private String baMsgId;
	
//	@Lob
//	@NotNull
//	@Column(name = "MESSAGE",updatable= false)
//	private Blob message;
	
	@Column(name = "ACCEPTTIME")
	private LocalDateTime acceptTime;
	
	@Column(name = "FTSSENDTIME")
	private LocalDateTime ftsSendTime;
	
	@Column(name = "FTSCOMPLETETIME")
	private LocalDateTime ftsCompleteTime;
	
	@Column(name = "MSSENDTIME")
	private LocalDateTime msSendTime;
	
	@Column(name = "MSCOMPLETETIME")
	private LocalDateTime msCompleteTime;
	
	@Column(name = "NOTIFYTIME")
	private LocalDateTime notifyTime;
	
	@Column(name = "MODTIME")
	@NotNull
	private LocalDateTime modTime;
	
	@Column(name = "USERDATAREMOTELEN")
	private Integer userDataRemoteLen;
	
	@Column(name = "TUR")
	@Size(max = 16)
	private String tur;
	
	//The length of MESSAGE must be equal to the MESSAGELEN value.
	@Column(name = "MESSAGELEN")
	private Integer messageLeng;
	
	@NotNull
	@Column(name = "MESSAGETYPE")
	@Size(max = 3)
	private String messageType;
	
	@Column(name = "CATAPPL")
	@Size(max = 4)
	private String catAppl;
	
	@Column(name = "ERRORTIME")
	private LocalDateTime errorTime;
	
	@Column(name = "FER_SUB_FST_FTS_TMP")
	private LocalDateTime ferSubFstFtsTmp;
	
	@Column(name = "FER_SUB_LST_FTS_TMP")
	private LocalDateTime ferSubLstFtsTmp;
	
	@Column(name = "FER_DLV_FST_FTS_TMP")
	private LocalDateTime ferDlvFstFtsTmp;
	
	@Column(name = "FER_DLV_LST_FTS_TMP")
	private LocalDateTime ferDlvLstFtsTmp;
	
	@Column(name = "FEN_SUB_MSS_TMP")
	private LocalDateTime fenSubMssTmp;
	
	@Column(name = "FEN_DLV_MSS_TMP")
	private LocalDateTime fenDlvMssTmp;
	
	@Column(name = "CRT_SUB_MSS_TMP")
	private LocalDateTime crtSubMssTmp;
	
	@Column(name = "CRT_DLV_MSS_TMP")
	private LocalDateTime crtDlvMssTmp;
	
	@Column(name = "DATASETNAME")
	@Size(max = 255)
	private String dataSetName;
	
	@Column(name = "LOCAL_DATA")
	@Size(max = 255)
	private String localData;
	
	@NotNull
	@Column(name = "CERTF_REQ")
	private Integer certfReq = 0;
	
	@Column(name = "FSIZE")
	private Long fileSize;
	
	@Column(name = "FHASH")
	@Size(max = 32)
	private String fileMD5;
	
	@Column(name = "FBLOCKMOVED")
	private Integer fBlockMoved;
	
	@Column(name = "FMAP")
	@Size(max = 56)
	private String fMap;
	
	@Column(name = "COMPLETE")
	private Integer complete;
	
	@Column(name = "LAST_UPDATE")
	private Integer lastUpdate;
	
	@Column(name = "BA_INSERT_TIMESTAMP")
	private LocalDateTime baInsertTimestamp;
	
	@Column(name = "SEND_ACCEPTED_TIMESTAMP")
	private LocalDateTime sendAcceptedTimestamp;
	
	@Column(name = "SEND_REQUEST_TIMESTAMP")
	private LocalDateTime sendRequestTimestamp;
	
	@Column(name = "SEND_CONFIRMED_TIMESTAMP")
	private LocalDateTime sendConfirmedTimestamp;
	
	@Column(name = "SEND_COMPLETED_TIMESTAMP")
	private LocalDateTime sendCompletedTimestamp;
	
	@Column(name = "SEND_ERROR_TIMESTAMP")
	private LocalDateTime sendErrorTimestamp;
	
	@Column(name = "STATUS_INFO")
	@Size(max = 4000)
	private String statusInfo;
	
	@Column(name = "OPERATION_TIMESTAMP")
	private LocalDateTime operationTimestamp;
	
	@Column(name = "OPERATION")
	@Size(max = 10)
	private String operation;
	
	@Column(name = "REACTIVATE")
	@Size(max = 10)
	private String reactivate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "CLEANUP_STATUS")
	@NotNull
	private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	
	@Column(name = "CLEANUP_LOT")
	private Integer cleanUpLot;
	
	@Column(name = "CLEANUP_TYPE")
	private CleanUpType cleanUpType;
	
	@Column(name = "FILE_DIGEST_ALG")
	@Size(max = 8)
	private String fileDigestAlg;
	
	@Column(name = "FILE_DIGEST")
	@Size(max = 128)
	private String fileDigest;
	
	@Column(name = "MSG_DIGEST_ALG")
	@Size(max = 8)
	private String msgDigestAlg;
	
	@Column(name = "MSG_DIGEST")
	@Size(max = 128)
	private String msgDigest;
	
	@Column(name = "LOCAL_AUTH_INFO_ALG")
	@Size(max = 8)
	private String localAuthInfoAlg;
	
	@Column(name = "LOCAL_AUTH_INFO")
	@Size(max = 128)
	private String localAuthInfo;

	@Override
	public void setFileDigestLen(Long fileDigestLen) {
		// not used here
	}

//	@Override
//	public RouteInterface getFtsInterface() {
//		return RouteInterface.DB;
//	}

}
