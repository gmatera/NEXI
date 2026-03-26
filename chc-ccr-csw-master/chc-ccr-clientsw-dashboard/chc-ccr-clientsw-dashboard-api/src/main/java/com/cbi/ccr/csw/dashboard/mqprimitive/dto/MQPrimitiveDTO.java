package com.cbi.ccr.csw.dashboard.mqprimitive.dto;

import com.cbi.frw.api.dto.GenericDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MQPrimitiveDTO extends GenericDTO{
	
	private String id;
	private String mqChannel;
	private String primitive;
	private String queueName;
	private Boolean toLoad;
	

}
