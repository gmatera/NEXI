package com.cbi.ccr.csw.femws.dto;

import java.util.Collections;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FemsWsInboundResponseDTO {

	private String baId;
	
	@Builder.Default
	private Map<String, String> headers = Collections.emptyMap();
	private String payload;
}
