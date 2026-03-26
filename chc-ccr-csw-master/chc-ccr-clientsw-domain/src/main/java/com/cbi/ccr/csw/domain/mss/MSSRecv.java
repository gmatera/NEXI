package com.cbi.ccr.csw.domain.mss;

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
import com.cbi.ccr.csw.domain.i.CswInWithMessage;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "FAS_MSG_RECV")
public class MSSRecv implements CswInWithMessage{
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "SEQID")
//	@GeneratedValue(generator="seq_fas_msg_recv")
//	@SequenceGenerator(name="seq_fas_msg_recv", sequenceName="FAS_MSG_RECV_SEQ", allocationSize=1)
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
	
	// Reserved to ClientSW
	@Column(name = "STATUS")
	private MSSRecvStatus status;
	
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
	
	@Column(name = "MSGSIZE")
	private Integer messageLeng;
	
	@Column(name = "BA_REQ_TIME")
	@Size(max = 17)
	private String baReqTime;
	
	@Column(name = "BAR_ACQ_TIME")
	@Size(max = 17)
	private String barAcqTime;
	
	@Column(name = "EAS_SUB_TIME")
	@Size(max = 17)
	private String easSubTime;
	
	@Column(name = "FER_SUB_TIME")
	@Size(max = 17)
	private String ferSubTime;
	
	@Column(name = "FEN_DEL_TIME")
	@Size(max = 17)
	private String fenDelTime;
	
	@Column(name = "FER_DEL_TIME")
	@Size(max = 17)
	private String ferDelTime;
	
	@Column(name = "FIRST_BAR_SUB_TIME")
	@Size(max = 17)
	private String firstBarSubTime;
	
	@Column(name = "LAST_BAR_SUB_TIME")
	@Size(max = 17)
	private String lastBarSubTime;
	
	@Column(name = "RECV_RNC_TIMESTAMP")
	private LocalDateTime receiveTimestamp;
	
	@Column(name = "RECV_RC_TIMESTAMP")
	private LocalDateTime recvRcTimestamp;
	
	@Column(name = "STATUS_INFO")
	@Size(max = 4000)
	private String statusInfo;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "CLEANUP_STATUS")
	private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	
	@Column(name = "CLEANUP_LOT")
	private Integer cleanUpLot;
	
	@Enumerated(EnumType.STRING)
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
	
//	@Lob
//	@NotNull
//	@Column(name = "MAB", updatable= false)
//	private Blob message;
	
}
