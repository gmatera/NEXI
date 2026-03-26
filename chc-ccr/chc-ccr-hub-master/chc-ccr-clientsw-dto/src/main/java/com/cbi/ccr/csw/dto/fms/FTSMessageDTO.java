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
public class FTSMessageDTO extends ClientMessageDTO{

	// will be mapped to FTSRecv
	private String vfn;
	private String fileName;
	private Long fileSize;
	private String fileHash;
	private String fileDigestAlg;
	private String fileDigest;
	
}
