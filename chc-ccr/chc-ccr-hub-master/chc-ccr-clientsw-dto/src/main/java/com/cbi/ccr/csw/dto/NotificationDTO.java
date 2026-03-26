package com.cbi.ccr.csw.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.cbi.ccr.csw.dto.fms.ServiceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private ServiceType service;
	@NotNull
	private Long messageKey;
	@NotNull
	private String localBaId;
	@NotNull
	private String remoteBaId;
	@NotNull
	private Boolean success;
	
	private String cmdTms;
}
