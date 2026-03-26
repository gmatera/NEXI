package com.cbi.ccr.csw.dashboard.test.dashboard;

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
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.test.DashBaseTest;

@SpringBootTest(classes = { ConfigT.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
public class DashboardControllerTest extends DashBaseTest{
	
	private MockMvc mockMvc;

	@Autowired
	public DashboardControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	@Test
	@DisplayName("DashBoard : Get Monthly Messages InBound Test")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void getMonthlyMessagesInboundTest() throws Exception {
				
		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get(ControllerPath.DASH+ControllerPath.CHART_MESSAGE_BY_MONTH_INBOUND)
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}

    
	@Test
	@DisplayName("DashBoard : Get Monthly Messages OutBound Test")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void getMonthlyMessagesOutboundTest() throws Exception {
				

		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get(ControllerPath.DASH+"/monthlyMessageOutbound")
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
	@Test
	@DisplayName("DashBoard : count FMS OutBound")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void countFMSOutbound() throws Exception {
				

		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get(ControllerPath.DASH+"/countFMSOutbound")
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
	
	@Test
	@DisplayName("DashBoard : count FTS OutBound")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void countFTSOutbound() throws Exception {
				

		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get(ControllerPath.DASH+"/countFTSOutbound")
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
	
	@Test
	@DisplayName("DashBoard : count MSS OutBound")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void countMSSOutbound() throws Exception {
				

		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get(ControllerPath.DASH+"/countMSSOutbound")
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
	@Test
	@DisplayName("DashBoard : count FMS InBound")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void countFMSInbound() throws Exception {
				

		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get("/dashboard/countFMSInbound")
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
	@Test
	@WithMockUser(username="Admin", roles={"ADMIN"})
	@DisplayName("DashBoard : count FTS InBound")
	void countFTSInbound() throws Exception {
				

		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get("/dashboard/countFTSInbound")
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
	@Test
	@DisplayName("DashBoard : count MSS InBound")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void countMSSInbound() throws Exception {
				

		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get("/dashboard/countMSSInbound")
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
	
}
