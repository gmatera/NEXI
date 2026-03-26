package com.cbi.ccr.csw.dashboard.fms.dto;

import com.cbi.ccr.csw.dashboard.dto.config.CommonConfigDTO;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.DSNCreationAlgo;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigFMSDTO extends CommonConfigDTO {
	
	// DB interface
	private CodePage sndCodePage;
	private LineSeparator sndLineSeparator;
	private RecordFormat sndRecordFormat;
	private Integer sndMaxRecLength;
	private boolean sndCompletionAlgo;
		
	private CodePage rcvCodePage;
	private LineSeparator rcvLineSeparator;
	private boolean rcvCompletionAlgo;
	private DSNCreationAlgo rcvDnsCreationAlgo;
	private String rcvPath;
	private String rcvDsnPrefix;
	
	
	// TODO to complete with all additional MQ config fields 
	private boolean rcvAutoRead;
	private String rcvPrimConvFormat;
	private String uploadQName;
	private Integer msgSizeDataQueue;
	private boolean mqiPosCreateInd;
	
	private RecordFormat rcvRecordFormat;
	private Integer rcvMaxRecLength;
	private CodePage hubCodePage;
	private LineSeparator hubLineSeparator;
}
