package com.cbi.ccr.csw.dashboard.mss.dto;

import java.time.LocalDateTime;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendFilterDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MSSRecvFilterDTO extends SendFilterDTO{
	

	// use the same name of the entity MSSSend
	private String msgId;
	private String status;
	private Integer messageLeng;
	private LocalDateTime baInsertTimestamp;
	private String tur;
	private String udr;
	private String remoteRef;



}
