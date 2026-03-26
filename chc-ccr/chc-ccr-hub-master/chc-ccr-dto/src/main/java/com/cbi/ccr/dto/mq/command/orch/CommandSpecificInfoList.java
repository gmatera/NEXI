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
public class CommandSpecificInfoList{
    private String key;
    private String type;
    private String value;
}
