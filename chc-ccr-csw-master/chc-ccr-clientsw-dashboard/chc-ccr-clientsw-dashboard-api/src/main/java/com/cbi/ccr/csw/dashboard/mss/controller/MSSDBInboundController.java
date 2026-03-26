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
import com.cbi.ccr.csw.domain.mss.MSSRecv;
import com.cbi.ccr.csw.domain.mss.MSSRecvDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSRecvStatus;
import com.cbi.frw.api.dto.PagedResultDTO;

@RestController
@RequestMapping(ControllerPath.MSSDB_PREFIX + ControllerPath.INBOUND)
public class MSSDBInboundController
		extends CommonSendRcvController<MSSRecvDTO, MSSRecvFilterDTO, MSSRecvDBRepository, MSSRecv> {

	@Value("${mss_inbound_db_datefilter_column}")
	private String dateColumn;

	@Value("${status_column}")
	private String statusColumn;

	public MSSDBInboundController() {
		super(MSSRecvDTO.class, MSSRecv.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<MSSRecvDTO>> getInbound(@RequestBody MSSRecvFilterDTO filterDTO) {
		if (filterDTO.getLogicalStateList() != null) {
			List<MSSRecvStatus> logicalStateEnumList = filterDTO.getLogicalStateList().stream()
					.map(MSSRecvStatus::valueOf).collect(Collectors.toList());
			filterDTO.setLogicalStateEnumValueList(logicalStateEnumList);
		}
		filterDTO.setStatusColumnName(statusColumn);
		filterDTO.setDateColumnName(dateColumn);
		return super.messages(filterDTO);
	}
}