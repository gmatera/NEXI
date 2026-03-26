package com.cbi.ccr.csw.service.common;

import java.security.interfaces.RSAPublicKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NexiPublicKey {

	private String n;
	private String kid;
    private String kty;
    private String alg;
    private String use;
    private String e;
    	
    private RSAPublicKey publicKey;
}
