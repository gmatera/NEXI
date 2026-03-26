package com.cbi.ccr.orchestrator.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
@RequestMapping(path = "/webserver/simulator/restful/**")
public class RestfulWebServerController{

	
	@PostMapping()
	public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		IOUtils.copy(request.getInputStream(), out);
		
		Enumeration<String> headersName = request.getHeaderNames();
		while (headersName.hasMoreElements()) {
			String header = headersName.nextElement();
			log.info("Received header {}:{}", header, request.getHeader(header));
		}
		
		response.addHeader("Content-Type", "application/json");
		
		try(ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())){
			IOUtils.copy(in, response.getOutputStream());
		}
		
		log.info("WEBSERVER Simulator got Restful message \n {}", out.toString());	

	}
	
	@GetMapping
	public String ping() {
		return "ping";
	}

}
