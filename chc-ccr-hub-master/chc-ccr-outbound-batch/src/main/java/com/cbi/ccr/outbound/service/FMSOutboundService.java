package com.cbi.ccr.outbound.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.FMSEventService;
import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.dto.fms.FMSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.ClientMessageStatus;
import com.cbi.ccr.domain.ClientMessageSubStatus;
import com.cbi.ccr.domain.CommonEntity.Direction;
import com.cbi.ccr.domain.FMSMessage;
import com.cbi.ccr.domain.FMSMessageRepository;
import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;
import com.cbi.ccr.dto.mq.command.orch.PhyFileDescriptor;
import com.cbi.ccr.orc.FMSOrchestratorService;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.http.JsonPart;

@Service
public class FMSOutboundService extends CommonOutboundService<FMSMessage,FMSMessageRepository, FMSEventService, FMSOrchestratorService> {
	
	protected FMSOutboundService(FMSMessageRepository repository, FMSEventService eventService, FMSOrchestratorService orchestratorService) {
		super(repository, eventService, orchestratorService);
	}
	
	@Override
	protected String getUri() {
		return CSWInboundControllerPath.RECEIVE_FMS;
	}
	@Override
	protected ServiceType getServiceType() {
		return ServiceType.FMS;
	}

	public FMSMessage buildMessage(ChcCommand command) {
		PhyFileDescriptor descriptor = command.getPhyFileDescriptor();
		CCRtransportData ccRtransportData = command.getCcrTransportData();
		FMSMessage fmsMessage = new FMSMessage();
		fmsMessage.setInsertDate(LocalDateTime.now());
		fmsMessage.setStatus(ClientMessageStatus.RECEIVED);
		fmsMessage.setSubStatus(ClientMessageSubStatus.SENDING_TO_REMOTE_BA);
		fmsMessage.setId(Long.valueOf(descriptor.getChcPhyMsgId()));
		fmsMessage.setChcId(Long.valueOf(command.getChcIds().get(0).getId()));
		fmsMessage.setDirection(Direction.INBOUND);
		fmsMessage.setLocalBaId(ccRtransportData.getLocalBaId());
		fmsMessage.setRemoteBaId(ccRtransportData.getRemoteBaId());
		fmsMessage.setVfn(ccRtransportData.getVfn());
		fmsMessage.setUdr(ccRtransportData.getUserDataRemote());
		fmsMessage.setMessageType(ccRtransportData.getMessageType());
		
		fmsMessage.setCsc(descriptor.getCsc());
		fmsMessage.setQtm(descriptor.getQtm());
		
		if(descriptor.getUris().get(0).getType().equals("FILE")) {
			fmsMessage.setRepoFileId(descriptor.getUris().get(0).getId());
			fmsMessage.setRepoMessageId(descriptor.getUris().get(1).getId());
		} else {
			fmsMessage.setRepoFileId(descriptor.getUris().get(1).getId());
			fmsMessage.setRepoMessageId(descriptor.getUris().get(0).getId());
		}
		
		fmsMessage.setMessageLeng(ccRtransportData.getMessageLength()!=null ? Integer.valueOf(ccRtransportData.getMessageLength()):0);
		fmsMessage.setFileSize(descriptor.getMsgSize() - fmsMessage.getMessageLeng());
		return fmsMessage;
	}
	
	@Override
	protected List<AbstractPart> buildAttachments(FMSMessage fmsMessage) throws ChcStubException, ChcException{
		File file = null;
		File message = null; 
		
		
		file = repoStub.download(fmsMessage.getRepoFileId(), ccrJweJwtService.getJwt());
		FilePart attachmentFile = new FilePart("file", file, "file.bin");
		UUID wrapperKey = UUID.randomUUID();
		
		message = repoStub.download(fmsMessage.getRepoMessageId(), ccrJweJwtService.getJwt());
		FilePart attachmentMessage = new FilePart("message", message, "message.bin");
		
		FMSInboundMessageDTO fmsMessageDTO = mapper.map(fmsMessage, FMSInboundMessageDTO.class);
		//INVERT LOCALBA AND REMOTEBA
		fmsMessageDTO.setLocalBaId(fmsMessage.getRemoteBaId());
		fmsMessageDTO.setRemoteBaId(fmsMessage.getLocalBaId());
		
		String encryptedMsgBody = encryptionUtil.encrypt(JSON.toJson(fmsMessageDTO), getEncryptionServiceKey(wrapperKey));
		
		MessageWrapperDTO wrapper = new MessageWrapperDTO();
		wrapper.setEncryptedBody(encryptedMsgBody);
		wrapper.setWrapperKey(wrapperKey);
		wrapper.setFileId(UUID.fromString(fmsMessage.getRepoFileId()));
		wrapper.setMessageId(UUID.fromString(fmsMessage.getRepoMessageId()));
		wrapper.setType(ServiceType.FMS);
		
		JsonPart messageWrapper = new JsonPart("wrapper", wrapper);
		
		fmsMessage.setTmsStartSending(LocalDateTime.now());
		fmsMessageDTO.setTmsStartSending(fmsMessage.getTmsStartSending());
		fmsMessageDTO.setTmsReceived(fmsMessage.getTmsReceived());
		
		return Arrays.asList(attachmentMessage, attachmentFile, messageWrapper);
	}
	

}
