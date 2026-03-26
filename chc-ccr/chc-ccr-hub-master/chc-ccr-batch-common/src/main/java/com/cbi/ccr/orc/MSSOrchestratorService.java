package com.cbi.ccr.orc;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.MSSEventService;
import com.cbi.ccr.domain.MSSMessage;
import com.cbi.ccr.domain.MSSMessageRepository;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;

@Service
public class MSSOrchestratorService extends OrchestratorService<MSSMessage, MSSEventService, MSSMessageRepository>{
	
	public MSSOrchestratorService(MSSEventService eventService, MSSMessageRepository repository) {
		super(eventService, repository);
	}
	
	@Override
	public ChcCommand buildCommand(MSSMessage message) {
		ChcCommand command = super.buildCommonCommand(message);
		addUdrToCommand(command, message.getUdr());
		return command;
	}
	
}
