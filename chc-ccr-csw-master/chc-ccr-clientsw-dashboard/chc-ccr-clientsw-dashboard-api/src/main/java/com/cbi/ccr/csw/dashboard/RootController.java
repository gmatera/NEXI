package com.cbi.ccr.csw.dashboard;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;

@Controller
@RequestMapping(ControllerPath.UI)
public class RootController {
    	 
	@GetMapping("/")
	public String home(HttpServletRequest request) {
		return "forward:/ui/index.html";
	}
	
  
}