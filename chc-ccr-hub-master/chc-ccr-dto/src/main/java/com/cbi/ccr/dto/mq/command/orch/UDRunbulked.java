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
public class UDRunbulked{
    private String IDE2E;
    private String CSG;
    private String QA;
    private String IRS;
    private String CSC;
    private String QTM;
    private String PTM;
    private String CGM;
    
    private String SvcPhySender; // CMF
    private String SvcPhyReceiver; // CDF
    private String SvcLogSender; // CML
    private String SvcLogReceiver; // CDL
}
