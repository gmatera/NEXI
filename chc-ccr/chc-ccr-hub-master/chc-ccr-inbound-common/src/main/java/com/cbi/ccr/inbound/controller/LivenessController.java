package com.cbi.ccr.inbound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cbi.frw.api.dto.InternalControllerPath;
import com.cbi.frw.api.dto.LivenessDTO;

@Controller
@RequestMapping(path = "/ccr")
public class LivenessController {

	@GetMapping(path = InternalControllerPath.LIVENESS)
	public ResponseEntity<LivenessDTO> liveness() {
		
		return ResponseEntity.ok(new LivenessDTO());
	}
}
