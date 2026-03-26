package com.cbi.ccr.csw.service.common.logger;

import com.cbi.frw.common.logging.LogData;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@JsonPropertyOrder({"level","system","module","function","correlationId","service","id","code","message",})
public class CswLogData extends LogData{
	@Builder.Default
	private String localBaId = "";
	@Builder.Default
	private String remoteBaId = "";
	@Builder.Default
	private String udr = "";
	@Builder.Default
	private String vfn = "";
	private Integer retryCount;
	
	public enum LogSystemEnum {
		CSW
	}
	
	public enum ModuleEnum {
		OUTBOUND_BATCH_DB, 
		INBOUND_BATCH_DB, 
		OUTBOUND_MQ, INBOUND_MQ, 
		PRIMITIVE_POOL_MQ, 
		SOAP_INBOUND,
		SOAP_OUTBOUND
	}
	
	public enum ServiceEnum {
		FMS, FTS, MSS, AON
	}

	
}
