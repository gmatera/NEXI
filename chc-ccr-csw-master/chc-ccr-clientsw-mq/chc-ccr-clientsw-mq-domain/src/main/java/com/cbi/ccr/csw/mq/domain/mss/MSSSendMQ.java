	package com.cbi.ccr.csw.mq.domain.mss;

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

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.i.CSWEntityOutMqWithMessage;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "MSS_SEND_MQI")
public class MSSSendMQ implements CSWEntityOutMqWithMessage {
	
	public static final String ID = "FAS_SEQID";
	public static final String TABLE_NAME = "MSS_SEND_MQI";
	public static final String BLOB_MESSAGE_FIELD = "MESSAGE";
	// contiene la primitivaDTO serializzata in java, da utilizzare nel caso di retry
	public static final String BLOB_PRIMITIVE_FIELD = "ORIGINAL_PRIMITIVE";
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "FAS_SEQID")
	@NotNull
	@GeneratedValue(generator="SEQ_MSS_SEND_MQI", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_MSS_SEND_MQI", sequenceName="SEQ_MSS_SEND_MQI", allocationSize=1)
	private Long id;
	
	@Column(name = "LOCALBA_ID", nullable = false, length = 12)
	@Size(max = 12)
	private String localBaId;

	@Column(name = "REMOTEBA_ID", nullable = false, length = 12)
	@Size(max = 12)
	private String remoteBaId;
	
	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private SendStatusMQ status = SendStatusMQ.SENDING;
	
	// Reserved to ClientSW
	@Column(name = "CSW_STATUS", length = 60)
	@Enumerated(EnumType.STRING)
	private ClientTaskStatus cswStatus;
	
	@Column(name = "LOCAL_BA_DATA", length = 100)
	@Size(max = 100)
	private String localBaData;
	
//	@Column(name = "MSGID", nullable = false, length = 30)
//	@Size(max = 30)
//	private String msgId;
	
	@Column(name = "PRIORITY", nullable = false)
	private Integer priority;
	
	@Column(name = "TUR", length = 16)
	@Size(max = 16)
	private String tur;
	
	@Column(name = "MSG_TYPE", nullable = false, length = 3)
	@Size(max = 3)
	private String messageType;
	
	@Column(name = "CAT_APPL", length = 4)
	@Size(max = 4)
	private String catAppl;
	
	@Column(name = "CORRELATION_ID", length = 30)
	@Size(max = 30)
	private String correlationId;
	
	@Column(name = "UDR_LEN")
	private Integer udrLen;
	
	@Column(name = "UDR", length = 80)
	@Size(max = 80)
	private String udr;
	
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
	
	@Column(name = "BA_REQ_TMS")
	private LocalDateTime baReqTms;
	
	@Column(name = "ACCEPTTIME")
	private LocalDateTime acceptTime;
	
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
	
	@Column(name = "CSW_RETRY_CNT")
	private Integer retryCounter;
	
	
	@NotNull
	@Column(name = "CSW_PRIMITIVE", length = 20)
	private String primitive;
}
