package com.cbi.ccr.dto.mq.command.orch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class URI{
	private String id;
	private String type;
	private String name;
}
