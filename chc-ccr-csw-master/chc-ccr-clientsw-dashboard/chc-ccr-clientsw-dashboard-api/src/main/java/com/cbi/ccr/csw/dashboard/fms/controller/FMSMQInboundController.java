package com.cbi.ccr.csw.dashboard.fms.controller;

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
import com.cbi.ccr.csw.dashboard.fms.dto.FMSRecvDTO;
import com.cbi.ccr.csw.dashboard.fms.dto.FMSRecvFilterDTO;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQRepository;
import com.cbi.frw.api.dto.PagedResultDTO;

@RestController
@RequestMapping(ControllerPath.FMSMQ_PREFIX + ControllerPath.INBOUND)
public class FMSMQInboundController
		extends CommonSendRcvController<FMSRecvDTO, FMSRecvFilterDTO, FMSRecvMQRepository, FMSRecvMQ> {

	@Value("${inbound_mq_datefilter_column}")
	private String dateColumn;
	
	@Value ("${status_column}")
    private String statusColumn;

	public FMSMQInboundController() {
		super(FMSRecvDTO.class, FMSRecvMQ.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<FMSRecvDTO>> getInbound(@RequestBody FMSRecvFilterDTO filterDTO) {

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