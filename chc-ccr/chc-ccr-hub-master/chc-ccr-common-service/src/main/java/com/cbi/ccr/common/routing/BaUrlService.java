package com.cbi.ccr.common.routing;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cbi.ccr.common.jwe.jwt.CCRJweJwtService;
import com.cbi.ccr.common.logging.CcrLog;
import com.cbi.ccr.common.routing.dto.ListaRoutingCSW;
import com.cbi.ccr.common.routing.dto.RoutingClient;
import com.cbi.ccr.domain.BaUrl;
import com.cbi.ccr.domain.BaUrlRepository;
import com.cbi.frw.common.logging.LogLevel;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpUtilsNoProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BaUrlService {

	private Map<String, BaUrl> localCache = new HashMap<>(0);
	
	@Autowired
	private BaUrlRepository baUrlRepository;
	
	@Value("${routing_client_dam_url}")
	private String routingClientDamUrl;
	@Autowired
	private HttpUtilsNoProxy utilsNoProxy;
	@Autowired
	protected CCRJweJwtService ccrJweJwtService;
	
	@PostConstruct
	public void init() {
		refresh();
	}
	
	public synchronized BaUrl findFirstByBaId(String baId){
		
		return localCache.get(baId);
	}
	
	@Scheduled(fixedDelayString =  "${routing_client_reload_millisec}")
	public synchronized void refresh() {
		localCache.clear();
		
		CcrLog.getLogData().setFunction("BaUrlService - refresh");
		CcrLog.getLogData().setCorrelationId(UUID.randomUUID().toString());
		CcrLog.getLogData().setModule("BaUrlService");
		CcrLog.getLogData().setLevel(LogLevel.DEBUG);
		
		if(StringUtils.isEmpty(routingClientDamUrl)) {
			List<BaUrl> list = baUrlRepository.findAll();
			for (BaUrl baUrl : list) {
				localCache.put(baUrl.getBaId(), baUrl);
			}
			CcrLog.getLogData().setMessage("Loading Ba Url from Database");
			CcrLog.debug(log);
		} else {
			try {
				loadFromDAM();
			} catch (ChcStubException e) {
				CcrLog.getLogData().setMessage(String.format("Unable to load Ba Url %s", e.getLocalizedMessage()));
				CcrLog.error(log);
			}
		}
		
		
		if(log.isDebugEnabled()) {
			//log.debug("DAM routing table reloaded");
		}
	}
	
	private void loadFromDAM() throws ChcStubException {

		String jwt = ccrJweJwtService.getJwt();
		
		Map<String, String> headers = new HashMap<>();
		headers.put("requestId", UUID.randomUUID().toString());
		headers.put("requestDate", LocalDateTime.now(ZoneOffset.UTC).toString());
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		
		if(!StringUtils.isEmpty(jwt))
			headers.put("Authorization", jwt);
		
		
		ListaRoutingCSW routingCSW = utilsNoProxy.getRequest(routingClientDamUrl, null, headers, ListaRoutingCSW.class, 20000).getResponse();
		
		for(RoutingClient client: routingCSW.getRoutingClients()) {
			
			BaUrl baUrl = new BaUrl();
			baUrl.setActive(true);
			baUrl.setBaId(client.getBaId());
			baUrl.setClientId(client.getClientId());
			baUrl.setUrl(client.getUrl());
			
			localCache.put(client.getBaId(), baUrl);
		}
		
		CcrLog.getLogData().setMessage("DAM routing table reloaded");
		CcrLog.debug(log);
		
	}
}
