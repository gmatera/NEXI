package com.cbi.ccr.csw.poller.db.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.frw.api.dto.LivenessDTO;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtils;
import com.cbi.frw.http.HttpUtilsNoProxy;

import lombok.NonNull;

@Service
public class InternalPingService {

	@Autowired
	private HttpUtilsNoProxy httpUtilsNoProxy;
	
	public void pingService(@NonNull String url) throws ChcStubException {
		
		// TODO perform internal authentication
		
		HttpResponse<LivenessDTO> repsonse = httpUtilsNoProxy.getRequest(url, null, null, LivenessDTO.class, 5000);
		
		if(repsonse != null) {
			return;
		}
		
		throw new ChcStubException(ErrorMessage.builder()
				.errorCode(I18nCommon.ERR_NETWORK_ERROR.name())
				.httpStatus(404)
				.build());
	}
}
