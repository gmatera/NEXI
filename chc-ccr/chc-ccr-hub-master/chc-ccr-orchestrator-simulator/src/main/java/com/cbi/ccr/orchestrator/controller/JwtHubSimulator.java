package com.cbi.ccr.orchestrator.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.frw.encryption.dto.CryptoHubRequestDTO;
import com.cbi.frw.encryption.dto.CryptoHubResponseDTO;
import com.cbi.frw.encryption.dto.TokenResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/chc/oauth/ccr/token")
public class JwtHubSimulator {
	

	@PostMapping(consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<TokenResponse> getKey() {
		
		
		TokenResponse response = new TokenResponse();
		response.setAccessToken("jwt-token");
		response.setExpireDate(LocalDateTime.now().plusDays(60));
		response.setExpiresIn(500000000000L);
		response.setTokenType("SIMULATOR");
		return ResponseEntity.ok(response);
	}
}
