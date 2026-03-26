package com.cbi.ccr.poller.service;

import org.springframework.stereotype.Service;

import com.cbi.ccr.domain.FMSMessage;
import com.cbi.ccr.domain.FMSMessageRepository;
import com.cbi.ccr.orc.FMSOrchestratorService;
import com.cbi.ccr.outbound.service.FMSOutboundService;

//@Service
public class FMSPollerService
		extends PollerAbstractService<FMSMessage, FMSMessageRepository, FMSOrchestratorService, FMSOutboundService> {


	public FMSPollerService(FMSMessageRepository repo, FMSOrchestratorService orchestratorService,
			FMSOutboundService commonOutboundService) {
		super(repo, orchestratorService, commonOutboundService);
	}

}
