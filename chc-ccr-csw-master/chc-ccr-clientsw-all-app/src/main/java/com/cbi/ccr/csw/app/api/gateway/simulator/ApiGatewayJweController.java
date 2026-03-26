package com.cbi.ccr.csw.app.api.gateway.simulator;

import javax.annotation.security.PermitAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dto.api.gateway.ApiGatewayPath;
import com.cbi.ccr.csw.service.common.JweJwtService;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.encryption.dto.CryptoHubRequestDTO;
import com.cbi.frw.encryption.dto.CryptoHubResponseDTO;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtilsProxy;

@RestController
@RequestMapping(path = ApiGatewayPath.API_GATEWAY_BASE_PATH)
@PermitAll
public class ApiGatewayJweController {
	
	private static final String ORC_SIMULATOR_HOST = "http://localhost:60005";
	private static final String CRYPTO_HUB_PATH = "/chc-cryptohub/rest/getkey";
	
	@Value("${ccr_host_batch}")
	private String ccrHostBatch;
	
	@Autowired
	private HttpUtilsProxy httpUtils;
	
	@Autowired
	private JweJwtService jweJwtService;

	@PostMapping(ApiGatewayPath.CRYPTO_HUB)
	public ResponseEntity<String> cryptoHubGetKey(@RequestBody String requestJWE) {
		try {
			CryptoHubRequestDTO cryptoHubRequest = JSON.fromJson(jweJwtService.getDeserializedDecriptedJWE(requestJWE), CryptoHubRequestDTO.class);
			HttpResponse<CryptoHubResponseDTO> response = httpUtils.postRequestWithBody(ORC_SIMULATOR_HOST + CRYPTO_HUB_PATH, null, cryptoHubRequest,  CryptoHubResponseDTO.class, 10000);
			String responseJWE = jweJwtService.getSerializedEncryptedJWE(JSON.toJson(response.getResponse()));
			return ResponseEntity.ok().body(responseJWE);
			
		} catch (ChcException | ChcStubException e) {
			return ResponseEntity.status(503).body(e.getMessage());
		}
	}
	
}
