package com.cbi.ccr.csw.dashboard.test.addon.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import com.cbi.ccr.csw.dashboard.addon.dto.AddonConfigDTO;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.test.DashBaseTest;

@SpringBootTest(classes = { ConfigT.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
public class AddonControllerTest extends DashBaseTest{
	
	private MockMvc mockMvc;

	@Autowired
	public AddonControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	@Test
	@DisplayName("Add-on : Get All Configurations")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void getAllConfigurationTest() throws Exception {
				
		List<AddonConfigDTO> configList = Arrays.asList(new AddonConfigDTO(1l,"LocalBaId", "RemoteBaId",null,null,null,null,null,null)); // 

		
		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get(ControllerPath.ADDON_PREFIX+ControllerPath.CONFIG)
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
	
	@Test
	@DisplayName("Add-on : save or update Test")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void saveOrUpdateTest() throws Exception {
			
//		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(ControllerPath.ADDON_PREFIX+ControllerPath.SAVE)
//				.content(JSON.toJson(new AddonConfigDTO(1l,"LocalBaId", "RemoteBaId",null,null,null,null,null,null))).contentType(MediaType.APPLICATION_JSON));
//		
//		 resultActions.
//         andExpect(MockMvcResultMatchers.status().isOk()).
//         andDo(MockMvcResultHandlers.print());
//		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}

	
	@Test
	@DisplayName("Add-on : delete Test")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void deleteTest() throws Exception {
				
//		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(ControllerPath.ADDON_PREFIX+ControllerPath.DELETE+"/1")
//				.contentType(MediaType.APPLICATION_JSON));
//		
//		 resultActions.
//         andExpect(MockMvcResultMatchers.status().isOk()).
//         andDo(MockMvcResultHandlers.print());
//		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
}
