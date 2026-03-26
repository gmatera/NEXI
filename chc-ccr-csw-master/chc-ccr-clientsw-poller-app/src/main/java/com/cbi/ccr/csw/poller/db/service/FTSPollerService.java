package com.cbi.ccr.csw.poller.db.service;

import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.fts.FTSSendStatus;
import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.poller.db.repository.FTSSendPollerDBRepository;

@Service
public class FTSPollerService extends PollerAbstractService<FTSSend, FTSSendPollerDBRepository> {

	@Override
	protected String getSubmitPath() {
		return CSWOutboundControllerPath.SUBMIT_FTS;
	}
	@Override
	protected String getSubmitURI() {
		return CSWOutboundControllerPath.BASE;
	}
	@Override
	protected void updateMessageAfterMaxRetry(FTSSend message) {
		message.setStatus(FTSSendStatus.EXPORT_ERROR);
		message.setStsCode(FTSSendStatus.EXPORT_ERROR.getStsCode());
	}

}
