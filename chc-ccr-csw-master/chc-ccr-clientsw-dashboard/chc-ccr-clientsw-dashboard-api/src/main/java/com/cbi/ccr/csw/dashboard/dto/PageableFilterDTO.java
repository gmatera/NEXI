package com.cbi.ccr.csw.dashboard.dto;

import java.util.List;

import com.cbi.ccr.csw.domain.fms.FMSRecvStatus;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.domain.fts.FTSRecvStatus;
import com.cbi.ccr.csw.domain.fts.FTSSendStatus;
import com.cbi.ccr.csw.domain.mss.MSSRecvStatus;
import com.cbi.ccr.csw.domain.mss.MSSSendStatus;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.frw.api.dto.PageableDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageableFilterDTO extends PageableDTO {

	private String startDate;

	private String endDate;
	private String fromTime;
	private String toTime;

	private String dateColumnName;

	private String statusColumnName;

	private List<Long> fileSizeList;
	private List<Integer> msgSizeList;
	private List<String> logicalStateList;
	
	private List<? extends Enum<?>> logicalStateEnumValueList;
	
//	private List<FMSSendStatus> logicalStateFMSDBList;
//	private List<FTSSendStatus> logicalStateFTSDBList;
//	private List<MSSSendStatus> logicalStateMSSDBList;
//	
//	private List<FMSRecvStatus> logicalStateFMSDBInboundList;
//	private List<FTSRecvStatus> logicalStateFTSDBInboundList;
//	private List<MSSRecvStatus> logicalStateMSSDBInboundList;
//	
//	private List<SendStatusMQ> logicalStateMQList;
//	private List<RecvStatusMQ> logicalStateMQInboundList;
	
	private boolean orderBylocaRemoteBa;
	

}
