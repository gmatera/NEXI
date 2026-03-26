package com.cbi.ccr.common.event;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.EventService;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.FMSMessage;
import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.PhyFileDescriptor;
import com.cbi.ccr.dto.mq.command.orch.URI;

@Service
public class FMSEventService extends EventService<FMSMessage>{

	@Override
	public CCRtransportData mapCCRTransportData(FMSMessage message) {
		CCRtransportData transportData = super.commonMapCCRTransportData(message);
		transportData.setServiceType(ServiceType.FMS);
		transportData.setMessageType(message.getMessageType());
		if(message.getMessageLeng() != null)
			transportData.setMessageLength(message.getMessageLeng().toString());
		transportData.setTur(message.getTur());
		transportData.setUserDataRemote(message.getUdr());
		transportData.setVfn(message.getVfn());
		return transportData;
	}

	@Override
	public PhyFileDescriptor mapPhyFileDescriptDto(FMSMessage clientMessage, String eventType) {
		PhyFileDescriptor fileDescriptor = super.commonMapPhyFileDescriptDto(clientMessage);
		addUdrToPhyFileDescriptor(clientMessage.getUdr(), fileDescriptor);
		
		if(fileDescriptor.getCsc() == null)
			fileDescriptor.setCsc(clientMessage.getCsc());
		
		if(fileDescriptor.getQtm() == null)
			fileDescriptor.setQtm(clientMessage.getQtm());
		
		if(clientMessage.getFileSize() != null && clientMessage.getMessageLeng() != null)
			fileDescriptor.setMsgSize(sumSize(clientMessage.getFileSize(), clientMessage.getMessageLeng()));
		fileDescriptor.setUdr(clientMessage.getUdr());
		fileDescriptor.setVfn(clientMessage.getVfn());
		
		if((clientMessage.getRepoFileId() == null && clientMessage.getRepoMessageId() == null)) {
			fileDescriptor.setUris(null);
		} else {
			fileDescriptor.setUris(Arrays.asList(URI.builder().id(clientMessage.getRepoFileId()).type("FILE").name(clientMessage.getFileName()).build(),
					URI.builder().id(clientMessage.getRepoMessageId()).type("MSG").build()));
		}
		
		return fileDescriptor;
	}
}
