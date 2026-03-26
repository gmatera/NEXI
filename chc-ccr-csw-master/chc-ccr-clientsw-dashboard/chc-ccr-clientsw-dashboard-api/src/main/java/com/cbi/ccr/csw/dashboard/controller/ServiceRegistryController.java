package com.cbi.ccr.csw.dashboard.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.dto.ServiceRegistryDTO;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleUser;
import com.cbi.ccr.csw.domain.csw.ServiceRegistry;
import com.cbi.ccr.csw.domain.csw.ServiceRegistryRepository;


@RestController
@RequestMapping(ControllerPath.LMI)
public class ServiceRegistryController {

	@Autowired
	private ServiceRegistryRepository serviceRegistryRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	@GetMapping("/getAllServiceRegistry")
	@PreAuthorizeRoleUser
	public List<ServiceRegistryDTO> getAllServiceRegistry(){
		List<ServiceRegistry> serviceRegistryList = serviceRegistryRepository.findAll();
		return mapper.map(serviceRegistryList, new TypeToken<List<ServiceRegistryDTO>>() {}.getType());
	}
}
