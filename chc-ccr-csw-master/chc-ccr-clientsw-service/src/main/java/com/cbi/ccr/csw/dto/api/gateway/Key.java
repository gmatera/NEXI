package com.cbi.ccr.csw.dto.api.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Key {
	public String kty;
	public String kid;
	public String use;
	public String n;
	public String e;
	public String alg;

}
