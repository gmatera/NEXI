package com.cbi.ccr.dto.mq.command.orch; 
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; 

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChcCommand{
	
	public static final String PHY_FILE_DESCRIPTOR_GROUP = "PhyFileDescriptor";
	public static final String CCR = "CCR";
	public static final String ORC = "ORC";
	public static final String COMMAND_VERSION = "1.0";
	
    private String cmdName;
    private String cmdVersion;
    private String cmdTS;
    private String wfSenderSt;
    private String wfReceiverSt;
    private String wfType;
    private String wfPriority;
    private String svcPhySender;
    private String svcPhyReceiver;
    private String svcLogSender;
    private String svcLogReceiver;
    private List<ChcId> chcIds;
    private CCRtransportData ccrTransportData;
    private PhyFileDescriptor phyFileDescriptor;
}
