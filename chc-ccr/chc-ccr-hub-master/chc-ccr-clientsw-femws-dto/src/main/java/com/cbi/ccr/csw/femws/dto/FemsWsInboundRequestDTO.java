package com.cbi.ccr.csw.femws.dto;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
public class FemsWsInboundRequestDTO {

	private String baId;
	
	@Builder.Default
	private Map<String, String> headers = Collections.emptyMap();
	private String payload;
	
	public boolean isSoap12() {
		if(headers != null) {
			Set<String> keys = headers.keySet();
			for (String h : keys) {
				if(h.equalsIgnoreCase("content-type") && headers.get(h).contains("application/soap")){
					return true;
				}
			}
		}
		
		return false;
	}
}
