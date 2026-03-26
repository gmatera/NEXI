package com.cbi.ccr.dto.mq.command.orch;

import java.util.List;

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
public class ONLMsgDescriptor {

	private String chcPhyMsgId;
	private String canale;
	private String msgName;
	private String idE2E;
	private String csg;
	private String qa;
	private String irs;
	private String csc;
	private String qtm;
	private String cgm;
	private String cmf;
	private String cdf;
	private String cml;
	private String cdl;
	private String udr;
	private List<URI> uris;
		
}
