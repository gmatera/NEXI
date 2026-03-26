package com.cbi.ccr.csw.domain.mss;

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
import com.cbi.ccr.csw.domain.i.CswOutWithMessage;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "FAS_MSG_SEND")
public class MSSSend implements CswOutWithMessage {
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "FAS_SEQID")
	@GeneratedValue(generator="seq_fas_msg_send", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="seq_fas_msg_send", sequenceName="FAS_MSG_SEND_SEQ", allocationSize=1)
	private Long id;
	
	@NotNull
	@Column(name = "LOCAL_BA")
	@Size(max = 12)
	private String localBaId;

	@NotNull
	@Column(name = "REMOTE_BA")
	@Size(max = 12)
	private String remoteBaId;
	
	@Column(name = "MSG_TYPE")
	@Size(max = 3)
	private String messageType;
	
	@Column(name = "CAT_APPL")
	@Size(max = 4)
	private String catAppl;
	
	@Column(name = "TUR")
	@Size(max = 16)
	private String tur;
	
	@Column(name = "MSGID")
	@Size(max = 30)
	@NotNull
	private String msgId;
	
	@Column(name = "REMOTE_REF")
	@Size(max = 80)
	private String remoteRef;
	
	@Column(name = "PRIORITY")
	private Integer priority;
	
	@Column(name = "CERTF_REQ")
	private Integer certfReq;
	
	@Column(name = "STSCODE")
	private Integer stsCode;
	
	@Column(name = "STATUS")
	private MSSSendStatus status = MSSSendStatus.NEW_TRAFFIC;
	
	// Reserved to ClientSW
	@Enumerated(EnumType.STRING)
	@Column(name = "CSW_STATUS", length = 60)
	private ClientTaskStatus cswStatus;
	
	@Column(name = "EASSTATUS")
	@Size(max = 3)
	private String easStatus;
	
	@Column(name = "COMPLETE")
	private Integer complete;
	
	@Column(name = "APPL_CHECK")
	private Integer applCheck;
	
	@Column(name = "CREATEDATE")
	@Size(max = 24)
	private String createDate;
	
	@Column(name = "LASTUPDATE")
	@Size(max = 24)
	private String lastUpdate;
	
	@Column(name = "UPDATE_MARK")
	private Integer updateMark;
	
	@Column(name = "SEQID")
	private Integer seqId;
	
	@Column(name = "MSGSIZE")
	private Integer messageLeng;
	
//	@Lob()
//	@Column(name = "MAB", updatable=false, insertable = true)
//	private Blob message;
	
	@Column(name = "BA_REQ_TIME")
	@Size(max = 17)
	private String baReqTime;
	
	@Column(name = "BAR_ACQ_TIME")
	@Size(max = 17)
	private String barAcqTime;
	
	@Column(name = "FEMSI_RETRY_NUMBER")
	private Integer femsiRetryNumber;
	
	@Column(name = "FIRST_EAS_SUB_TIME")
	@Size(max = 17)
	private String firstEasSubTime;
	
	@Column(name = "LAST_EAS_SUB_TIME")
	@Size(max = 17)
	private String lastEasSubTime;
	
	@Column(name = "FER_SUB_TIME")
	@Size(max = 17)
	private String ferSubTime;
	
	@Column(name = "FEN_DEL_TIME")
	@Size(max = 17)
	private String fenDelTime;
	
	@Column(name = "FER_DEL_TIME")
	@Size(max = 17)
	private String ferDelTime;
	
	@Column(name = "BA_INSERT_TIMESTAMP")
	private LocalDateTime baInsertTimestamp;
	
	@Column(name = "LOAD_TIMESTAMP")
	private LocalDateTime loadTimestamp;
	
	@Column(name = "SEND_REQ_TIMESTAMP")
	private LocalDateTime sendReqTimestamp;
	
	@Column(name = "SEND_ERR_TIMESTAMP")
	private LocalDateTime sendErrTimestamp;
	
	@Column(name = "SEND_CNF_TIMESTAMP")
	private LocalDateTime sendCnfTimestamp;
	
	@Column(name = "SEND_SC_TIMESTAMP")
	private LocalDateTime sendScTimestamp;
	
	@Column(name = "STATUS_INFO")
	@Size(max = 4000)
	private String statusInfo;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "CLEANUP_STATUS")
	private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	
	@Column(name = "CLEANUP_LOT")
	private Integer cleanUpLot;
	
	@Column(name = "CLEANUP_TYPE")
	private CleanUpType cleanUpType;
	
	@Column(name = "NET_MSGID")
	@Size(max = 16)
	private String netMsgId;
	
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
	
	/**
	 * Colonna da aggiungere all'alter table
	 */
	@Column(name = "CSW_RETRY_CNT")
	private Integer retryCounter;
	

}
