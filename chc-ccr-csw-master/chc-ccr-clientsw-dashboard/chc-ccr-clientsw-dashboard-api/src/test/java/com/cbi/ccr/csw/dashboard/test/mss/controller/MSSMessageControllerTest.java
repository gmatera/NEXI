package com.cbi.ccr.csw.dashboard.test.mss.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.cbi.ccr.csw.dashboard.ConfigT;
import com.cbi.ccr.csw.dashboard.dto.FilterDTO;
import com.cbi.ccr.csw.dashboard.mss.controller.MSSDBConfigController;
import com.cbi.ccr.csw.dashboard.mss.dto.ConfigMSSDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSRecvDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSSendDTO;
import com.cbi.ccr.csw.dashboard.test.DashBaseTest;

@SpringBootTest(classes = { ConfigT.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
public class MSSMessageControllerTest extends DashBaseTest{
	
	private MockMvc mockMvc;

	@Autowired
	public MSSMessageControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	@Mock
	protected MSSDBConfigController mssMessageController;
    
	@Test
	@DisplayName("MSS : Get OutBound Mss Messages")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void getOutboundTest() throws Exception {
		
		
		FilterDTO filterDTO = new FilterDTO();
		filterDTO.setRemoteBaID("remoteBaId");
		
		MSSSendDTO mssSendDTO =createMSSSendDTO();
		MSSRecvDTO mssReceiveDto =	createMSSRecvDTO();
		ConfigMSSDTO configMssDto =createConfigMSSDTO();
		List<MSSSendDTO>  list = new ArrayList<MSSSendDTO>() ;
		list.add(mssSendDTO);
		
//		Mockito.when(mssMessageController.getOutbound(filterDTO)).thenReturn(list);
//	
//	
//		 
//		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//				 .post(ControllerPath.MSS_PREFIX+"/outboundMss")
//				 .content(JSON.toJson(filterDTO))
//				 .contentType(MediaType.APPLICATION_JSON));
//							 
//		 
//		 resultActions.
//         andExpect(MockMvcResultMatchers.status().isOk()).
//         andDo(MockMvcResultHandlers.print());
//		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
//		 
//		 assertEquals("LoalBaId", mssReceiveDto.getLocalBaId());
//		 assertEquals(true, configMssDto.getSndCompletionAlgo());


		
	}
	
	
	
	
	ConfigMSSDTO createConfigMSSDTO() {
		ConfigMSSDTO configMssDto  = new ConfigMSSDTO();
		configMssDto.setId("1");


		return configMssDto;
	} 
	
	
	MSSRecvDTO createMSSRecvDTO() {
		MSSRecvDTO mssReceiveDto  = new MSSRecvDTO();
		mssReceiveDto.setId(1l);
		mssReceiveDto.setLocalBaId("LoalBaId");
		mssReceiveDto.setRemoteBaId("RemoreBaId");
		return mssReceiveDto;
	} 
	
	
	
	MSSSendDTO createMSSSendDTO() {
		MSSSendDTO mssSendDto  = new MSSSendDTO();
		mssSendDto.setId(1l);
		mssSendDto.setLocalBaId("localBa");
		mssSendDto.setRemoteBaId("remoteBa");
		mssSendDto.setApplCheck(1);
		mssSendDto.setBaInsertTimestamp(LocalDateTime.now());
		return mssSendDto;
	} 
	
	
	

}
