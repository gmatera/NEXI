package com.cbi.ccr.inbound.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.FMSEventService;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.FMSMessage;
import com.cbi.ccr.domain.FMSMessageRepository;
import com.cbi.ccr.orc.FMSOrchestratorService;

@Service
public class CCRIndoundServiceFMS extends CCRIndoundServiceCommon<FMSMessage, FMSMessageRepository, FMSMessageDTO, FMSEventService, FMSOrchestratorService> {

	public CCRIndoundServiceFMS(FMSMessageRepository repository, FMSEventService eventService, FMSOrchestratorService orchestratorService) {
		super(repository, FMSMessage.class, eventService, orchestratorService);
	}

	protected boolean isServiceDataCorrect(MessageWrapperDTO wrapper) {
		return wrapper.getType() == ServiceType.FMS;
	}
	
	protected FMSMessage messageExists(FMSMessageDTO dto) {
		// check dei duplicati
		return repository.findFirstByVfnAndUdr(dto.getVfn(), dto.getUdr());
	}
	
	protected void beforeSave(FMSMessage message, MessageWrapperDTO wrapper) {
		
		if(message.getFileName() != null)
			message.setFileName(FilenameUtils.getName(message.getFileName()));
		
		//message.setFtsDelivTime(LocalDateTime.now());
	}

	@Override
	protected void deleteEntity(FMSMessage entity) {
		repository.delete(entity);
	}
	

	

}
