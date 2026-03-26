package com.cbi.ccr.csw.service.common;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.GlobalProperties;
import com.cbi.ccr.csw.domain.csw.config.GlobalPropertiesRepository;
import com.cbi.ccr.csw.domain.csw.config.PropertiesEnum;
import com.cbi.ccr.csw.dto.api.gateway.ApiGatewayResponseDTO;
import com.cbi.ccr.csw.encryption.LocalEncryptionService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.util.Color;
import com.cbi.frw.encryption.dto.TokenResponse;
import com.cbi.frw.http.ChcStubException;
import com.cbi.frw.http.ErrorMessage;
import com.cbi.frw.http.HttpResponse;
import com.cbi.frw.http.HttpUtils;
import com.cbi.frw.http.HttpUtilsProxy;
import com.cbi.frw.jwe.JweUtils;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionDetails;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.id.Subject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@DependsOn("transactionManager")
public class JweJwtService {

	@Value("${client-software-id}")
	private String clientSoftwareId;

	@Value("${api-gateway-public-key-url}")
	private String apiGatewayPublicKeyUrl;

	@Value("${nexi-oauth-token-url}")
	private String nexiOauthTokenUrl;

	@Getter
	@Value("${enable-security}")
	private boolean securityEnabled;

	@Value("${csw_issued_at_seconds_to_remove}")
	private int cswIssuedAtSeondsToReduce;

	@Value("${csw_not_before_seconds_to_remove}")
	private int cswNotBeforSeondsToReduce;
	
	private boolean useApiGatewaySimulator;

	@Getter
	private RSAPublicKey rsaSignPublicKey;

	@Getter
	private String signPublicKey;

	@Getter
	private RSAPrivateKey rsaSignPrivateKey;

	@Getter
	private String signPrivateKey;

	@Getter
	private RSAPublicKey rsaEncPublicKey;

	@Getter
	private String encPublicKey;

	@Getter
	private RSAPrivateKey rsaEncPrivateKey;

	@Getter
	private String encPrivateKey;

	@Getter
	private List<NexiPublicKey> nexiPublicKeys;

	@Autowired
	private GlobalPropertiesRepository globalRepo;
	
	@Autowired
	private HttpUtilsProxy httpUtilsProxy;

	@Autowired
	private LocalEncryptionService localEncryptionService;
	
	private TokenResponse jwt;

	public static final String PROP_TOKEN_HEADER = "Authorization";
	public static final String BEARER = "Bearer ";
	public static final String CONTENT_TYPE_PLAIN_TEXT = "text/plain";

	@PostConstruct
	private void init() throws ChcException, NoSuchAlgorithmException,
			InvalidKeySpecException, JOSEException, ChcStubException {

		/*
		 * Certificati privati del client Software, sign, sono i certificati per la
		 * signature enc, sono quelli per l'encryption del payload
		 */

		signPublicKey = globalRepo.findFirstByPropertyName(PropertiesEnum.SIGN_PUBLIC_KEY.getLabel()).getValue();
		rsaSignPublicKey = JweUtils.getRSAPublicKey(signPublicKey);

		signPrivateKey = globalRepo.findFirstByPropertyName(PropertiesEnum.SIGN_PRIVATE_KEY.getLabel()).getValue();
		signPrivateKey = localEncryptionService.decrypt(signPrivateKey);
		
		rsaSignPrivateKey = JweUtils.getRSAPrivateKey(signPrivateKey);

		encPublicKey = globalRepo.findFirstByPropertyName(PropertiesEnum.ENC_PUBLIC_KEY.getLabel()).getValue();
		rsaEncPublicKey = JweUtils.getRSAPublicKey(encPublicKey);

		
		encPrivateKey = globalRepo.findFirstByPropertyName(PropertiesEnum.ENC_PRIVATE_KEY.getLabel()).getValue();
		encPrivateKey = localEncryptionService.decrypt(encPrivateKey);
		
		rsaEncPrivateKey = JweUtils.getRSAPrivateKey(encPrivateKey);
		
		GlobalProperties value =  globalRepo.findFirstByPropertyName(PropertiesEnum.USE_LOCAL_API_GATEWAY.getLabel());
		
		if(value == null ) {
			useApiGatewaySimulator = false;
		} else {
			useApiGatewaySimulator = true;
		}
	
		if (securityEnabled) {
			log.info(Color.g("Security enabled"));
			sendRequestForNexiPublicKey();
			this.jwt = sendClientAssertion();
		} else {
			log.info(Color.r("Security is not enabled"));
		}
	}

