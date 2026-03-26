package com.cbi.ccr.csw.dashboard.fts.dto;

import java.time.LocalDateTime;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendFilterDTO;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FTSRecvFilterDTO extends SendFilterDTO {

	// use the same name of the entity FTSSend
	private String vfn;
	private String status;
	private String fileName;
	private LocalDateTime baInsertTimestamp;   // DEFAULT sys_extract_utc(systimestamp),
	private RouteInterface ftsInterface;

}
