package com.cbi.ccr.common.event;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.EventService;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.FTSInterface;
import com.cbi.ccr.domain.FTSMessage;
import com.cbi.ccr.dto.mq.command.dashboard.ChcEvent;
import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.PhyFileDescriptor;
import com.cbi.ccr.dto.mq.command.orch.URI;

@Service
public class FTSEventService extends EventService<FTSMessage>{

	@Override
	public CCRtransportData mapCCRTransportData(FTSMessage message) {
		CCRtransportData transportData = super.commonMapCCRTransportData(message);
		transportData.setVfn(message.getVfn());
		if(message.getFtsInterface() == FTSInterface.FS) {
			transportData.setServiceType(ServiceType.AON);	
		}else {
			transportData.setServiceType(ServiceType.FTS);	
		}
		
		return transportData;
	}

	@Override
	public PhyFileDescriptor mapPhyFileDescriptDto(FTSMessage clientMessage, String eventType) {
		PhyFileDescriptor fileDescriptor = super.commonMapPhyFileDescriptDto(clientMessage);
		fileDescriptor.setMsgSize(clientMessage.getFileSize());
		fileDescriptor.setVfn(clientMessage.getVfn());
		fileDescriptor.setCsc(clientMessage.getCsc());
		fileDescriptor.setQtm(clientMessage.getQtm());
		
		if(clientMessage.getRepoFileId() == null) {
			fileDescriptor.setUris(null);
		} else {
			fileDescriptor.setUris(Arrays.asList(URI.builder().id(clientMessage.getRepoFileId()).type("FILE").name(clientMessage.getFileName()).build()));
		}
		
		if(clientMessage.getFtsInterface().equals(FTSInterface.FS)) {
			if(!eventType.equals(ChcEvent.POSITIVE_NOTIFICATION_RECEIVED) ||
					!eventType.equals(ChcEvent.POSITIVE_NOTIFICATION_SENT) ||
					!eventType.equals(ChcEvent.NEGATIVE_NOTIFICATION_RECEIVED) ||
					!eventType.equals(ChcEvent.NEGATIVE_NOTIFICATION_SENT)) {
			fileDescriptor.setQtm("01");
			fileDescriptor.setCsc("303");
			}
		}
		return fileDescriptor;
	}
}
