package com.cbi.ccr.csw.dashboard.test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cbi.ccr.csw.dashboard.jwt.UserDetailsImpl;
import com.cbi.ccr.csw.dashboard.jwt.UserDetailsServiceImpl;
import com.cbi.ccr.csw.dashboard.user.dto.RoleName;
import com.cbi.ccr.csw.domain.user.UserRepository;
import com.cbi.ccr.csw.domain.user.Users;

public abstract class DashBaseTest{

	@MockBean
	protected UserRepository userRepository;
	@Autowired
	private PasswordEncoder encoder;

	@MockBean
	protected UserDetailsServiceImpl userDetailsServiceImpl;
			
	
	@BeforeEach
	public void setUp() {
		Mockito.when(userDetailsServiceImpl.loadUserByUsername("user")).thenReturn(new UserDetailsImpl(new Users(1L, "user", encoder.encode("users"), "Jack Sparrow", RoleName.ROLE_ADMIN.name(),"","","","")));
		
	}

}
