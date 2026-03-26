package com.cbi.ccr.csw.dashboard.test.fts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.cbi.ccr.csw.dashboard.ConfigT;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.dto.FilterDTO;
import com.cbi.ccr.csw.dashboard.fts.controller.FTSDBConfigController;
import com.cbi.ccr.csw.dashboard.fts.dto.ConfigFTSDTO;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSRecvDTO;
import com.cbi.ccr.csw.dashboard.fts.dto.FTSSendDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSSendDTO;
import com.cbi.ccr.csw.dashboard.test.DashBaseTest;
import com.cbi.ccr.csw.domain.csw.config.db.ConfigurationFtsDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSSendDBRepository;
import com.cbi.frw.common.json.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = { ConfigT.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
public class FTSMessageControllerTest extends DashBaseTest  {
	
	private MockMvc mockMvc;

	@Mock
	protected FTSDBConfigController ftsMessageController;
	
	@Autowired
	public FTSMessageControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
    
	@Test
	@DisplayName("FTS : Get OutBound Fts Messages")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void getOutboundTest() throws Exception {
				

		FilterDTO filterDTO = new FilterDTO();
		filterDTO.setRemoteBaID("remoteBaId");
		
		FTSRecvDTO ftsRecvDTO =createFTSRecvDTO() ;  
		FTSSendDTO ftsSendDTO =createFTSSendDTO(); 
		ConfigFTSDTO configFTSDTO = createConfigFTSDTO() ;
		List<FTSSendDTO>  list = new ArrayList<FTSSendDTO>() ;
		list.add(ftsSendDTO);
		
//		Mockito.when(ftsMessageController.getOutbound(filterDTO)).thenReturn(list);
//		 
//		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//				 .post(ControllerPath.FTS_PREFIX+"/outboundFts")
//				 .content(JSON.toJson(filterDTO))
//				 .contentType(MediaType.APPLICATION_JSON));
//							 
//		 
//		 resultActions.
//         andExpect(MockMvcResultMatchers.status().isOk()).
//         andDo(MockMvcResultHandlers.print());
//		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
//		
//		 assertEquals("localBaId", ftsRecvDTO.getLocalBaId());
	} 
	
	
	
	ConfigFTSDTO createConfigFTSDTO() {
		ConfigFTSDTO configFTSDTO  = new ConfigFTSDTO();
				
		return configFTSDTO;
	} 
	
	
	FTSRecvDTO createFTSRecvDTO() {
		FTSRecvDTO ftsRecvDTO  = new FTSRecvDTO();
		ftsRecvDTO.setLocalBaId("localBaId");
		
		return ftsRecvDTO;
	} 
	
	FTSSendDTO createFTSSendDTO() {
		FTSSendDTO ftsSendDTO  = new FTSSendDTO();
		ftsSendDTO.setId(1L);
		ftsSendDTO.setLocalBaId("localBa");
		ftsSendDTO.setRemoteBaId("remoteBa");
	
		
		return ftsSendDTO;
	} 

}
