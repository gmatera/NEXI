package com.cbi.ccr.dto.mq.command.orch; 
import java.util.List;

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
public class CommandSpecificInfo{
    public String group;
    public List<CommandSpecificInfoList> data;
}
