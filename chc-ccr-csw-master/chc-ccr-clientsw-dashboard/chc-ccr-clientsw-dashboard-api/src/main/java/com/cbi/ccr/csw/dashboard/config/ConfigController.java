package com.cbi.ccr.csw.dashboard.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fms.FMSSendDBRepository;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.fts.FTSSendDBRepository;
import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.domain.mss.MSSSendDBRepository;

@Controller
@RequestMapping(ControllerPath.LMI)
public class ConfigController {
    
	
	@GetMapping("/list")
	public String fmsMessage(Model model) {
//		
//		List<FMSSend> list = fmsSendRepository.findAll();
//		
//		model.addAttribute("fmsMessages", list);
	    return "fmsMessage";
	}
	
  
}