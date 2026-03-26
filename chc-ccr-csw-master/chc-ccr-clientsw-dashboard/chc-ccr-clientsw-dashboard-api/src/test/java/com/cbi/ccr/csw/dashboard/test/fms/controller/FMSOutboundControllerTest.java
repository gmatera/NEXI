/*package com.cbi.ccr.csw.dashboard.test.fms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
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
import com.cbi.ccr.csw.dashboard.fms.controller.FMSOutboundController;
import com.cbi.ccr.csw.dashboard.fms.dto.FMSOutFilterDTO;
import com.cbi.ccr.csw.dashboard.test.DashBaseTest;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMS;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFmsRepository;
import com.cbi.ccr.csw.domain.fms.FMSRecv;
import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fms.FMSSendRepository;
import com.cbi.frw.common.json.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = { ConfigT.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
public class FMSOutboundControllerTest extends DashBaseTest {
	

	private MockMvc mockMvc;

	@Autowired
	public FMSOutboundControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
//	@Mock
//	FMSOutboundControllerMock fmOutboundControllerMock = mock(FMSOutboundControllerMock.class);
//	
//	class FMSOutboundControllerMock extends FMSOutboundController{
//		
//		Page<FMSSend> list;
//		@Override
//		public Page<FMSSend> getPageableEntity(FMSOutFilterDTO filterDTO){
//			return list;
//		}
//		
//	}
    
	@Test
	@Disabled
	@DisplayName("FMS Outbound : Get OutBound FMS Messages")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void getOutboundTest() throws Exception {
			

		FMSOutFilterDTO filterDTO = new FMSOutFilterDTO();
		filterDTO.setRemoteBaId("remoteBaId");
		
		
//		List<FMSSend> fmsSendList = new ArrayList<FMSSend>();
//		FMSSend fmsSend = new FMSSend();
//		fmsSend.setBaMsgId("BaMsgID");
//		fmsSendList.add(fmsSend);
//		Page<FMSSend> list =  new PageImpl<>(fmsSendList);
		
	//	Mockito.when(fmsOutboundController.getPageableEntity(filterDTO)).thenReturn(list);	
		
	//	Page<FMSSend> list = null;
		
	//	Mockito.when(fmOutboundControllerMock.getPageableEntity(filterDTO)).thenReturn(list);

		 
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .post(ControllerPath.FMS_PREFIX+ControllerPath.OUTBOUND)
				 .content(JSON.toJson(filterDTO))
				 .contentType(MediaType.APPLICATION_JSON));
							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}


	

}
*/