package com.cbi.ccr.csw.dashboard.fts.dto;

import com.cbi.ccr.csw.dashboard.dto.config.CommonConfigDTO;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.DSNCreationAlgo;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigFTSDTO extends CommonConfigDTO {

	private CodePage sndCodePage;
	private LineSeparator sndLineSeparator;
	private RecordFormat sndRecordFormat;
	private Integer sndMaxRecLength;
	
	private CodePage rcvCodePage;
	private LineSeparator rcvLineSeparator;
	private boolean sndCompletionAlgo;
	private boolean rcvCompletionAlgo;
	private DSNCreationAlgo rcvDnsCreationAlgo;
	private String rcvPath;
	private String rcvDsnPrefix;
	
	private String uploadQName;
	private String rcvPrimConvFormat;
	private boolean rcvAutoRead;
	private Integer msgSizeDataQueue;
	private boolean mqiPosCreateInd;

	private RecordFormat rcvRecordFormat;
	private Integer rcvMaxRecLength;
	private CodePage hubCodePage;
	private LineSeparator hubLineSeparator;
	
}
