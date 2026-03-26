package com.cbi.ccr.dto.mq.command.dashboard;

import java.util.List;

import com.cbi.ccr.dto.mq.command.orch.CCRtransportData;
import com.cbi.ccr.dto.mq.command.orch.ChcId;
import com.cbi.ccr.dto.mq.command.orch.ONLMsgDescriptor;
import com.cbi.ccr.dto.mq.command.orch.PhyFileDescriptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChcEvent {
	
	public static final String CCR = "CCR";
	public static final String ORC = "ORC";
	public static final String CSW = "CSW";
	public static final String BATCH_FLOW_RECEIVED = "BatchFlowReceived";
	public static final String BATCH_FLOW_SENT = "BatchFlowSent";
	public static final String POSITIVE_NOTIFICATION_RECEIVED = "POSNotificationReceived";
	public static final String POSITIVE_NOTIFICATION_SENT = "POSNotificationSent";
	public static final String NEGATIVE_NOTIFICATION_RECEIVED = "NEGNotificationReceived";
	public static final String NEGATIVE_NOTIFICATION_SENT = "NEGNotificationSent";
	public static final String ERROR_ON_PROCESSING = "ErrorOnProcessing";
	public static final String COMMAND_VERSION = "1.0";
	public static final String BEGIN = "BEGIN";
	public static final String END = "END";

	@Builder.Default
	private String wfCurrentStation = ChcEvent.CCR;
	private String wfType;
	private String wfPriority;
	private String evType;
	private String evVersion;
	private String evTS;
	private String wfFromStation;
	private String wfToStation;
	private String wfProgress;
	private String svcName;
	private String msgName;
	private List<ChcId> chcIds;
	private ONLMsgDescriptor onlMsgDescriptor;
    private CCRtransportData ccrTransportData;
    private PhyFileDescriptor phyFileDescriptor;
    private List<ChcTrackInfo> chcTrackInfos;
    private List<ErrorDescriptor> errorDescriptors;
	
}
