package com.cbi.ccr.csw.dashboard.mss.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.fms.controller.CommonOutboundDBController;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSSendDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSSendFilterDTO;
import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.domain.mss.MSSSendDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSSendStatus;
import com.cbi.frw.api.dto.PagedResultDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ControllerPath.MSSDB_PREFIX +  ControllerPath.OUTBOUND)
public class MSSDBOutboundController extends CommonOutboundDBController<MSSSendDTO, MSSSendFilterDTO, MSSSendDBRepository, MSSSend>{

	@Value ("${outbound_db_datefilter_column}")
    private String dateColumn;
	
	@Value ("${status_column}")
    private String statusColumn;
	
	@Autowired
	private MSSSendDBRepository repoMss;
	
	public MSSDBOutboundController() {
		super(MSSSendDTO.class, MSSSend.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<MSSSendDTO>> getOutbound(@RequestBody MSSSendFilterDTO filterDTO) {
		if (filterDTO.getLogicalStateList() != null) {
			List<MSSSendStatus> logicalStateEnumList = filterDTO.getLogicalStateList().stream()
					.map(MSSSendStatus::valueOf).collect(Collectors.toList());
			filterDTO.setLogicalStateEnumValueList(logicalStateEnumList);
		}
		filterDTO.setStatusColumnName(statusColumn);
		filterDTO.setDateColumnName(dateColumn);
		return super.messages(filterDTO);
	}
	
	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.RETRY_INVALID_BA_AND_INVALID_INTERFACE)
	public void retryInvalidBaAndInterface() {
		log.info("Starting retry");
		List<MSSSend> list = repo.findAllByStatus(MSSSendStatus.INVALID_BA);
		list.addAll(repoMss.findAllByStatus(MSSSendStatus.INVALID_INTERFACE));
		retry(MSSSend.class, list);
	}
	
}