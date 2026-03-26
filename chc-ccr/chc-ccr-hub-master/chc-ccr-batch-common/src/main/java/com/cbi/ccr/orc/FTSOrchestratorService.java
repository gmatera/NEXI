package com.cbi.ccr.orc;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.FTSEventService;
import com.cbi.ccr.domain.FTSMessage;
import com.cbi.ccr.domain.FTSMessageRepository;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;

@Service
public class FTSOrchestratorService extends OrchestratorService<FTSMessage, FTSEventService, FTSMessageRepository>{
	
	public FTSOrchestratorService(FTSEventService eventService, FTSMessageRepository repository) {
		super(eventService, repository);
	}
	
	
	@Override
	public ChcCommand buildCommand(FTSMessage message) {
		ChcCommand command = super.buildCommonCommand(message);
		addUdrToCommand(command, message.getUdr());
		return command;
	}
	
}
