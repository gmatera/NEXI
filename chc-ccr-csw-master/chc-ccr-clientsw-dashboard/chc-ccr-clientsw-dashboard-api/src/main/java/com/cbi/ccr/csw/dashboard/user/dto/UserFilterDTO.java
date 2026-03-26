package com.cbi.ccr.csw.dashboard.user.dto;

import com.cbi.frw.api.dto.PageableDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterDTO extends PageableDTO{
	
	private String username;
}
