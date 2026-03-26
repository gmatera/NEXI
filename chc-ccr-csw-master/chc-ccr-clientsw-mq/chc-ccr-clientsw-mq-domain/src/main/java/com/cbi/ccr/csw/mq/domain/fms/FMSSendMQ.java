package com.cbi.ccr.csw.mq.domain.fms;

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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.i.CswEntityOutMqWithFile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "FMS_SEND_MQI")
public class FMSSendMQ implements CswEntityOutMqWithFile {

	public static final String ID = "ID_SYNC_SEND";
	public static final String TABLE_NAME = "FMS_SEND_MQI";
	public static final String BLOB_MESSAGE_FIELD = "MESSAGE";
	// contiene la primitivaDTO serializzata in java, da utilizzare nel caso di retry
	public static final String BLOB_PRIMITIVE_FIELD = "ORIGINAL_PRIMITIVE";
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "ID_SYNC_SEND")
	@NotNull
	@GeneratedValue(generator="SEQ_FMS_SEND_MQI" , strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_FMS_SEND_MQI", sequenceName="SEQ_FMS_SEND_MQI", allocationSize=1)
	private Long id;
	
	@Column(name = "LOCALBA_ID", nullable = false, length = 12)
	@Size(max = 12)
	private String localBaId;

	@Column(name = "REMOTEBA_ID", nullable = false, length = 12)
	@Size(max = 12)
	private String remoteBaId;
	
	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private SendStatusMQ status = SendStatusMQ.CREATING;
	
	// Reserved to ClientSW
	@Column(name = "CSW_STATUS", length = 60)
	@Enumerated(EnumType.STRING)
	private ClientTaskStatus cswStatus;
	
	@Column(name = "SEND_TYPE")
	private Integer sendType;
	
	@Column(name = "CORRELATION_ID", length = 30)
	@Size(max = 30)
	private String correlationId;
	
	@Column(name = "VFN", nullable = false, length = 32)
	@Size(max = 32)
	private String vfn;
	
	@Column(name = "FSIZE", nullable = false)
	private Long fileSize;
	
	@Column(name = "QUEUE_FILENAME", nullable = false, length = 48)
	@Size(max = 48)
	private String quequeFileName;
//	
	@Column(name = "GROUP_ID", nullable = false, length = 48)
	@Size(max = 48)
	private String groupId;
	
	@Column(name = "LINE_SEPARATOR", nullable = false)
	@Enumerated(EnumType.STRING)
	private LineSeparator lineSeparator;
	
	@Column(name = "RECORD_FORMAT")
	@Enumerated(EnumType.STRING)
	private RecordFormat recordFormat;
	
	// Valid values are from 0 to 32767
	@Column(name = "MAX_REC_LEN")
	private Integer maxRecLen;
	
	@Column(name = "CHAR_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private CodePage charType;
	
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
	@Size(max = 4)
	private String catAppl;
	
	@Column(name = "LOCAL_BA_DATA", length = 80)
	@Size(max = 80)
	private String localBaData;
	
	@Column(name = "NET_FILE_SIZE")
	private Long netFileSize;
	
	@Column(name = "TRANSFER_ID", length = 16)
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
	
	@Column(name = "ACCEPTTIME")
	private LocalDateTime acceptTime;
	
	@Column(name = "START_CREATE_TIMESTAMP")
	private LocalDateTime startCreateTimestamp;
	
	@Column(name = "END_CREATE_TIMESTAMP")
	private LocalDateTime endCreateTimestamp;
	
	@Column(name = "ERROR_TIMESTAMP")
	private LocalDateTime errorTimestamp;
	
	@Column(name = "SENDTIME")
	private LocalDateTime sendTime;
	
	@Column(name = "COMPLETETIME")
	private LocalDateTime completeTime;
	
	@Column(name = "NOTIFYTIME")
	private LocalDateTime notifyTime;
	
	@Column(name = "BA_PROCESS_TMS")
	private LocalDateTime baProcessTms;
	
	@Column(name = "CSW_PROCESS_TMS")
	private LocalDateTime cswProcessTms;
	
	@Column(name = "SEND_ERROR_TIMESTAMP")
	private LocalDateTime sendErrorTimestamp;
	
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
	
	@Column(name = "NEG_DETAIL_OPER", length = 1024)
	@Size(max = 1024)
	private String negDetailOper;
	
	@Column(name = "CSW_RETRY_CNT")
	private Integer retryCounter;
		
	// dummy field
	@Transient()
	private String fileMD5 = null;
	
	// the primitive unique code. eg 1400
	@NotNull
	@Column(name = "CSW_PRIMITIVE", length = 20)
	private String primitive;
}
