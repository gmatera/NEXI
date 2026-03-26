package com.cbi.ccr.csw.domain.fts;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.CleanUpStatus;
import com.cbi.ccr.csw.domain.CleanUpType;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.i.CswInboundWithFileEntity;
import com.cbi.ccr.csw.domain.i.HasFile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "RECV_FILE")
public class FTSRecv implements CswInboundWithFileEntity{
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "FAS_SEQID")
//	@GeneratedValue(generator="seq_recv_file")
//	@SequenceGenerator(name="seq_recv_file", sequenceName="RECV_FILE_SEQ", allocationSize=1)
	private Long id;
	
	@NotNull
	@Column(name = "VFN")
	@Size(max = 32)
	private String vfn;
	
	@Column(name = "FNAME")
	@Size(max = 1024)
	private String fileName;
	
	@NotNull
	@Column(name = "LOCAL_BA")
	@Size(max = 12)
	private String localBaId;

	@NotNull
	@Column(name = "REMOTE_BA")
	@Size(max = 12)
	private String remoteBaId;
	
	@Column(name = "STATUS")
	FTSRecvStatus status;
	
	// Reserved to ClientSW
	@Enumerated(EnumType.STRING)
	@Column(name = "CSW_STATUS", length = 60)
	private ClientTaskStatus cswStatus;
	
	@Column(name = "EASSTATUS")
	@Size(max = 3)
	private String easStatus;
	
	@Column(name = "STSCODE")
	@NotNull
	private Integer stsCode;
	
	@Column(name = "COMPLETE")
	@NotNull
	private Integer complete;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TRANSP_TYPE")
	@NotNull
	private TraspType traspType = TraspType.EAS;
	
	@Column(name = "CREATEDATE")
	@Size(max = 24)
	private String createDate;
	
	@Column(name = "LASTUPDATE")
	@Size(max = 24)
	private String lastUpdate;
	
	@Column(name = "APPL_CHECK")
	private Integer applCheck;
	
	@Column(name = "ORIGINAL_FNAME")
	@Size(max = 1024)
	private String originalFileName;
	
	@Column(name = "RETRY_CNT")
	private Integer retryCnt;
	
	@Column(name = "UPDATE_MARK")
	private Integer updateMark;
	
	@Column(name = "FSIZE")
	private Long fileSize;
	
	@Column(name = "FMD5")
	@Size(max = 32)
	private String fileMD5;
	
	@Column(name = "FBLKMOVED")
	private Integer fBlockMoved;
	
	@Column(name = "FILEMAP")
	@Size(max = 256)
	private String fileMap;
	
	@Column(name = "REF_DATE")
	@Size(max = 6)
	private String refDate;
	
	@Column(name = "ACT_REQ_TIME")
	@Size(max = 12)
	private String actReqTime;
	
	@Column(name = "EAS_ACQ_TIME")
	@Size(max = 17)
	private String easReqTime;
	
	@Column(name = "QUEUE_INS_TIME")
	@Size(max = 17)
	private String quequeInsTime;
	
	@Column(name = "START_TIME")
	@Size(max = 17)
	private String startTime;
	
	@Column(name = "EAS_COMPL_TIME")
	@Size(max = 17)
	private String easComplTime;
	
	@Column(name = "FIRST_DEL_TIME")
	@Size(max = 17)
	private String firstDelTime;
	
	@Column(name = "LAST_DEL_TIME")
	@Size(max = 17)
	private String lastDelTime;
	
	@Column(name = "FIRST_READ_TIME")
	@Size(max = 17)
	private String firstReadTime;
	
	@Column(name = "LAST_READ_TIME")
	@Size(max = 17)
	private String lastReadTime;
	
	@Column(name = "BA_PROC_TIME")
	@Size(max = 12)
	private String baProcTime;
	
	@Column(name = "EAS_ELAB_TIME")
	@Size(max = 17)
	private String easElabTime;
	
	@Column(name = "RECV_REQUEST_TIMESTAMP")
	private LocalDateTime rcvRequestTimestamp;

	@Column(name = "RECV_ERROR_TIMESTAMP")
	private LocalDateTime recvErrorTimestamp;

	@Column(name = "RECV_COMPLETE_TIMESTAMP")
	private LocalDateTime receiveTimestamp;
	
	@Column(name = "RECV_DELIVERED_TIMESTAMP")
	private LocalDateTime recvDeliveredTimestamp;
	
	@Column(name = "STATUS_INFO")
	@Size(max = 4000)
	private String statusInfo;
	
	@Column(name = "APPLICATIVE_DATA_FIELD")
	@Size(max = 80)
	private String applicativeDataField;
	
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
	
	@Column(name = "LOCAL_AUTH_INFO_ALG")
	@Size(max = 8)
	private String localAuthInfoAlg;
	
	@Column(name = "LOCAL_AUTH_INFO")
	@Size(max = 128)
	private String localAuthInfo;
	
	@Column(name = "LOCAL_AUTH_INFO_FS")
	@Size(max = 128)
	private String localAuthInfoFs;
	
	@Column(name = "FTS_INTERFACE")
	@Enumerated(EnumType.STRING)
	private RouteInterface ftsInterface = RouteInterface.DB;

	@Override
	public void setFileDigestLen(Long fileDigestLen) {
		throw new UnsupportedOperationException();
	}

	
}
