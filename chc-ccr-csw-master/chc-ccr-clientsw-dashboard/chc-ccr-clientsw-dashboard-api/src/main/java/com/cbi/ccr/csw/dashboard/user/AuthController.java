package com.cbi.ccr.csw.dashboard.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.jwt.JwtResponse;
import com.cbi.ccr.csw.dashboard.jwt.TokenRefresh;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.dashboard.user.dto.SigninRequestDTO;

@RestController
@RequestMapping(ControllerPath.LMI)
public class AuthController {

	@Autowired
	private UserService userService;

	@PostMapping(ControllerPath.SIGN_IN)
	public ResponseEntity<JwtResponse> authenticateUser(@RequestBody SigninRequestDTO signinRequestDTO) {

		return ResponseEntity.ok(userService.authenticateUser(signinRequestDTO));
	}

	@PreAuthorizeRoleUser
	@PostMapping(ControllerPath.REFRESH_TOKEN)
	public ResponseEntity<TokenRefresh> refreshToken(@RequestBody TokenRefresh dto) {
	
		return ResponseEntity.ok(userService.refreshJwtToken(dto.getUserName()));

	}
	
}
