package com.cbi.ccr.csw.dashboard.addon.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddonConfigListDTO {
	private List<AddonConfigDTO> configList;
}
