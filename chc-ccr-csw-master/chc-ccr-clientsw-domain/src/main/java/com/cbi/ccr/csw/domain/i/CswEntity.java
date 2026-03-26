package com.cbi.ccr.csw.domain.i;

import javax.validation.constraints.NotNull;

import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface CswEntity {
	
	public String getLocalBaId();
	public void setLocalBaId(@NotNull String localBaId);
	
	public String getRemoteBaId();
	public void setRemoteBaId(@NotNull String remoteBaId);
}
