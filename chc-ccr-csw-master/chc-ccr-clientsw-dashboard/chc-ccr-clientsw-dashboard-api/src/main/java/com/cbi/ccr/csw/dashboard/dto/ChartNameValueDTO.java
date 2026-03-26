package com.cbi.ccr.csw.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChartNameValueDTO {

	@NonNull
	private String name;
	@NonNull
	private Long value;
}
