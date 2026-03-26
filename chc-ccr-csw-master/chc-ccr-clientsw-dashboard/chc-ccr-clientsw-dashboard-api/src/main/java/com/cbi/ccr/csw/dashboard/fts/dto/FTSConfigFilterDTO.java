package com.cbi.ccr.csw.dashboard.fts.dto;

import com.cbi.ccr.csw.dashboard.dto.config.CommonConfigFilterDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FTSConfigFilterDTO extends CommonConfigFilterDTO {
	private boolean orderBylocaRemoteBa = true;
}
