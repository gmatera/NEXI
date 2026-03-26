package com.cbi.ccr.dto.mq.command.orch;

import com.cbi.ccr.csw.dto.fms.ServiceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class CCRtransportData {
	
    private ServiceType serviceType;
    
	@NonNull
    private String localBaId;
	@NonNull
    private String remoteBaId;
	
    private String vfn;
    private String userDataRemote;
    private String tur;
    private String messageType;
    private String messageLength;
}
