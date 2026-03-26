package com.cbi.ccr.csw.domain.csw;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.cbi.ccr.csw.domain.fms.FMSSendStatus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "csw_service_registry_log")
public class ServiceRegistryLog {

	@Id
	@Column(name = "id", length = 50)
	private String id; 
	
	@NotNull
	@Column(name = "insert_date")
	private LocalDateTime insertDate;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "log_type", length = 30)
	private ServiceLogType logType;
	
	@Column(name = "log", length = 1024)
	private String log;
	
	@NotNull
	@Column(name = "service_registry_id", length = 50)
	private String serviceRegistryId; 
	
}
