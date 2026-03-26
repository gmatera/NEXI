package com.cbi.ccr.csw.mq.domain.i;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.cbi.ccr.csw.domain.i.CswOutboundEntity;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;

public interface CswEntityMq extends CswOutboundEntity{

	public void setStatus(SendStatusMQ sendStatusMQ);
	public SendStatusMQ getStatus();
	public void setAcceptTime(LocalDateTime time);
	public void setSendTime(LocalDateTime time);
	public void setCompleteTime(LocalDateTime time);
	public void setNotifyTime(LocalDateTime time);
	public void setBaProcessTms(LocalDateTime time);
	public void setComplete(Integer complete);
	public void setSendErrorTimestamp(LocalDateTime sendErrorTms);
	// the primitive unique code. eg 1400
	public void setPrimitive(@NotNull String primitive);
	public String getPrimitive();
	public void setRejectReason(Integer rejectReason);
	public Integer getRejectReason();
	public void setPrimitiveError(String primitiveError);
}
