package com.cbi.ccr.csw.poller.db.service.mq;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQ;
import com.cbi.ccr.csw.poller.db.repository.mq.MSSSendMQPollerDBRepository;
import com.cbi.ccr.csw.poller.db.service.PollerAbstractService;

@Profile("MQ")
@Service
public class MSSPollerMQService extends PollerAbstractService<MSSSendMQ, MSSSendMQPollerDBRepository>  {

	@Override
	protected String getSubmitPath() {
		return CSWOutboundControllerPath.SUBMIT_MSS;
	}

	@Override
	protected String getSubmitURI() {
		return CSWOutboundControllerPath.BASE_MQ;
	}
	
	@Override
	protected void updateMessageAfterMaxRetry(MSSSendMQ message) {
		// not used
	}
	
}
