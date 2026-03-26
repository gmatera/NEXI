package com.cbi.ccr.csw.dashboard.fms.controller;

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
import com.cbi.ccr.csw.dashboard.fms.dto.FMSSendDTO;
import com.cbi.ccr.csw.dashboard.fms.dto.FMSSendFilterDTO;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fms.FMSSendDBRepository;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.frw.api.dto.PagedResultDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ControllerPath.FMSDB_PREFIX +  ControllerPath.OUTBOUND)
public class FMSDBOutboundController extends CommonOutboundDBController<FMSSendDTO, FMSSendFilterDTO, FMSSendDBRepository, FMSSend> {

	@Value ("${outbound_db_datefilter_column}")
    private String dateColumn;
	
	@Value ("${status_ba_column}")
    private String statusColumn;
	
	@Autowired
	private FMSSendDBRepository repoFms;

	public FMSDBOutboundController() {
		super(FMSSendDTO.class, FMSSend.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<FMSSendDTO>> getOutbound(@RequestBody FMSSendFilterDTO filterDTO) {
		filterDTO.setStatusColumnName(statusColumn);
		filterDTO.setDateColumnName(dateColumn);
		
		if (filterDTO.getLogicalStateList() != null) {
			List<FMSSendStatus> logicalStateEnumList = filterDTO.getLogicalStateList().stream()
					.map(FMSSendStatus::valueOf).collect(Collectors.toList());
			filterDTO.setLogicalStateEnumValueList(logicalStateEnumList);
		}
		
		return super.messages(filterDTO);
	}
	
	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.RETRY_INVALID_BA_AND_INVALID_INTERFACE)
	public void retryInvalidBaAndInterface() {
		log.info("Starting retry");
		List<FMSSend> list = repoFms.findAllByStatus(FMSSendStatus.INVALID_BA);
		list.addAll(repoFms.findAllByStatus(FMSSendStatus.INVALID_INTERFACE));
		retry(FMSSend.class, list);
	}
	
}