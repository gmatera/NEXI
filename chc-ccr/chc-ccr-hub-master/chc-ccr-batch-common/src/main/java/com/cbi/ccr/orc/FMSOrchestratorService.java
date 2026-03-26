package com.cbi.ccr.orc;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.FMSEventService;
import com.cbi.ccr.domain.FMSMessage;
import com.cbi.ccr.domain.FMSMessageRepository;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;

@Service
public class FMSOrchestratorService extends OrchestratorService<FMSMessage, FMSEventService, FMSMessageRepository>{
	
	public FMSOrchestratorService(FMSEventService eventService, FMSMessageRepository repository) {
		super(eventService, repository);
	}
	
	@Override
	public ChcCommand buildCommand(FMSMessage message) {
		ChcCommand command = super.buildCommonCommand(message);
		addUdrToCommand(command, message.getUdr());
		return command;
	}
	
}
