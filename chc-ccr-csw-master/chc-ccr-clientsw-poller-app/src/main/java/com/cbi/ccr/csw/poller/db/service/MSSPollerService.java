package com.cbi.ccr.csw.poller.db.service;

import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.domain.mss.MSSSendStatus;
import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.poller.db.repository.MSSSendPollerDBRepository;

@Service
public class MSSPollerService extends PollerAbstractService<MSSSend, MSSSendPollerDBRepository>  {

	@Override
	protected String getSubmitPath() {
		return CSWOutboundControllerPath.SUBMIT_MSS;
	}
	@Override
	protected String getSubmitURI() {
		return CSWOutboundControllerPath.BASE;
	}
	@Override
	protected void updateMessageAfterMaxRetry(MSSSend message) {
		message.setStatus(MSSSendStatus.SENDING_ERROR);
		message.setStsCode(MSSSendStatus.SENDING_ERROR.getStsCode());
	}
	
}
