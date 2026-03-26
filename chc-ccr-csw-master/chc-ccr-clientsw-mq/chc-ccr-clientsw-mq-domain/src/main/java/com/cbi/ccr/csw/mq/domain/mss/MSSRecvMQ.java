package com.cbi.ccr.csw.mq.domain.mss;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.i.CswInWithMessage;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "MSS_RECV_MQI")
public class MSSRecvMQ  implements CswInWithMessage {
	
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
	@Column(name = "CSW_STATUS", length = 60)
	@Enumerated(EnumType.STRING)
	private ClientTaskStatus cswStatus;
	
	@Column(name = "MSGID", nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String msgId;
	
//	@Column(name = "MSGIDR", nullable = false, length = 30)
//	@Size(max = 30)
//	@NotNull
//	private String msgIdr;
	
	@Column(name = "PRIORITY", nullable = false)
	private Integer priority;
	
	@Column(name = "UDR_LEN")
	private Integer udrLen;
	
	@Column(name = "UDR", length = 80)
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
	
	@Column(name = "MSG_DIGEST_ALG", length = 8)
	@Size(max = 8)
	private String msgDigestAlg;
	
	@Column(name = "MSG_DIGEST_LEN")
	private Long msgDigestLen;
	
	@Column(name = "MSG_DIGEST", length = 128)
	@Size(max = 128)
	private String msgDigest;
	
	@Column(name = "MESSAGELEN")
	@Size(max = 128)
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
	
	
}
