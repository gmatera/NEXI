package com.cbi.ccr.csw.dto.fms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSSMessageDTO extends ClientMessageDTO{
	// will be mapped to CCR MSSRecv
	
	private String msgId;
	private String messageType;
	private Integer messageLeng;
	private Integer priority;
    private String remoteRef;
	private String msgDigestAlg;
	private String msgDigest;
	private String netMsgId;
	
}
