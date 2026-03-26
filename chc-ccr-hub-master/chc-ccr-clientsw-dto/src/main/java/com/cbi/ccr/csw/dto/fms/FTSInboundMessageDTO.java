package com.cbi.ccr.csw.dto.fms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@RequiredArgsConstructor
public class FTSInboundMessageDTO extends FTSMessageDTO{
	
	//private LocalDateTime firstDelTime;
}
