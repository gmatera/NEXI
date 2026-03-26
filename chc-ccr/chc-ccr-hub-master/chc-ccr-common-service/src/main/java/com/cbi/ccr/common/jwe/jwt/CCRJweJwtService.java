package com.cbi.ccr.common.jwe.jwt;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.dto.TokenResponse;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtils;
import com.cbi.frw.http.HttpUtilsNoProxy;
import com.cbi.frw.jwe.JweUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CCRJweJwtService {

	@Getter
	@Value("${enable_security}")
	private boolean securityEnabled;
	@Getter		
	@Value("${nexi-oauth-token-url}")
	private String nexiOauthTokenUrl;
	
	@Value("${ccr-client-id}")
	private String clientId;
	
	@Value("${ccr-client-secret}")
	private String clientSecret;
	
	@Getter
	@Value("${ccr_public_key}")
	private String ccrPublicKey;
	
	@Value("${ccr_private_key}")
	private String ccrPrivateKey;
	
	private TokenResponse jwt;
	@Autowired
	private HttpUtilsNoProxy httpUtilsNoProxy;
	
	public static final String PROP_TOKEN_HEADER = "Authorization";
	public static final String BEARER = "Bearer ";

	private RSAPrivateKey privateKey;
	
	@PostConstruct
	private void init() throws ChcStubException {
		if(securityEnabled) {
			log.info(Color.g("Security enabled"));
			this.jwt = authenticate();
			log.info("token: {}", JSON.toJson(jwt));
		} else {
			log.info(Color.r("Security is not enabled"));
		}
	}
	
	public String getJwt() throws ChcStubException {
		
		if(!securityEnabled)
			return null;
		
		if(this.jwt == null || jwt.isExpired())
			this.jwt = authenticate();
		return BEARER + this.jwt.getAccessToken();
	}
	
	private TokenResponse authenticate() throws ChcStubException {

		Map<String, String> headers = new HashMap<>();
		headers.put(HttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded");

		Map<String, String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("grant_type", "client_credentials");
		params.put("client_secret", clientSecret);
		params.put("scopes", "Tech_CHC_CCRServer");
		
		
		HttpResponse<TokenResponse> response = httpUtilsNoProxy.postRequest(nexiOauthTokenUrl, params, headers, TokenResponse.class, 1000);
		response.getResponse().setExpireDate(LocalDateTime.now().plusSeconds(response.getResponse().getExpiresIn()));
		return response.getResponse();

	}
	
	public RSAPrivateKey getCCRPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		if(privateKey == null) {
			privateKey = JweUtils.getRSAPrivateKey(this.ccrPrivateKey);
		}
		return privateKey;
	}
}
