package com.cbi.ccr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ccr_mss_message")
public class MSSMessage extends CommonEntity {
	
	@Size(max = 50)
	@Column(name = "repo_message_id", length = 50)
	private String repoMessageId;
	
	@Column(name = "PRIORITY")
	private Integer priority;
	
	@Column(name = "TUR", length = 16)
	@Size(max = 16)
	private String tur;
	
	@Column(name = "MSGSIZE")
	private Integer messageLeng;

	@Column(name = "MSG_TYPE", length = 3, nullable = false)
	@Size(max = 3)
	private String messageType;
	
	@Column(name = "CAT_APPL", length = 4)
	@Size(max = 4)
	private String catAppl;
	
	@Column(name = "MSG_DIGEST_ALG", length = 8)
	@Size(max = 8)
	private String msgDigestAlg;
	
	@Column(name = "MSG_DIGEST", length = 128)
	@Size(max = 128)
	private String msgDigest;
	
	@Column(name = "MSGID", length = 30)
	private String msgId;
	
	@Column(name = "NET_MSGID", length = 16)
	private String netMsgId;
	
	@Column(name = "CORRELATION_ID")
	@Size(max = 30)
	private String correlationId;
}
