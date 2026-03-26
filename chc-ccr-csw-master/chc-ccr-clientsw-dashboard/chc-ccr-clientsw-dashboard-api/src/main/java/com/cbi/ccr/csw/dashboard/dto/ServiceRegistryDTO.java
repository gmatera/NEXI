package com.cbi.ccr.csw.dashboard.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.cbi.ccr.csw.domain.csw.ServiceStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceRegistryDTO {
	private String id;
	private String groupId;
	private Set<String> roles;
	private LocalDateTime lastUpdate;
	private ServiceStatus serviceStatus;
	private String hostName;
	private Integer port;
}
