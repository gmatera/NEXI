package com.cbi.ccr.csw.service.common;

import java.util.Base64;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Service;

import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;

@Service
public class LauService {
	
	public void checkLAU(String localAuthInfo, String localAuthInfoAlg, Boolean lauEnabled, String lauKey, String sb) throws ChcException {
		if(lauEnabled == null) {
			throw new ChcException( I18nService.ERR_MISSING_CONFIGURATION, "Missing lau configuration");
		}
		if(lauEnabled.booleanValue() && lauKey == null) {
			throw new ChcException( I18nService.ERR_MISSING_CONFIGURATION, "Lau key not configured correctly");
		} else if (lauEnabled.booleanValue()) {
			commonHmacCheck(localAuthInfo, localAuthInfoAlg, lauKey, sb);
		}
	}

	private void commonHmacCheck(String localAuthInfo, String localAuthInfoAlg, String lauKey, String sb) throws ChcException {
		if(localAuthInfo != null && localAuthInfoAlg != null) {
			
			String actualHmac = new String(new HmacUtils(HmacAlgorithms.HMAC_SHA_256, lauKey).hmac(sb.getBytes()));
			String entityHmac = new String(Base64.getDecoder().decode(localAuthInfo));
			
			if(!actualHmac.equals(entityHmac)) {
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, "localAuthInfo verification failed");
			}
			
		} else {
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, "localAuthInfo verification failed");
		}
	}
	
}
