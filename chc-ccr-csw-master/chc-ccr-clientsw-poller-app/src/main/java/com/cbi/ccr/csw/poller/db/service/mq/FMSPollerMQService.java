package com.cbi.ccr.csw.poller.db.service.mq;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.poller.db.repository.mq.FMSSendMQPollerDBRepository;
import com.cbi.ccr.csw.poller.db.service.PollerAbstractService;

@Profile("MQ")
@Service
public class FMSPollerMQService extends PollerAbstractService<FMSSendMQ, FMSSendMQPollerDBRepository>{

	@Override
	protected String getSubmitPath() {
		return CSWOutboundControllerPath.SUBMIT_FMS;
	}
	@Override
	protected String getSubmitURI() {
		return CSWOutboundControllerPath.BASE_MQ;
	}
	@Override
	protected void updateMessageAfterMaxRetry(FMSSendMQ message) {
		// not used
	}

}
