package com.cbi.ccr.csw.mq.domain.i;

import com.cbi.ccr.csw.domain.i.CswInboundWithFileEntity;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;

public interface CswInboundMqWithFileEntity extends CswInboundWithFileEntity{

	public void setStatus(RecvStatusMQ cleanable);
	
	public RecvStatusMQ getStatus();
	
	public Integer getRejectReason();
	
	public String getQueueFileName();

	

	public void setComplete(Integer i);

	public String getVfn();
	
}
