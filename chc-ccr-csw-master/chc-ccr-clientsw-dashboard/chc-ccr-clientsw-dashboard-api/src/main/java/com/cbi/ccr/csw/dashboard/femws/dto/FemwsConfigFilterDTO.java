package com.cbi.ccr.csw.dashboard.femws.dto;

import com.cbi.ccr.csw.dashboard.dto.config.CommonConfigFilterDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FemwsConfigFilterDTO  extends CommonConfigFilterDTO {

	private String baId;
	private boolean orderBylocaRemoteBa = false;

}
