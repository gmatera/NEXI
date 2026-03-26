package com.cbi.ccr.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
public abstract class CommonEntity {
	
	public enum Direction { INBOUND, OUTBOUND}
	
	@EqualsAndHashCode.Include
	@Id
	@NotNull
	@Column(name = "phy_msg_id")
	private Long id;
	
	@Column(name = "insert_Date", nullable = false)
	private LocalDateTime insertDate = LocalDateTime.now();
	
	// correlation ID
	@Column(name = "CHC_ID", nullable = false)
	private Long chcId;
	
	@Column(name = "LOCALBA_ID", length = 12, nullable = false)
	@Size(max = 12)
	private String localBaId;
	
	@Column(name = "REMOTE_BA_ID", length = 12, nullable = false)
	@Size(max = 12)
	private String remoteBaId;
	
	@Column(name = "BA_INSERT_TIMESTAMP")
	private LocalDateTime baInsertTimestamp;   // DEFAULT sys_extract_utc(systimestamp)
	
	@Column(name = "RECEIVED_TIMESTAMP")
	private LocalDateTime tmsReceived;
	
	@Column(name = "START_SENDING_TIMESTAMP")
	private LocalDateTime tmsStartSending;
	
	@Column(name = "UDR", length = 80)
	@Size(max = 80)
	private String udr;

	// Reserved to ClientSW
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ClientMessageStatus status;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "sub_status", nullable = false)
	private ClientMessageSubStatus subStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "direction", length = 10, nullable = false)
	private Direction direction;
	
	@Column(name = "log", length = 1024)
	@Size(max = 1024)
	private String log;
	
	@Column(name = "local_auth_info_alg", length = 8)
	@Size(max = 8)
	private String localAuthInfoAlg;
	
	@Column(name = "local_auth_info", length = 128)
	@Size(max = 128)
	private String localAuthInfo;
	
	// primary key lato BA mittente delle tabelle SYNC_SEND, etc
	@Column(name = "client_sw_msg_id")
	private Long clientSwMessageId;
	
	@Column(name = "RETRY_CNT")
	private Integer retryCounter;
	
	@Column(name = "csc")
	private String csc;
	
	@Column(name = "qtm")
	private String qtm;
	
}
