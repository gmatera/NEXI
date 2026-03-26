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
import com.cbi.ccr.csw.dashboard.fts.dto.FTSSendDTO;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSSendFilterDTO;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQRepository;
import com.cbi.frw.api.dto.PagedResultDTO;


@RestController
@RequestMapping(ControllerPath.FTSMQ_PREFIX +  ControllerPath.OUTBOUND)
public class FTSMQOutboundController extends CommonSendRcvController<FTSSendDTO, FTSSendFilterDTO, FTSSendMQRepository, FTSSendMQ>{

	@Value ("${outbound_mq_datefilter_column}")
    private String dateColumn;
	
	@Value ("${status_column}")
    private String statusColumn;
	
	public FTSMQOutboundController() {
		super(FTSSendDTO.class, FTSSendMQ.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<FTSSendDTO>> getOutbound(@RequestBody FTSSendFilterDTO filterDTO) {

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