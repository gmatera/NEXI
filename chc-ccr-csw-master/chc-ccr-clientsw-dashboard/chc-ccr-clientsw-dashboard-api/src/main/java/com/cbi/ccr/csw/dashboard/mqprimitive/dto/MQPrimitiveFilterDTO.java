package com.cbi.ccr.csw.dashboard.mqprimitive.dto;

import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MQPrimitiveFilterDTO  extends PageableFilterDTO{
	
	private String id;
	private String mqChannel;
	private String primitive;
	private String queueName;
	private boolean orderBylocaRemoteBa = false;
}
