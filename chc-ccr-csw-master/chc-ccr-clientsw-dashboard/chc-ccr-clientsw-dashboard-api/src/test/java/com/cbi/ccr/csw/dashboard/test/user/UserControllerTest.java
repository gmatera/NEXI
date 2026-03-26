package com.cbi.ccr.csw.dashboard.test.user;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.cbi.ccr.csw.dashboard.ConfigT;
import com.cbi.ccr.csw.dashboard.I18nDashboard;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.jwt.JwtResponse;
import com.cbi.ccr.csw.dashboard.test.DashBaseTest;
import com.cbi.ccr.csw.dashboard.user.UserDTO;
import com.cbi.ccr.csw.dashboard.user.dto.RoleName;
import com.cbi.ccr.csw.dashboard.user.dto.SigninRequestDTO;
import com.cbi.ccr.csw.domain.user.Users;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;

@SpringBootTest(classes = { ConfigT.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
public class UserControllerTest extends DashBaseTest{

	private MockMvc mockMvc;
	
	@Autowired
	public UserControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	
	@Test
	@DisplayName("USER : Sign In Authorized User Test")
	void signInTest() throws Exception {
	          
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(ControllerPath.LMI+ControllerPath.SIGN_IN)
				.content(JSON.toJson(new SigninRequestDTO("user",  "users")))
				.contentType(MediaType.APPLICATION_JSON));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user")) 
				.andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
				.andDo(MockMvcResultHandlers.print());
		System.out.println("response:" + resultActions.andReturn().getResponse().getContentAsString());
				

	}


	@Test
	@DisplayName("USER : Create User Test")
	void CreateUserTest() throws Exception {

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(ControllerPath.LMI+ControllerPath.SIGN_IN)
				.content(JSON.toJson(new SigninRequestDTO("user",  "users")))
				.contentType(MediaType.APPLICATION_JSON));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user")) 
				.andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
				.andDo(MockMvcResultHandlers.print());
		System.out.println("response:" + resultActions.andReturn().getResponse().getContentAsString());
		
		JwtResponse jwtResponse =	JSON.fromJson(resultActions.andReturn().getResponse().getContentAsString(), JwtResponse.class);
		
		
		Mockito.when(userRepository.existsByUsername("testuser")).thenReturn(false);
		UserDTO UserDTO = new UserDTO();
		UserDTO.setUsername("testuser");
		UserDTO.setPassword("testuser");

		HashSet<RoleName> role = new HashSet<>();
		role.add(RoleName.ROLE_ADMIN);

		UserDTO.setRoles(role);

		ResultActions resultCreateUserActions = mockMvc.perform(MockMvcRequestBuilders.post(ControllerPath.USER_PREFIX)
				.header("Authorization", "Bearer "+jwtResponse.getAccessToken())
				.content(JSON.toJson(UserDTO)).contentType(MediaType.APPLICATION_JSON));

		resultCreateUserActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
		System.out.println("response:" + resultCreateUserActions.andReturn().getResponse().getContentAsString());

	}
	
	@Test
	@DisplayName("USER : Create User Forbidden Test")
	@WithMockUser(username="User", roles={"USER"})
	void CreateUserForbiddenTest() throws Exception {

		Mockito.when(userRepository.existsByUsername("testuser")).thenReturn(true);
		UserDTO UserDTO = new UserDTO();
		UserDTO.setUsername("testuser");
		UserDTO.setPassword("testuser");

		HashSet<RoleName> role = new HashSet<>();
		role.add(RoleName.ROLE_ADMIN);

		UserDTO.setRoles(role);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(ControllerPath.USER_PREFIX)
				.content(JSON.toJson(UserDTO)).contentType(MediaType.APPLICATION_JSON));

		resultActions.andExpect(MockMvcResultMatchers.status().isForbidden()).andDo(MockMvcResultHandlers.print());
		System.out.println("response:" + resultActions.andReturn().getResponse().getContentAsString());

	}
	
	@Test
	@DisplayName("USER : Create User Alreay Exists Test")
	@WithMockUser(username="User", roles={"ADMIN"})
	void CreateUserAlreadyExistTest() throws Exception {

		Mockito.when(userRepository.existsByUsername("testuser")).thenReturn(true);
		UserDTO UserDTO = new UserDTO();
		UserDTO.setUsername("testuser");
		UserDTO.setPassword("testuser");

		HashSet<RoleName> role = new HashSet<>();
		role.add(RoleName.ROLE_ADMIN);

		UserDTO.setRoles(role);
				
		 org.assertj.core.api.Assertions.assertThatThrownBy(() -> mockMvc.perform(MockMvcRequestBuilders.post(ControllerPath.USER_PREFIX)
				.content(JSON.toJson(UserDTO))  
				.contentType(MediaType.APPLICATION_JSON))).hasCause(new ChcException(I18nDashboard.ERR_USER_ALREADY_TAKEN));

	}
	
	@Test
	@DisplayName("USER : Create User UnAuthorization Test")
	void CreateUserUnAuthorizationTest() throws Exception {

		String token = "Bearer esshbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjQ3NTk1MjY2LCJleHAiOjE2NDc2ODE2NjZ9.LCDKryvolpEV76MLBk-lpLZRHuYQW74EcldkufVbVYEHFQhOzH3ILl2USDVa7-D6z5WzWJ7hwSLcO10ohN2T1Q";
		Mockito.when(userRepository.existsByUsername("testuser")).thenReturn(true);
	
		HashSet<RoleName> role = new HashSet<>();
		role.add(RoleName.ROLE_ADMIN);

		UserDTO UserDTO = new UserDTO(1l,"testuser","testuser","testuser","testuser","testtokem", role,"","","","");
	
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(ControllerPath.USER_PREFIX)
				.header("Authorization", token)
				.content(JSON.toJson(UserDTO)).contentType(MediaType.APPLICATION_JSON));

		resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized()).andDo(MockMvcResultHandlers.print());
		System.out.println("response:" + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	@DisplayName("USER : Delete User Test")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void deleteUserTest() throws Exception {

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(ControllerPath.USER_PREFIX+"/1")
				.contentType(MediaType.APPLICATION_JSON));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
		System.out.println("response:" + resultActions.andReturn().getResponse().getContentAsString());

	}


	
	@Test
	@DisplayName("USER : Update User Test")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void UpdateUserTest() throws Exception {

		Users user = new Users(1L, "user", "user", "Jack Sparrow", RoleName.ROLE_ADMIN.name(),"","","","");
		Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));

		UserDTO UserDTO = new UserDTO();
		UserDTO.setId(1l);
		UserDTO.setUsername("testuser");
		UserDTO.setPassword("testuser");

		HashSet<RoleName> role = new HashSet<>();
		role.add(RoleName.ROLE_ADMIN);

		UserDTO.setRoles(role);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put(ControllerPath.USER_PREFIX)
				.content(JSON.toJson(UserDTO)).contentType(MediaType.APPLICATION_JSON));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
		System.out.println("response:" + resultActions.andReturn().getResponse().getContentAsString());

	}


}
