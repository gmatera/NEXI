package com.cbi.ccr.common.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDetails {

	private String outcomeCode;
	private String outcomeDescription;
	private String outcomeType;
	private String userFeedback;
	
}
