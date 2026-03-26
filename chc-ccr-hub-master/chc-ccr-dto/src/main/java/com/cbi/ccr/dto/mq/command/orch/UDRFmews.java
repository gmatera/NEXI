package com.cbi.ccr.dto.mq.command.orch;

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
public class UDRFmews {
	
	private String ide2e;
	private String csg;
	private String qa;
	private String irs;
	private String csc;
	private String qtm;
	private String ptm;
	private String cgm;
	private String cmf;
	private String cdf;
	private String cml;
	private String cdl;
}
