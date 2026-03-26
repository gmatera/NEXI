package com.cbi.ccr.csw.domain.i;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface CswOutboundEntity extends CswEntity{

	public ClientTaskStatus getCswStatus();
	public void setCswStatus(ClientTaskStatus status);
	public Long getId();
	public Integer getRetryCounter();
	public void setRetryCounter(Integer number);
	public String getStatusInfo();
	public void setStatusInfo(String statusInfo);
//	public Long getVersion();
//	public void setVersion(Long version);
	public void setComplete(@NotNull Integer value);
	public Integer getComplete( );
	public String getLocalAuthInfo();
	public String getLocalAuthInfoAlg();
}
