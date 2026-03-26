package com.cbi.ccr.inbound.service;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.MSSEventService;
import com.cbi.ccr.csw.dto.fms.MSSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.MSSMessage;
import com.cbi.ccr.domain.MSSMessageRepository;
import com.cbi.ccr.orc.MSSOrchestratorService;

@Service
public class CCRIndoundServiceMSS extends CCRIndoundServiceCommon<MSSMessage, MSSMessageRepository, MSSMessageDTO, MSSEventService, MSSOrchestratorService> {

	public CCRIndoundServiceMSS(MSSMessageRepository repository, MSSEventService eventService, MSSOrchestratorService orchestratorService) {
		super(repository, MSSMessage.class, eventService, orchestratorService);
	}
	
	protected boolean isServiceDataCorrect(MessageWrapperDTO wrapper) {
		return wrapper.getType() == ServiceType.MSS;
	}
	
	protected MSSMessage messageExists(MSSMessageDTO dto) {
		// check dei duplicati
		return repository.findFirstByMsgIdAndLocalBaId(dto.getMsgId(), dto.getLocalBaId());
	}
	
	protected void beforeSave(MSSMessage message, MessageWrapperDTO wrapper) {
		// not needed
	}

	@Override
	protected void deleteEntity(MSSMessage entity) {
		repository.delete(entity);
	}

}
