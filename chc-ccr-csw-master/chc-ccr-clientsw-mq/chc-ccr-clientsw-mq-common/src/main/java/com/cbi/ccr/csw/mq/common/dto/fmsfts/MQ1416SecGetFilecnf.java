package com.cbi.ccr.csw.mq.common.dto.fmsfts;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MQ1416SecGetFilecnf {
	
	public static final String PRIMITIVE_ID = "1416";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String reqBaLoc;
	@NotNull
	private String reqBaRem;
	@NotNull
	private Integer  reqGetFileType;
	@NotNull
	private Integer reqSyncFlag;

	private String reqKeyBaLoc;
	private String reqKeyBaRem;
	private String reqVfn;
	// private String reqFiller;
	private String reqNumElemOut;
	// private String reqFiller;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;
	
	@NotNull
	private String result;
	
	private Integer rejReason;
	private String numElemOut;
	
	// il seguente array di campi dipende dal valore NumElemOutOut
	private String baLoc;
	private String baRem;
	private String vfn;
	private Integer adfLen;
	private String adf;
	private Integer udrLen;
	private String udr;
	// private String filler
	private String queueFileName;
	private LocalDateTime hostFirstDlvTms;
	private LocalDateTime ftsEndReceiveTms;
	private LocalDateTime startCreateTms;
	private LocalDateTime endCreateTms;
	// private String filler;
	
	// Dopo l'array le informazioni di autenticazione:
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
}
