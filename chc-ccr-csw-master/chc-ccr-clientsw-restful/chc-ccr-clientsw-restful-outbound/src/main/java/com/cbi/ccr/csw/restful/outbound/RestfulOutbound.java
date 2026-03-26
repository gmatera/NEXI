package com.cbi.ccr.csw.restful.outbound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dto.CSWOutboundControllerPath;
import com.cbi.ccr.csw.service.common.JweJwtService;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtils;
import com.cbi.frw.http.HttpUtilsProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(CSWOutboundControllerPath.BASE + CSWOutboundControllerPath.REST_OUTBOUND)
public class RestfulOutbound {

	@Value("${csw_version}")
	private String cswVersion;

	@Value("${rest_traceBody}")
	private String restTraceBody;

	@Value("${orchestrator_restful_endpoint}")
	private String orchestratorRestfulEndpoint;

	@Autowired
	private JweJwtService jweJwtService;
	@Autowired
	private HttpUtilsProxy httpUtilsProxy;
	
	@PostConstruct
	public void init() {
		log.info("RestfulOutbound Initialized");
	}

	@GetMapping
	public void get(HttpServletRequest req, HttpServletResponse response) throws ChcStubException, ChcException {
		sendToHub(req, response, HttpUtils.GET);
	}

	@PostMapping()
	public void post(HttpServletRequest req, HttpServletResponse response) throws ChcStubException, ChcException {
		sendToHub(req, response, HttpUtils.POST);
	}

	@PatchMapping()
	public void patch(HttpServletRequest req, HttpServletResponse response) throws ChcStubException, ChcException {
		sendToHub(req, response, HttpUtils.PATCH);
	}

	@DeleteMapping()
	public void delete(HttpServletRequest req, HttpServletResponse response) throws ChcStubException, ChcException {
		sendToHub(req, response, HttpUtils.DELETE);
	}

	@PutMapping()
	public void put(HttpServletRequest req, HttpServletResponse response) throws ChcStubException, ChcException {
		sendToHub(req, response, HttpUtils.PUT);
	}

	@PostMapping("/ping")
	public void ping(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.SC_OK);
		response.getWriter().print("ping reply " + cswVersion);
		response.flushBuffer();
	}

	private void sendToHub(HttpServletRequest request, HttpServletResponse response, String method)
			throws ChcStubException, ChcException {

		String baseUrl = CSWOutboundControllerPath.BASE + CSWOutboundControllerPath.REST_OUTBOUND;
		String uri = request.getRequestURI();

		String targetPath = arrengeQueryParameters(uri.substring(uri.indexOf(baseUrl) + baseUrl.length() - 1), request.getParameterMap());
		
		Map<String, String> headersToSend = new HashMap<>();

		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			// CHC-420
			if(!"Content-Type".equalsIgnoreCase(header) && !"Content-Length".equalsIgnoreCase(header)) {
				headersToSend.put(header, request.getHeader(header));
			}
		}

		try(ByteArrayOutputStream bos = new ByteArrayOutputStream();){
			try {
				IOUtils.copy(request.getInputStream(), bos);
			} catch (IOException e) {
				throw new ChcStubException(ErrorMessage.builder().errorCode(I18nCommon.ERR_GENERIC.name())
						.httpStatus(HttpStatus.SC_BAD_REQUEST)
						.localizedMessage(String.format("Error reading the body request %s", e.toString())).build());
			}
	
			String serializedEncryptedJWE = getJWE(headersToSend, bos);
	
			headersToSend.put(HttpUtils.CONTENT_TYPE, "application/json");
			
			HttpResponse<String> hubResponse = httpUtilsProxy.sendRequestWithBody(
					String.format("%s/%s", orchestratorRestfulEndpoint, targetPath), 
					headersToSend, serializedEncryptedJWE, String.class, 
					 20000, method);
			
			String jweResponse;
			if(jweJwtService.isSecurityEnabled())
				 jweResponse = jweJwtService.getDeserializedDecriptedJWE(hubResponse.getResponse());
			else 
				jweResponse = hubResponse.getResponse();
			
			if (hubResponse.getHeaders() != null) {
				hubResponse.getHeaders().forEach((h, v) -> {
					if(!"Content-Length".equalsIgnoreCase(h)) {
//						log.info("h: {}, v: {}", h, v);
						response.addHeader(h, v);
					}
				});
			}
			
			try {
				response.getWriter().append(jweResponse);
				response.flushBuffer();
			} catch (IOException e) {
				throw new ChcStubException(ErrorMessage.builder().errorCode(I18nCommon.ERR_GENERIC.name())
						.httpStatus(HttpStatus.SC_SERVICE_UNAVAILABLE)
						.localizedMessage(String.format("Error wrtiting the reponse %s", e.toString())).build());
			}
		} catch (IOException e1) {
			log.error("Error reading InputStream", e1);
		}

	}

	private String getJWE(Map<String, String> headersToSend, ByteArrayOutputStream bos) throws ChcException, ChcStubException {
		if (jweJwtService.isSecurityEnabled()) {
			headersToSend.put(JweJwtService.PROP_TOKEN_HEADER, jweJwtService.getJwt());
			headersToSend.put(HttpUtils.CONTENT_TYPE, JweJwtService.CONTENT_TYPE_PLAIN_TEXT);
			return jweJwtService.getSerializedEncryptedJWE(bos.toString());
		} else {
			return bos.toString();
		}
	}
	
	private String arrengeQueryParameters(String targetPath, Map<String, String[]> parameters) {
		
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		sb.append(targetPath);
		sb.append("?");
		
		for(Entry<String, String[]> param: parameters.entrySet()) {
			
			for(String value: param.getValue()) {
				if(!first)
					sb.append("&");
				
				sb.append(param.getKey());
				sb.append("=");
				sb.append(value);
				first = false;
			}
			
			
		}
		
//		log.info("Query parameters: {}", );
		return sb.toString();
	}

}
