package com.cbi.ccr.common.event;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.EventService;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.MSSMessage;
import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.PhyFileDescriptor;
import com.cbi.ccr.dto.mq.command.orch.URI;

@Service
public class MSSEventService extends EventService<MSSMessage>{

	@Override
	public CCRtransportData mapCCRTransportData(MSSMessage message) {
		CCRtransportData transportData = super.commonMapCCRTransportData(message);
		transportData.setServiceType(ServiceType.MSS);
		transportData.setMessageType(message.getMessageType());
		transportData.setMessageLength(message.getMessageLeng().toString());
		transportData.setTur(message.getTur());
		transportData.setUserDataRemote(message.getUdr());
		return transportData;
	}

	@Override
	public PhyFileDescriptor mapPhyFileDescriptDto(MSSMessage clientMessage, String eventType) {
		PhyFileDescriptor fileDescriptor = super.commonMapPhyFileDescriptDto(clientMessage);
		addUdrToPhyFileDescriptor(clientMessage.getUdr(), fileDescriptor);
		
		if(fileDescriptor.getCsc() == null)
			fileDescriptor.setCsc(clientMessage.getCsc());
		
		if(fileDescriptor.getQtm() == null)
			fileDescriptor.setQtm(clientMessage.getQtm());
		
		fileDescriptor.setMsgSize(new Long(clientMessage.getMessageLeng()));
		fileDescriptor.setUdr(clientMessage.getUdr());
		if(clientMessage.getRepoMessageId() == null) {
			fileDescriptor.setUris(null);
		} else {
			fileDescriptor.setUris(Arrays.asList(URI.builder().id(clientMessage.getRepoMessageId()).type("MSG").build()));
		}
		return fileDescriptor;
	}
}
