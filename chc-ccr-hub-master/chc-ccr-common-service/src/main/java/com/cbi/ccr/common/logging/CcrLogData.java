package com.cbi.ccr.common.logging;

import com.cbi.frw.common.logging.LogData;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@JsonPropertyOrder({"level","system","module","function","correlationId","service","id","code","message","udr"})
public class CcrLogData extends LogData{

	private String udr;
}