	private void sendRequestForNexiPublicKey() throws ChcException, NoSuchAlgorithmException, InvalidKeySpecException {
		try {
			this.nexiPublicKeys = new ArrayList<>();

		
			if(!useApiGatewaySimulator) {
				Map<String, String> headers = new HashMap<>();
				HttpResponse<ApiGatewayResponseDTO> response;
				response = httpUtilsProxy.getRequest(apiGatewayPublicKeyUrl, headers, null, ApiGatewayResponseDTO.class,
						10000);
	
				for (int i = 0; i < response.getResponse().getKeys().size(); i++) {
					NexiPublicKey k = new NexiPublicKey();
					k.setAlg(response.getResponse().getKeys().get(i).getAlg());
					k.setE(response.getResponse().getKeys().get(i).getE());
					k.setKid(response.getResponse().getKeys().get(i).getKid());
					k.setKty(response.getResponse().getKeys().get(i).getKty());
					k.setN(response.getResponse().getKeys().get(i).getN());
					k.setUse(response.getResponse().getKeys().get(i).getUse());
	
					BigInteger modulus = new BigInteger(1, fromBase64Url(response.getResponse().getKeys().get(i).getN()));
					BigInteger pubExponent = new BigInteger(fromBase64Url(response.getResponse().getKeys().get(i).getE()));
	
					RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(modulus, pubExponent);
					KeyFactory factory = KeyFactory.getInstance("RSA");
					k.setPublicKey((RSAPublicKey) factory.generatePublic(publicSpec));
					this.nexiPublicKeys.add(k);
				}
			} else {
				log.warn(Color.r("WARNING! Using apigateway Simulator!"));
				/**
				 * For simulation purpose, we cannot use nexiPublicKey, 
				 * so we are using our internal encryption keys
				 */
				
				NexiPublicKey pk = new NexiPublicKey();
				pk.setAlg("RSA");
				pk.setE(rsaEncPublicKey.getPublicExponent().toString());
				pk.setKid(UUID.randomUUID().toString());
				pk.setN(rsaEncPublicKey.getModulus().toString());
				pk.setUse("enc");
				pk.setPublicKey(rsaEncPublicKey);
				
				NexiPublicKey sk = new NexiPublicKey();
				sk.setAlg("RSA");
				sk.setE(rsaSignPublicKey.getPublicExponent().toString());
				sk.setKid(UUID.randomUUID().toString());
				sk.setN(rsaSignPublicKey.getModulus().toString());
				sk.setUse("sig");
				sk.setPublicKey(rsaSignPublicKey);
				
				this.nexiPublicKeys.add(pk);
				this.nexiPublicKeys.add(sk);
				
			}
		} catch (ChcStubException e) {
			throw new ChcException(I18nCommon.ERR_NETWORK_ERROR, e.getLocalizedMessage());
		}
	}

	public RSAPublicKey getNexiEncryptionPublicKey() throws ChcException {
		for (NexiPublicKey key : this.nexiPublicKeys) {
			if (key.getUse().equals("enc"))
				return key.getPublicKey();
		}
		throw new ChcException(I18nCommon.ERR_ENCRYPT, "unable to retrive Nexi public encryption key");
	}
	
	public RSAPublicKey getNexiSignaturePublicKey() throws ChcException {
		for (NexiPublicKey key : this.nexiPublicKeys) {
			if (key.getUse().equals("sig"))
				return key.getPublicKey();
		}
		throw new ChcException(I18nCommon.ERR_ENCRYPT, "unable to retrive Nexi public signature key");
	}

	private byte[] fromBase64Url(String base64Url) {
		String padded = base64Url.length() % 4 == 0 ? base64Url : base64Url + "====".substring(base64Url.length() % 4);
		String base64 = padded.replace("_", "/").replace("-", "+");
		return Base64.getDecoder().decode(base64);
	}

	public String getJwt() throws ChcStubException {
		if (!securityEnabled)
			return "token";

		try {
			if (this.jwt == null || jwt.isExpired())
				this.jwt = sendClientAssertion();
			return BEARER + this.jwt.getAccessToken();
		} catch (JOSEException e) {
			throw new ChcStubException(ErrorMessage.builder()
					.errorCode(I18nCommon.ERR_ENCRYPT.name())
					.httpStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
					.localizedMessage(e.getLocalizedMessage())
					.build());
		}
	}

	private TokenResponse sendClientAssertion() throws JOSEException, ChcStubException {
		SignedJWT signedJWT = getClientAssertion();

		Map<String, String> headers = new HashMap<>();
		headers.put(HttpUtils.CONTENT_TYPE, "application/x-www-form-urlencoded");

		Map<String, String> params = new HashMap<>();
		params.put("client_id", clientSoftwareId);
		params.put("grant_type", "client_credentials");
		params.put("client_assertion_type", "urn/ietf/params/oauth/client-assertion-type/jwt-bearer");
		params.put("client_assertion", signedJWT.serialize(false));
		
		CswLog.debug(log, String.format("Client Assertion POST Request, URL: %s, Headers: %s, Parameters: %s, ", nexiOauthTokenUrl, headers, params));
		
		HttpResponse<TokenResponse> response = httpUtilsProxy.postRequest(nexiOauthTokenUrl, params, headers, TokenResponse.class, 10000);
		response.getResponse().setExpireDate(LocalDateTime.now().plusSeconds(response.getResponse().getExpiresIn()));
		return response.getResponse();

	}

