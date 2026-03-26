package com.cbi.ccr.csw.mq.domain.fms;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.i.CswInboundMqWithFileEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "FMS_RECV_MQI")
public class FMSRecvMQ  implements CswInboundMqWithFileEntity {
	
	@EqualsAndHashCode.Include
	@Id
	@NotNull
	@Column(name = "ID")
	private Long id;

	@Column(name = "LOCALBA_ID", nullable = false, length = 12)
	@Size(max = 12)
	private String localBaId;

	@Column(name = "REMOTEBA_ID", nullable = false, length = 12)
	@Size(max = 12)
	private String remoteBaId;
	
	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private RecvStatusMQ status = RecvStatusMQ.RECEIVING;
	
	// Reserved to ClientSW
	@Enumerated(EnumType.STRING)
	@Column(name = "CSW_STATUS", length = 60)
	private ClientTaskStatus cswStatus;
	
	@Column(name = "CORRELATION_ID", length = 30)
	@Size(max = 30)
	private String correlationId;
	
	@Column(name = "VFN", nullable = false, length = 32)
	@Size(max = 32)
	private String vfn;
	
	@Column(name = "FSIZE", nullable = false)
	private Long fileSize;
	
	@Column(name = "QUEUE_FILENAME", length = 48)
	@Size(max = 48)
	private String queueFileName;
	
	@Column(name = "GROUP_ID", length = 48)
	@Size(max = 48)
	private String groupId;
	
	@Column(name = "LINE_SEPARATOR")
	@Enumerated(EnumType.STRING)
	private LineSeparator lineSeparator;
	
	@Column(name = "RECORD_FORMAT")
	@Enumerated(EnumType.STRING)
	private RecordFormat recordFormat;
	
	// Valid values are from 0 to 32767
	@Column(name = "MAX_REC_LEN")
	private Integer maxRecLen;
	
	@Column(name = "SND_CHAR_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private CodePage sndCharType;
	
	@Column(name = "RCV_CHAR_TYPE")
	@Enumerated(EnumType.STRING)
	private CodePage rcvCharType;
	
	@Column(name = "ADF_LEN")
	private Integer adfLen;
	
	@Column(name = "ADF", length = 80)
	@Size(max = 80)
	private String adf;
	
	@Column(name = "UDR_LEN", nullable = false)
	private Integer udrLen;
	
	@Column(name = "UDR", nullable = false, length = 80)
	@Size(max = 80)
	private String udr;
	
	@Column(name = "TUR", length = 16)
	@Size(max = 16)
	private String tur;
	
	@Column(name = "MESSAGETYPE", nullable = false, length = 3)
	@Size(max = 3)
	private String messageType;
	
	@Column(name = "CATAPPL", length = 4)
	private String catAppl;
	
	@Column(name = "LOCAL_BA_DATA", length = 80)
	@Size(max = 80)
	private String localBaData;
	
	@Column(name = "NET_FILE_SIZE")
	@NotNull
	private Long netFileSize;
	
	@Column(name = "TRANSFER_ID", nullable = false, length = 16)
	@Size(max = 16)
	private String transferId;
	
	@Column(name = "FNAME", length = 1024)
	@Size(max = 1024)
	private String fileName;
	
	// TODO usare costante
	@Column(name = "FILE_DIGEST_ALG", length = 8)
	@Size(max = 8)
	private String fileDigestAlg = "SHA-256";
		
	@Column(name = "FILE_DIGEST_LEN")
	private Long fileDigestLen;
	
	@Column(name = "FILE_DIGEST", length = 128)
	@Size(max = 128)
	private String fileDigest;
	
	@Column(name = "MSG_DIGEST_ALG", length = 8)
	@Size(max = 8)
	private String msgDigestAlg;
	
	@Column(name = "MSG_DIGEST_LEN")
	private Long msgDigestLen;
	
	@Column(name = "MSG_DIGEST", length = 128)
	@Size(max = 128)
	private String msgDigest;
	
	//The length of MESSAGE must be equal to the MESSAGELEN value.
	@Column(name = "MESSAGELEN", nullable = false)
	private Integer messageLeng;
	
//	@Lob
//	@NotNull
//	@Column(name = "MESSAGE",updatable= false)
//	private Blob message;
	
	@Column(name = "LOCAL_AUTH_INFO_ALG", length = 8)
	@Size(max = 8)
	private String localAuthInfoAlg;
	
	@Column(name = "LOCAL_AUTH_INFO_LEN")
	private Long localAuthInfoLen;
	
	@Column(name = "LOCAL_AUTH_INFO", length = 128)
	@Size(max = 128)
	private String localAuthInfo;
	
	@Column(name = "CSW_INSERT_TIMESTAMP")
	private LocalDateTime cswInsertTimestamp;
	
	@Column(name = "ACCEPT_TMS")
	private LocalDateTime acceptTms;
	
	@Column(name = "START_READ_TMS")
	private LocalDateTime startReadTms;
	
	@Column(name = "END_READ_TMS")
	private LocalDateTime endReadTms;
	
	@Column(name = "BA_PROCESS_TMS")
	private LocalDateTime baProcessTms;
	
	@Column(name = "CSW_PROCESS_TMS")
	private LocalDateTime cswProcessTms;
	
	@Column(name = "HOST_FIRST_SUB_TMS")
	private LocalDateTime hostFirstSubTms;
	
	@Column(name = "FER_SUB_TIME")
	private LocalDateTime ferSubTime;
	
	@Column(name = "FEN_SUB_TMS")
	private LocalDateTime fenSubTime;
	
	@Column(name = "HOST_FIRST_DEL_TMS")
	private LocalDateTime hostFirstDelTms;
	
	@Column(name = "FIRST_BA_DLV_TMS")
	private LocalDateTime firstBaDlvTms;
	
	@Column(name = "COMPLETE")
	private Integer complete;
	
	@Column(name = "PRIMITIVE_ERROR", length = 50)
	@Size(max = 50)
	private String primitiveError;
	
	@Column(name = "REJECT_REASON")
	private Integer rejectReason;
	
	@Column(name = "STATUS_INFO", length = 1024)
	@Size(max = 4000)
	private String statusInfo;
	
	@Column(name = "CSW_RETRY_CNT")
	private Integer retryCounter;
	
	// dummy field
	@Transient()
	private String fileMD5 = null;

}
