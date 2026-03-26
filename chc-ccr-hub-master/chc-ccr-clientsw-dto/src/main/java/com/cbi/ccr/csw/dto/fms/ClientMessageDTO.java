package com.cbi.ccr.csw.dto.fms;

import java.time.LocalDateTime;

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
public class ClientMessageDTO {
	
	private String clientInterface;
	private Long clientSwMessageId;
	private String localBaId;
	private String remoteBaId;
	private String localAuthInfoAlg;
	private String localAuthInfo;
	private LocalDateTime baInsertTimestamp;
	private LocalDateTime tmsReceived;
	private LocalDateTime tmsStartSending;
	private String tur;
	private String udr;
	private String catAppl;
	private String recordFormat;
	private Integer maxRecordLength;
	private String lineSeparator;
	private String codePage;
}
