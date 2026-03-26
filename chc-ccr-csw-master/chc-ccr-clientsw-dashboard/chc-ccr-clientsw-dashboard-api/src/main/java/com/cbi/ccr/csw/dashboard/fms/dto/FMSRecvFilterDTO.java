package com.cbi.ccr.csw.dashboard.fms.dto;

import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendFilterDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSRecvFilterDTO extends SendFilterDTO  {

	// inseriscili con lo stesso nome dell'entity FMSRecv
	private String statusBA;
	private String status;
//	private Integer statusCodeBA;
//	private Integer statusCodeSync;
	private String udr;
	private String vfn;
	private String fileName;
	private String tur;
}