	private SignedJWT getClientAssertion() throws JOSEException {

		/**
		 * static string, maximum 64 characters ([a..z][A..Z][0..9][-,*,|]), represents
		 * the GPA and is mapped to the nexiAccountCHCCompanyID LDAP attribute
		 */
		Issuer issuer = new Issuer(clientSoftwareId);

		/**
		 * Subject: client ID of the specific client installation.
		 */
		Subject subject = new Subject(clientSoftwareId);

		/**
		 * Audience: static string, represents the CHC Hub
		 */
		Audience audience = new Audience("https://intapi.nexi.it");
		ArrayList<Audience> audienceList = new ArrayList<>();
		audienceList.add(audience);

		/**
		 * Expiration date of the assertion, in epoch format to milliseconds (e.g.:
		 * 1649161472965). Assertions submitted after the expiration date will be
		 * rejected. A deadline of 4 minutes in the future is suggested: in any case, no
		 * expiration dates of more than 15 minutes in the future will be accepted.
		 */

		Date exp = new Date(System.currentTimeMillis() + (10 * 60 * 1000));

		/**
		 * Issued At: date of issue of the assertion, in epoch format to milliseconds.
		 * According to the OAUTH specification it would be an optional field, but it
		 * becomes mandatory for Nexi.
		 */
		Date iat = Date.from(
				LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().minusSeconds(cswIssuedAtSeondsToReduce));

		/**
		 * Not Before: in the event of misalignments between the clocks of the GPA
		 * client and Nexi, the claim "iat" may contain a date prior to that on which
		 * the Nexi server receives the assertion. This field indicates the date prior
		 * to which the assertion will be rejected (in epoch format).
		 */
		Date nbf = Date.from(
				LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().minusSeconds(cswNotBeforSeondsToReduce));

		/**
		 * Unique identifier of the assertion (it is suggested to use a GUID). According
		 * to the OAUTH specification it is an optional field, but Nexi requires it as a
		 * mandatory field to avoid reply attacks.
		 */
		JWTID jwtID = new JWTID(UUID.randomUUID().toString());
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS512, JOSEObjectType.JWT, null, null, null, null, null, null,
				null, null, null, true, null, null);

		JWTAssertionDetails assertion = new JWTAssertionDetails(issuer, subject, audienceList, exp, iat, nbf, jwtID,
				null);

		SignedJWT signedJwt = new SignedJWT(header, assertion.toJWTClaimsSet());
		signedJwt.sign(new RSASSASigner(rsaSignPrivateKey));
		return signedJwt;
	}

	private JWSObject getJsonWebSignature(String jsonObject) throws JOSEException {
		JWSHeader header = new JWSHeader(JWSAlgorithm.RS512, JOSEObjectType.JOSE_JSON, "JWE", null, null, null, null, null,
				null, null, null, true, null, null);
		JWSObject jwsObject = new JWSObject(header, new Payload(jsonObject));
		jwsObject.sign(new RSASSASigner(rsaSignPrivateKey));
		return jwsObject;
	}

	@SuppressWarnings("deprecation")
	public String getSerializedEncryptedJWE(String jsonObject) throws ChcException {
		try {
			JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A256CBC_HS512);
			JWEObject jweObject = new JWEObject(header, new Payload(getJsonWebSignature(jsonObject).serialize()));
			
			jweObject.encrypt(new RSAEncrypter(getNexiEncryptionPublicKey()));
			return jweObject.serialize();
		} catch (JOSEException e) {
			throw new ChcException(I18nCommon.ERR_ENCRYPT, e.getLocalizedMessage());
		}
	}

	public String getDeserializedDecriptedJWE(String jwe) throws ChcException  {
		try {
			JWEObject jweObject = JWEObject.parse(jwe);
			jweObject.decrypt(new RSADecrypter(rsaEncPrivateKey));
			JWSObject jwsObject = JWSObject.parse(new String(jweObject.getPayload().toBytes()));
			
			if(getNexiSignaturePublicKey() != null) {
				jwsObject.verify(new RSASSAVerifier(getNexiSignaturePublicKey()));
			}
			
			return jwsObject.getPayload().toString();
		} catch (JOSEException | ParseException e) {
			throw new ChcException(I18nCommon.ERR_DECRYPT, e.getLocalizedMessage());
		}
	}

}
