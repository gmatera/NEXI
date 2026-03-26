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
import com.cbi.ccr.csw.dashboard.mss.dto.MSSRecvDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSRecvFilterDTO;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQRepository;
import com.cbi.frw.api.dto.PagedResultDTO;

@RestController
@RequestMapping(ControllerPath.MSSMQ_PREFIX + ControllerPath.INBOUND)
public class MSSMQInboundController
		extends CommonSendRcvController<MSSRecvDTO, MSSRecvFilterDTO, MSSRecvMQRepository, MSSRecvMQ> {

	@Value("${inbound_mq_datefilter_column}")
	private String dateColumn;
	
	@Value ("${status_column}")
    private String statusColumn;

	public MSSMQInboundController() {
		super(MSSRecvDTO.class, MSSRecvMQ.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<MSSRecvDTO>> getInbound(@RequestBody MSSRecvFilterDTO filterDTO) {
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