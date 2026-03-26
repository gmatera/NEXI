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
import com.cbi.ccr.csw.dashboard.fms.dto.FMSSendDTO;
import com.cbi.ccr.csw.dashboard.fms.dto.FMSSendFilterDTO;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQRepository;
import com.cbi.frw.api.dto.PagedResultDTO;

@RestController
@RequestMapping(ControllerPath.FMSMQ_PREFIX + ControllerPath.OUTBOUND)
public class FMSMQOutboundController
		extends CommonSendRcvController<FMSSendDTO, FMSSendFilterDTO, FMSSendMQRepository, FMSSendMQ> {

	@Value("${outbound_mq_datefilter_column}")
	private String dateColumn;

	@Value("${status_column}")
	private String statusColumn;

	public FMSMQOutboundController() {
		super(FMSSendDTO.class, FMSSendMQ.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<FMSSendDTO>> getOutbound(@RequestBody FMSSendFilterDTO filterDTO) {

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