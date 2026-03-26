package com.cbi.ccr.orchestrator.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.cbi.frw.common.json.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/orchestrator/simulator/femws")
public class FemWsSimulatorController{

	
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	@PostMapping()
	public void post(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
		
			IOUtils.copy(request.getInputStream(), out);
			
			Map<String, String> headers = new HashMap<>();
			
			Enumeration<String> headersName = request.getHeaderNames();
			while (headersName.hasMoreElements()) {
				String header = headersName.nextElement();
				headers.put(header, request.getHeader(header));
			}
			
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				
				response.addHeader(key, val);
			}
			
			response.addHeader("x-chc-simulator", "true");
	
			
			StringBuilder chcTrackInfoTS = new StringBuilder();
			chcTrackInfoTS.append("T1:");
			chcTrackInfoTS.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
			chcTrackInfoTS.append(";");
			chcTrackInfoTS.append("TC1:");
			chcTrackInfoTS.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
			chcTrackInfoTS.append(";");
			chcTrackInfoTS.append("TC4:");
			chcTrackInfoTS.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
			chcTrackInfoTS.append(";");
				
			
			response.addHeader("x-chc-track-info", chcTrackInfoTS.toString());
			
			try(ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())){
				IOUtils.copy(in, response.getOutputStream());
			}
			response.getOutputStream().flush();
			
			log.info("ORC Simulator got Femws message \n {}", out.toString());	
		}
	}
	
	@GetMapping
	public String ping() {
		return "ping";
	}

}
