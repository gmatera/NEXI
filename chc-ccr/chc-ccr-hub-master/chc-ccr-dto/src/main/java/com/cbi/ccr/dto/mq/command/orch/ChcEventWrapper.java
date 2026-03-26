package com.cbi.ccr.dto.mq.command.orch;

import com.cbi.ccr.dto.mq.command.dashboard.ChcEvent;

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
public class ChcEventWrapper {

	private ChcEvent chcEvent;
}
