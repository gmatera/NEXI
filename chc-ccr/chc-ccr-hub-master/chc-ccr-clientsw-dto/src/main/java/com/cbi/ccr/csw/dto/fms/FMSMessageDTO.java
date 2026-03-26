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
public class FMSMessageDTO extends ClientMessageDTO{

	// will be mapped to CCR FMSMessage
	private String vfn;
	private String fileName;
	private String messageType;
	private Integer messageLeng;
	private Long fileSize;
	private String fileHash;
	private String fileDigestAlg;
	private String fileDigest;
	private String msgDigestAlg;
	private String msgDigest;

	// moved to InboundDTO
//	private LocalDateTime ftsDelivTime; not needed
//	private LocalDateTime crtSubMssTmp;

}
