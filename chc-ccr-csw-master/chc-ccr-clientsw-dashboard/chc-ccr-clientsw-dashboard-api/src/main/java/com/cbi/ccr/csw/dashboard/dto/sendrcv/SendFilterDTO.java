package com.cbi.ccr.csw.dashboard.dto.sendrcv;

import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SendFilterDTO extends PageableFilterDTO {

	// use the same name of the entity FMSSend
	private String remoteBaId;
	private String localBaId;
	private boolean orderBylocaRemoteBa = true;
	
//	private String fromTime;
//	private String toTime;

}
