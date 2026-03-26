package com.cbi.ccr.csw.femws.inbound.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.femws.ConfigurationFemsWs;
import com.cbi.ccr.csw.domain.csw.config.femws.ConfigurationFemsWsRepository;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundRequestDTO;
import com.cbi.ccr.csw.femws.dto.FemsWsInboundResponseDTO;
import com.cbi.ccr.csw.femws.inbound.I18nInboundFemws;
import com.cbi.ccr.csw.femws.service.FemsWsService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpUtils;
import com.cbi.frw.http.HttpUtilsNoProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FemwsInboundService extends FemsWsService{
	@Value("${csw_version}")
	private String cswVersion;

	@Autowired
	private ConfigurationFemsWsRepository configurationFemsWsRepository;
	
	@Autowired
	private HttpUtilsNoProxy httpUtilsNoProxy;
		
	public FemsWsInboundResponseDTO processInbound(FemsWsInboundRequestDTO messageDTO) throws ChcException, ChcStubException {
		
		List<ConfigurationFemsWs> configurationFemsWs = 
				configurationFemsWsRepository.findByBaId(messageDTO.getBaId());
				
		try{
			
			if(configurationFemsWs.isEmpty()) {
				throw new ChcException(I18nInboundFemws.BAID_CONFIGURATION_NOT_FOUND);
			}

			if(log.isDebugEnabled()) {
				CswLog.debug(log, String.format("processing request: %s", JSON.toJson(messageDTO)));
			}
			
			// posting to the targer WebServer
			return sendToWebServer(messageDTO, configurationFemsWs.get(0));
			
		} catch (ChcStubException e) {
			CswLog.error(log, String.format("Error sending to the target WebServer: %s",e.toString()));
			throw e;
		}catch (Exception e) {
			CswLog.error(log, String.format("Generic Error processInbound: %s",e.toString()));
			throw new ChcException(I18nCommon.ERR_GENERIC, e.toString());
		}
		
	}
	
	private FemsWsInboundResponseDTO sendToWebServer(FemsWsInboundRequestDTO messageDTO, ConfigurationFemsWs configurationFemsWs) throws IOException, ChcStubException {
		
		String webServer = configurationFemsWs.getWebServerUrl();
		String soapAction = configurationFemsWs.getWsSoapAction();
		
		CswLog.getLogData().setFunction("sendToWebServer");
		CswLog.info(log, String.format("sending message to: %s",webServer));

		if(messageDTO.getHeaders().get("SOAPAction") == null) {
			messageDTO.getHeaders().put("SOAPAction", soapAction);
		}
		
		messageDTO.getHeaders().keySet().removeIf(key -> "Content-Length".equalsIgnoreCase(key));
		
		
		// posting to the targer WebServer
		try(CloseableHttpClient client = httpUtilsNoProxy.httpClientFactory(20000); 
				ByteArrayInputStream bis = new ByteArrayInputStream(messageDTO.getPayload().getBytes());
				CloseableHttpResponse response = httpUtilsNoProxy.postLowLevel(client, webServer, messageDTO.getHeaders(), bis);){
			
			Map<String, String> responseHeaders = new HashMap<>();
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				if(headers[i].getName().contains("x-chc") || headers[i].getName().equalsIgnoreCase(HttpUtils.CONTENT_TYPE)) {
					responseHeaders.put(headers[i].getName(), headers[i].getValue());
				}
			}
			
			if(response.getStatusLine().getStatusCode() == 200) {
				
				InputStream in = response.getEntity().getContent();
				try(ByteArrayOutputStream bos = new ByteArrayOutputStream();){
					IOUtils.copy(in, bos);
					
					FemsWsInboundResponseDTO returnBean = FemsWsInboundResponseDTO.builder()
							.headers(responseHeaders)
							.payload(new String(bos.toByteArray(), StandardCharsets.UTF_8))
							.build();
					
					if(log.isDebugEnabled()) {
						CswLog.debug(log, String.format("response received from Soap webserver:: %s", returnBean.getPayload()));
					}
					
					return returnBean;
				}
				
			}else {
				CswLog.error(log, String.format("Error received from Soap webserver:: %s", response.toString()));
				
				return FemsWsInboundResponseDTO.builder()
						.headers(responseHeaders)
						.payload(response.toString())
						.build();
			}
		}
	}
	
	
}
