package com.cbi.ccr.csw.dashboard.test.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import com.cbi.ccr.csw.dashboard.ConfigT;
import com.cbi.ccr.csw.dashboard.controller.ServiceRegistryController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.dto.ServiceRegistryDTO;
import com.cbi.ccr.csw.dashboard.mss.dto.MSSSendDTO;
import com.cbi.ccr.csw.dashboard.test.DashBaseTest;
import com.cbi.ccr.csw.domain.csw.ServiceStatus;

@SpringBootTest(classes = { ConfigT.class })
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
public class ServiceRegistryControllerTest extends DashBaseTest{

	
	private MockMvc mockMvc;

	@Autowired
	public ServiceRegistryControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@Mock
	protected ServiceRegistryController serviceRegistryController;
	
	
	@Test
	@DisplayName("ServiceRegistry : Get Service Registry Data Test")
	@WithMockUser(username="Admin", roles={"ADMIN"})
	void getServiceRegistryData() throws Exception {
				
		
		ServiceRegistryDTO serviceRegistryDTO = new ServiceRegistryDTO();
		
		serviceRegistryDTO.setId("1");
		serviceRegistryDTO.setGroupId("groupId");
		serviceRegistryDTO.setHostName("hostName");
		serviceRegistryDTO.setServiceStatus(null);
		serviceRegistryDTO.setPort(8000);
		serviceRegistryDTO.setLastUpdate(LocalDateTime.now());
		serviceRegistryDTO.setRoles(null);
		
		
		List<ServiceRegistryDTO>  list = new ArrayList<ServiceRegistryDTO>() ;
		list.add(serviceRegistryDTO);
		
		Mockito.when(serviceRegistryController.getAllServiceRegistry()).thenReturn(list);
	
		
		 ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
				 .get("/serviceRegistry")
				 .accept(MediaType.APPLICATION_JSON));							 
		 
		 resultActions.
         andExpect(MockMvcResultMatchers.status().isOk()).
         andDo(MockMvcResultHandlers.print());
		 System.out.println("response:"+resultActions.andReturn().getResponse().getContentAsString());
		
	}
}
