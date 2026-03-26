package com.cbi.ccr.csw.dashboard.fts.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.controller.CommonSendRcvController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSRecvDTO;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSRecvFilterDTO;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQRepository;
import com.cbi.frw.api.dto.PagedResultDTO;

@RestController
@RequestMapping(ControllerPath.FTSMQ_PREFIX + ControllerPath.INBOUND)
public class FTSMQInboundController
		extends CommonSendRcvController<FTSRecvDTO, FTSRecvFilterDTO, FTSRecvMQRepository, FTSRecvMQ> {

	@Value("${inbound_mq_datefilter_column}")
	private String dateColumn;

	@Value("${status_column}")
	private String statusColumn;

	public FTSMQInboundController() {
		super(FTSRecvDTO.class, FTSRecvMQ.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<FTSRecvDTO>> getInbound(@RequestBody FTSRecvFilterDTO filterDTO) {

		if (filterDTO.getLogicalStateList() != null) {
			List<RecvStatusMQ> logicalStateEnumList = filterDTO.getLogicalStateList().stream()
					.map(RecvStatusMQ::valueOf).collect(Collectors.toList());
			filterDTO.setLogicalStateEnumValueList(logicalStateEnumList);
		}
		filterDTO.setStatusColumnName(statusColumn);
		filterDTO.setDateColumnName(dateColumn);
		return super.messages(filterDTO);
	}

}