package com.cbi.ccr.csw.poller.db.service.mq;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.poller.db.repository.mq.FTSSendMQPollerDBRepository;
import com.cbi.ccr.csw.poller.db.service.PollerAbstractService;

@Profile("MQ")
@Service
public class FTSPollerMQService extends PollerAbstractService<FTSSendMQ, FTSSendMQPollerDBRepository> {

	@Override
	protected String getSubmitPath() {
		return CSWOutboundControllerPath.SUBMIT_FTS;
	}
	@Override
	protected String getSubmitURI() {
		return CSWOutboundControllerPath.BASE_MQ;
	}
	@Override
	protected void updateMessageAfterMaxRetry(FTSSendMQ message) {
		// not used
	}

}
