package com.cbi.ccr.orchestrator.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/orchestrator/simulator/restful/**")
public class RestfulSimulatorController{

	
	@PostMapping()
	public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		IOUtils.copy(request.getInputStream(), out);
		
		Map<String, String> headers = new HashMap<>();
		
		Enumeration<String> headersName = request.getHeaderNames();
		while (headersName.hasMoreElements()) {
			String header = headersName.nextElement();
			headers.put(header, request.getHeader(header));
		}
		
		for(Entry<String, String[]> param: request.getParameterMap().entrySet()) {
			log.info("Parameter: {}, Values: {}", param.getKey(), param.getValue());
		}
		
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			
			response.addHeader(key, val);
		}
		
		response.addHeader("simulator", "true");
		
		try(ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())){
			IOUtils.copy(in, response.getOutputStream());
		}
		response.getOutputStream().flush();
		
		log.info("ORC Simulator got Femws message \n {}", out.toString());	
	}
	
	@GetMapping
	public String ping() {
		return "ping";
	}

}
