package com.cbi.ccr.csw.dashboard.dto.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationsListDTO<D extends CommonConfigDTO> {
	
	private List<D> configList;
}
