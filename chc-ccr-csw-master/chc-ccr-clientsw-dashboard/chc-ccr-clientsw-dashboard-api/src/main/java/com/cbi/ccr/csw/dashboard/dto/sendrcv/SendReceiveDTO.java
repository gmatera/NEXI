package com.cbi.ccr.csw.dashboard.dto.sendrcv;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.frw.api.dto.GenericDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendReceiveDTO extends GenericDTO{
	private Long id;
	private String localBaId;
	private String remoteBaId;
	private ClientTaskStatus cswStatus;
	private Integer retryCounter;
	
	
}
