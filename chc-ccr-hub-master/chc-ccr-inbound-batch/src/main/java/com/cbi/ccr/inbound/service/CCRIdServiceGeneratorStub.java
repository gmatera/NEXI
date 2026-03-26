package com.cbi.ccr.inbound.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.ccr.common.jwe.jwt.CCRJweJwtService;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.dto.IdGeneratorServicePath;
import com.cbi.ccr.dto.Uids;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtilsNoProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CCRIdServiceGeneratorStub {

	@Value("${chc_id_generator_service_url}")
	private String idGeneratorServiceUrl;
	
	@Autowired
	private CCRJweJwtService jweJwtService;
	
	@Autowired
	private HttpUtilsNoProxy httpUtilsNoProxy;
	
	public String getId() throws ChcStubException {
		Map<String, String> headers = new HashMap<>();
		headers.put("x-request-id", UUID.randomUUID().toString());
		headers.put("size", "1");
		
		if(jweJwtService.isSecurityEnabled())
			headers.put(CCRJweJwtService.PROP_TOKEN_HEADER, jweJwtService.getJwt());
		
		CcrLog.getLogData().setMessage(String.format("Calling id generator service at %s", idGeneratorServiceUrl + IdGeneratorServicePath.ID_GENERATOR_SERVICE_PATH));
		CcrLog.debug(log);

		HttpResponse<Uids> response = httpUtilsNoProxy.getRequest(idGeneratorServiceUrl + IdGeneratorServicePath.ID_GENERATOR_SERVICE_PATH, null, headers, Uids.class, 1000);
		
		String id = response.getResponse().getUids()[0].getId();
		CcrLog.getLogData().setId(id);
		
		return id;
	}
}
