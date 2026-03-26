package com.cbi.ccr.csw.dashboard.mss.controller;

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
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSSendDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSSendFilterDTO;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQRepository;
import com.cbi.frw.api.dto.PagedResultDTO;

@RestController
@RequestMapping(ControllerPath.MSSMQ_PREFIX + ControllerPath.OUTBOUND)
public class MSSMQOutboundController
		extends CommonSendRcvController<MSSSendDTO, MSSSendFilterDTO, MSSSendMQRepository, MSSSendMQ> {

	@Value("${outbound_mq_datefilter_column}")
	private String dateColumn;
	
	@Value ("${status_column}")
    private String statusColumn;

	public MSSMQOutboundController() {
		super(MSSSendDTO.class, MSSSendMQ.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<MSSSendDTO>> getOutbound(@RequestBody MSSSendFilterDTO filterDTO) {
		if (filterDTO.getLogicalStateList() != null) {
			List<SendStatusMQ> logicalStateEnumList = filterDTO.getLogicalStateList().stream()
					.map(SendStatusMQ::valueOf).collect(Collectors.toList());
			filterDTO.setLogicalStateEnumValueList(logicalStateEnumList);
		}
		filterDTO.setStatusColumnName(statusColumn);
		filterDTO.setDateColumnName(dateColumn);
		return super.messages(filterDTO);
	}
}