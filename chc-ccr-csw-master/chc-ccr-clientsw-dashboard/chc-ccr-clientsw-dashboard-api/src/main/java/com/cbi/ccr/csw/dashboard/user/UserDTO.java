package com.cbi.ccr.csw.dashboard.user;

import java.util.Set;

import javax.persistence.Column;

import com.cbi.ccr.csw.dashboard.user.dto.RoleName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	
	private Long id;
	private String username;
	private String password;
	private String currentPassword;
	private String fullName;
	private String refreshToken;
	private Set<RoleName> roles; // comma separated
	private String secretAnswerOne;
	private String secretAnswerTwo;
	private String secretResponseOne;
	private String secretResponseTwo;

}
