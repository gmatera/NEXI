package com.cbi.ccr.csw.dashboard.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefresh {
	private String accessToken;
	private String userName;
	private String tokenType;

}
