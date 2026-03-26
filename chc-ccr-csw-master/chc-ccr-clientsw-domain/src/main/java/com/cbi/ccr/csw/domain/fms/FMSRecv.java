package com.cbi.ccr.csw.domain.fms;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.i.CswInboundWithFileEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "SYNC_RECV")
public class FMSRecv implements CswInboundWithFileEntity{
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "FAS_SEQID")
//	@GeneratedValue(generator="seq_sync_recv")
//	@SequenceGenerator(name="seq_sync_recv", sequenceName="SYNC_RECV_SEQ", allocationSize=1)
	private Long id;
	
	@NotNull
	@Column(name = "LOCALBA_ID")
	@Size(max = 12)
	private String localBaId;

	@NotNull
	@Column(name = "REMOTEBA_ID")
	@Size(max = 12)
	private String remoteBaId;
	
	@NotNull
	@Column(name = "VFN")
	@Size(max = 32)
	private String vfn;
	
	@Column(name = "STATUS_BA")
	private FMSRecvStatus status;
	
	// not used
	@Column(name = "STATUS_SYNC")
	private String statusSync;
	
	// Reserved to ClientSW
	@Enumerated(EnumType.STRING)
	@Column(name = "CSW_STATUS", length = 60 )
	private ClientTaskStatus cswStatus;
	
	@Column(name = "STCODE_BA")
	private Integer statusCodeBA;
	
	@Column(name = "STCODE_SYNC")
	private Integer statusCodeSync;
	
	@Column(name = "TIMEOUT")
	private Integer timeout;
	
	@Column(name = "FTSRECVTIME")
	private LocalDateTime receiveTimestamp;
	
	@Column(name = "MSRECVTIME")
	private LocalDateTime msRecvTime;
	
	@NotNull
	@Column(name = "FNAME")
	@Size(max = 1024)
	private String fileName;
	
	@NotNull
	@Column(name = "USERDATAREMOTE")
	@Size(max = 80)
	private String udr;
	
	@Column(name = "PRIORITY")
	private Integer priority;
	
	@NotNull
	@Column(name = "MESSAGETYPE")
	@Size(max = 3)
	private String messageType;
	
	@Column(name = "MODTIME")
	private LocalDateTime modTime;
	
	//The length of MESSAGE must be equal to the MESSAGELEN value.
	@Column(name = "MESSAGELENGTH")
	private Integer messageLeng;
	
	@Column(name = "CAT_APPLY")
	@Size(max = 4)
	private String catAppl;
	
	@Column(name = "TUR")
	@Size(max = 16)
	private String tur;
	
	@Column(name = "BAMSGID")
	@Size(max = 30)
	private String baMsgId;
	
	@Column(name = "RECEIVETIME")
	private LocalDateTime receiveTime;
	
	@Column(name = "FTSINSERTTIME")
	private LocalDateTime ftsInsertTime;
	
	@Column(name = "MSCONFTIME")
	private LocalDateTime msConfTime;
	
	@Column(name = "FTSDELIVTIME")
	private LocalDateTime ftsDelivTime;
	
	@Column(name = "BA_PROCESSED_TMP")
	private LocalDateTime baProcessedTmp;
	
	@Column(name = "DATASETNAME")
	@Size(max = 255)
	private String dataSetName;
	
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
	
	@Column(name = "READ_STR_FTS_TMP")
	private LocalDateTime readStrFtsTmp;
	
	@Column(name = "READ_END_FTS_TMP")
	private LocalDateTime readEndFtsTmp;
	
	@Column(name = "LOCAL_DATA")
	@Size(max = 255)
	private String localData;
	
	@Column(name = "CERTF_REQ")
	private Integer certfReq;
	
	@Column(name = "START_RECV_GMT")
	private LocalDateTime startRecvGmt;
	
	@Column(name = "FSIZE")
	private Long fileSize;
	
	@Column(name = "FHASH")
	@Size(max = 32)
	private String fileMD5;
	
	@Column(name = "FBLOCKMOVED")
	private Integer fBlockMoved; 
	
	@Column(name = "FMAP")
	@Size(max = 256)
	private String fMap;
	
	@Column(name = "COMPLETE")
	private Integer complete;
	
	@Column(name = "LAST_UPDATE")
	private Integer lastUpdate;
	
	@Column(name = "READ_REQUEST_TIMESTAMP")
	private LocalDateTime readRequestTimestamp;
	
	@Column(name = "READ_CONFIRMED_TIMESTAMP")
	private LocalDateTime readConfirmedTimestamp;
	
	@Column(name = "READ_COMPLETED_TIMESTAMP")
	private LocalDateTime readCompletedTimestamp;
	
	@Column(name = "READ_NOTIFIED_TIMESTAMP")
	private LocalDateTime readNotifiedTimestamp;
	
	@Column(name = "READ_ERROR_TIMESTAMP")
	private LocalDateTime readErrorTimestamp;
	
	@Column(name = "STATUS_INFO")
	@Size(max = 4000)
	private String statusInfo;
	
	@Column(name = "FILE_FORMAT")
	@Size(max = 32)
	private String fileFormat;
	
	@Column(name = "COMPRESSION_ALGO")
	@Size(max = 32)
	private String compressionAlgo;
	
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
		throw new UnsupportedOperationException();
	}

}
