package com.cbi.ccr.csw.femws.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.cbi.ccr.csw.femws.dto.FemsWsInboundRequestDTO;
import com.cbi.ccr.csw.femws.service.utils.SOAPMessagesUtils;
import com.cbi.ccr.csw.service.common.CSWCommonService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FemsWsService extends CSWCommonService{
	
	@Value("${ccr_host_online}")
	protected String ccrHost;	

	@Autowired
	protected SOAPMessagesUtils soapMessagesUtils;
	
//	protected void logMessage(FemsWsInboundRequestDTO respDTO) {
//		StringBuilder sb = new StringBuilder();
//		
//		for (Map.Entry<String, String> entry : respDTO.getHeaders().entrySet()) {
//			String key = entry.getKey();
//			String val = entry.getValue();
//			sb.append(key);
//			sb.append(":");
//			sb.append(val);
//			sb.append("\n");
//		}
//		log.info("\n ------------------RECEIVE FROM CCR--------------------------------");
//		log.info("\n FEMWS message received from CCR BA_ID:{} \n headers:\n{} \n payload:\n{}", respDTO.getBaId(), sb.toString(), respDTO.getPayload());
//		log.info("\n ------------------------------------------------------------------");
//
//	}
}
