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
public class MQ1418SecGetNotFilecnf {
	
	public static final String PRIMITIVE_ID = "1418";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String reqBaLoc;
	@NotNull
	private String reqBaRem;
	@NotNull
	private Integer getNotType;//ReqGetNotType
	@NotNull
	private Integer syncFlag; //ReqSyncFlag
	
	private String reqKeyBaLoc;
	private String reqKeyBaRem;
	private String reqVfn;
	// private String reqFiller;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;
	
	@NotNull
	private String result;
	
	private Integer rejReason;
	private String numElemOutOut;
	
	// Il seguente array di campi dipende dal valore NumElemOutOut
	private String baLoc;
	private String baRem;
	private String vfn;
	// private String filler;
	private LocalDateTime hostFirstSubTms;
	private LocalDateTime completeTms;
	// private String filler;
	
	// Dopo l'array le informazioni di autenticazione locale
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
	
}
