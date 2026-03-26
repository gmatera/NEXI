package com.cbi.ccr.csw.dashboard.jwt;

public class JwtResponse {

	private Long id;
	private String accessToken;
	private String tokenType;
	private String role;

	public JwtResponse(Long id, String accessToken,String role) {
		this.id = id;
		this.accessToken = accessToken;
		this.setRole(role);
		this.tokenType = "Bearer ";

	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
