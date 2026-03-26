package com.cbi.ccr.csw.dashboard.test.fms.controller;

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
import com.cbi.frw.common.json.JSON;

@SpringBootTest(classes = { ConfigT.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
public class FMSConfigControllerTest extends DashBaseTest{

	
	private MockMvc mockMvc;

	@Autowired
	public FMSConfigControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	@Test
	@DisplayName("FMS Configuration : get FMS COnfiguration")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void fmsConfiguration() throws Exception {
				
		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get(ControllerPath.FMSDB_PREFIX+ControllerPath.CONFIG)
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
	@Test
	@DisplayName("FMS Configuration : save or update Test")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void saveOrUpdateTest() throws Exception {
				

		AddonConfigDTO addonConfigDto = new AddonConfigDTO();
		addonConfigDto.setLocalBaId("localBaId");
		addonConfigDto.setRemoteBaId("remoteBaId");
		
//		
//		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(ControllerPath.FMS_PREFIX+ControllerPath.SAVE)
//				.content(JSON.toJson(addonConfigDto)).contentType(MediaType.APPLICATION_JSON));
//		
//		 resultActions.
//         andExpect(MockMvcResultMatchers.status().isOk()).
//         andDo(MockMvcResultHandlers.print());
//		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
}
