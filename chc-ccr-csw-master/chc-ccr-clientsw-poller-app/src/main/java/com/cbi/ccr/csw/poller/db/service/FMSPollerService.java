package com.cbi.ccr.csw.poller.db.service;

import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fms.FMSSendStatus;
import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.poller.db.repository.FMSSendPollerDBRepository;

@Service
public class FMSPollerService extends PollerAbstractService<FMSSend, FMSSendPollerDBRepository>{

	@Override
	protected String getSubmitPath() {
		return CSWOutboundControllerPath.SUBMIT_FMS;
	}
	@Override
	protected String getSubmitURI() {
		return CSWOutboundControllerPath.BASE;
	}
	@Override
	protected void updateMessageAfterMaxRetry(FMSSend message) {
		message.setStatusCodeBA(FMSSendStatus.SENDING_FAILURE.getStCodeBA());
		message.setStatusCodeSync(FMSSendStatus.SENDING_FAILURE.getStCodeSync());
		message.setStatus(FMSSendStatus.SENDING_FAILURE);
	}

}
