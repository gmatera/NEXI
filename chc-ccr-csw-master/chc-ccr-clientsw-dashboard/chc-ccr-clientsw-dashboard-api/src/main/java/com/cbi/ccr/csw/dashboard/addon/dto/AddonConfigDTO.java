package com.cbi.ccr.csw.dashboard.addon.dto;

import com.cbi.frw.api.dto.GenericDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddonConfigDTO extends GenericDTO{
	private Long id;
	private String localBaId;
	private String remoteBaId;
	private String sndPath;
	private String rcvPath;
	private String sendingPrefix;
	private String errorPrefix;
	private String sentPrefix;
	private String errorDeliverPrefix;
}
