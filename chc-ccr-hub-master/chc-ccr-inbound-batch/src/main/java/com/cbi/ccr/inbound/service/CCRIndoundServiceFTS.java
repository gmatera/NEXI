package com.cbi.ccr.inbound.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.FTSEventService;
import com.cbi.ccr.csw.dto.fms.FTSMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.FTSInterface;
import com.cbi.ccr.domain.FTSMessage;
import com.cbi.ccr.domain.FTSMessageRepository;
import com.cbi.ccr.orc.FTSOrchestratorService;

@Service
public class CCRIndoundServiceFTS extends CCRIndoundServiceCommon<FTSMessage, FTSMessageRepository, FTSMessageDTO, FTSEventService, FTSOrchestratorService> {

	public CCRIndoundServiceFTS(FTSMessageRepository repository, FTSEventService eventService, FTSOrchestratorService orchestratorService) {
		super(repository, FTSMessage.class, eventService, orchestratorService);
	}

	protected boolean isServiceDataCorrect(MessageWrapperDTO wrapper) {
		return wrapper.getType() == ServiceType.FTS || wrapper.getType() == ServiceType.AON;
	}
	
	protected FTSMessage messageExists(FTSMessageDTO dto) {
		// check dei duplicati
		return repository.findFirstByVfnAndLocalBaId(dto.getVfn(), dto.getLocalBaId());
	}
	
	protected void beforeSave(FTSMessage message, MessageWrapperDTO wrapper) {
		
		if (wrapper.getType()==ServiceType.AON) 
			message.setFtsInterface(FTSInterface.FS);
		
		if(message.getFileName() != null)
			message.setFileName(FilenameUtils.getName(message.getFileName()));
		
	}

	@Override
	protected void deleteEntity(FTSMessage entity) {
		repository.delete(entity);
	}
	


}
