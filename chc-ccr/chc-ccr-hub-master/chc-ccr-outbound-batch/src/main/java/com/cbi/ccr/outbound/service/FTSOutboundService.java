package com.cbi.ccr.outbound.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cbi.ccr.common.event.FTSEventService;
import com.cbi.ccr.csw.dto.CSWInboundControllerPath;
import com.cbi.ccr.csw.dto.fms.FTSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.MessageWrapperDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.domain.ClientMessageStatus;
import com.cbi.ccr.domain.ClientMessageSubStatus;
import com.cbi.ccr.domain.CommonEntity.Direction;
import com.cbi.ccr.domain.FTSInterface;
import com.cbi.ccr.domain.FTSMessage;
import com.cbi.ccr.domain.FTSMessageRepository;
import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.ChcCommand;
import com.cbi.ccr.dto.mq.command.orch.PhyFileDescriptor;
import com.cbi.ccr.orc.FTSOrchestratorService;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.FilePart;
import com.cbi.frw.http.JsonPart;

@Service
public class FTSOutboundService extends CommonOutboundService<FTSMessage,FTSMessageRepository, FTSEventService, FTSOrchestratorService> {
	
	protected FTSOutboundService(FTSMessageRepository repository, FTSEventService eventService, FTSOrchestratorService orchestratorService) {
		super(repository, eventService, orchestratorService);
	} 

	@Override
	protected String getUri() {
		return CSWInboundControllerPath.RECEIVE_FTS;
	}
	@Override
	protected ServiceType getServiceType() {
		return ServiceType.FTS;
	}

	public FTSMessage buildMessage(ChcCommand command) {
		PhyFileDescriptor descriptor = command.getPhyFileDescriptor(); 
		CCRtransportData ccRtransportData = command.getCcrTransportData();
		FTSMessage ftsMessage = new FTSMessage();
		ftsMessage.setInsertDate(LocalDateTime.now());
		ftsMessage.setStatus(ClientMessageStatus.RECEIVED);
		ftsMessage.setSubStatus(ClientMessageSubStatus.SENDING_TO_REMOTE_BA);
		ftsMessage.setId(Long.valueOf(descriptor.getChcPhyMsgId()));
		ftsMessage.setChcId(Long.valueOf(command.getChcIds().get(0).getId()));
		ftsMessage.setDirection(Direction.INBOUND);
		ftsMessage.setUdr(ccRtransportData.getUserDataRemote());
		ftsMessage.setLocalBaId(ccRtransportData.getLocalBaId());
		ftsMessage.setRemoteBaId(ccRtransportData.getRemoteBaId());
		
		if(descriptor.getUris().get(0).getType().equals("FILE")) {
			ftsMessage.setFileName(descriptor.getUris().get(0).getName());
		}
		
		ftsMessage.setCsc(descriptor.getCsc());
		ftsMessage.setQtm(descriptor.getQtm());
		
		if(ccRtransportData.getServiceType().equals(ServiceType.AON))
			ftsMessage.setFtsInterface(FTSInterface.FS);
		
		if(ccRtransportData.getServiceType().equals(ServiceType.AON) && ccRtransportData.getVfn() == null) {
			buildVfn(ftsMessage);
		} else {
			ftsMessage.setVfn(ccRtransportData.getVfn());
		}
		
		ftsMessage.setRepoFileId(descriptor.getUris().get(0).getId());
		ftsMessage.setFileSize(descriptor.getMsgSize());
		return ftsMessage;
	}
	
	private void buildVfn(FTSMessage ftsMessage) {
		StringBuilder sb = new StringBuilder();
		String vfn = sb.append(ftsMessage.getLocalBaId().substring(0, 5)).
						append(ftsMessage.getRemoteBaId().substring(0, 5)).
						append(ftsMessage.getLocalBaId().substring(5, 12)).
						append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"))).toString();
		
		ftsMessage.setVfn(vfn);
	}
	
	@Override
	protected List<AbstractPart> buildAttachments(FTSMessage message) throws ChcStubException, ChcException{
		File file = null;
		file = repoStub.download(message.getRepoFileId(), ccrJweJwtService.getJwt());
		FilePart attachmentFile = new FilePart("file", file, "file.bin");
		UUID wrapperKey = UUID.randomUUID();
		
		FTSInboundMessageDTO ftsMessageDTO = mapper.map(message, FTSInboundMessageDTO.class);
		// INVERT LOCALBA AND REMOTEBA
		ftsMessageDTO.setLocalBaId(message.getRemoteBaId());
		ftsMessageDTO.setRemoteBaId(message.getLocalBaId());

		String encryptedMsgBody = encryptionUtil.encrypt(JSON.toJson(ftsMessageDTO), getEncryptionServiceKey(wrapperKey));

		MessageWrapperDTO wrapper = new MessageWrapperDTO();
		wrapper.setWrapperKey(wrapperKey);
		wrapper.setEncryptedBody(encryptedMsgBody);
		wrapper.setType(message.getFtsInterface() == FTSInterface.DB ? ServiceType.FTS : ServiceType.AON);
		wrapper.setFileId(UUID.fromString(message.getRepoFileId()));
		wrapper.setMessageId(null);
		JsonPart messageWrapper = new JsonPart("wrapper", wrapper);
		
		message.setTmsStartSending(LocalDateTime.now());
		ftsMessageDTO.setTmsStartSending(message.getTmsStartSending());
		ftsMessageDTO.setTmsReceived(message.getTmsReceived());
		
		return Arrays.asList(attachmentFile, messageWrapper);
	}
	
}
