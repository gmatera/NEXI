package com.cbi.ccr.csw.dto.fms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSSInboundMessageDTO extends MSSMessageDTO{
	// will be mapped to CCR MSSRecv
	
	private String easSubTime;
	private String ferSubTime;
	private String fenDelTime;
	private String ferDelTime;
	
}
