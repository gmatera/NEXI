package com.cbi.ccr.csw.domain.i;

import javax.validation.constraints.NotNull;

public interface CswInboundEntity extends CswEntity{
	public Long getId();
	
	public String getLocalBaId();
	public String getRemoteBaId();
	public void setComplete(@NotNull Integer complete);
}
