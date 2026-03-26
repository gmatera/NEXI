package com.cbi.ccr.csw.mq.common.dto.mss;

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
public class MQ1972SecGetMsgcnf {
	
	public static final String PRIMITIVE_ID = "1972";
	@NotNull
	private String id = PRIMITIVE_ID;
	@NotNull
	private String reqBaLoc;

	private String reqBaRem;
	private Integer reqPriority;
	private Integer reqGetMsgType;
	private String reqKeyBALoc;
	private String reqKeyBARem;
	private String reqKeyMsgIdR;
	private Integer reqNumElemOut;
	private byte[] reqFiller;
	private String reqLocalAuthInfoAlg;
	private Integer reqLocalAuthInfoLen;
	private String reqLocalAuthInfo;
	
	@NotNull
	private Integer result;
	
	private Integer rejReason;
	private Integer numElemOutOut;
	
	// I seguenti campi dati costituiscono un array con un numero di elementi pari a NumElemOut.
	private String baLoc;
	private String baRem;
	private Integer priority;
	
	private String msgIdR;
	private Integer udrLen;
	private String udr;
	private String tur;
	private LocalDateTime hostFirstDlvTms;
	private byte[] filler;
	
	// Dopo l'array vengono le informazioni di autenticazione locale.
	private String localAuthInfoAlg;
	private Integer localAuthInfoLen;
	private String localAuthInfo;
}
