package com.cbi.ccr.csw.dashboard.fts.controller;

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
import com.cbi.ccr.csw.dashboard.fms.controller.FMSDBOutboundController;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSSendDTO;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSSendFilterDTO;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.fts.FTSSendDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSSendStatus;
import com.cbi.frw.api.dto.PagedResultDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ControllerPath.FTSDBFS_PREFIX + ControllerPath.OUTBOUND)
public class FTSDBOutboundController extends CommonOutboundDBController<FTSSendDTO, FTSSendFilterDTO, FTSSendDBRepository, FTSSend> {

	@Value("${outbound_db_datefilter_column}")
	private String dateColumn;

	@Value("${status_column}")
	private String statusColumn;
	
	@Autowired
	private FTSSendDBRepository repoFts;

	public FTSDBOutboundController() {
		super(FTSSendDTO.class, FTSSend.class);
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.IN_OUT_LIST)
	public ResponseEntity<PagedResultDTO<FTSSendDTO>> getOutbound(@RequestBody FTSSendFilterDTO filterDTO) {
		if (filterDTO.getLogicalStateList() != null) {
			List<FTSSendStatus> logicalStateEnumList = filterDTO.getLogicalStateList().stream()
					.map(FTSSendStatus::valueOf).collect(Collectors.toList());
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
		List<FTSSend> list = repoFts.findAllByStatus(FTSSendStatus.INVALID_BA);
		list.addAll(repoFts.findAllByStatus(FTSSendStatus.INVALID_INTERFACE));
		retry(FTSSend.class, list);
	}

}