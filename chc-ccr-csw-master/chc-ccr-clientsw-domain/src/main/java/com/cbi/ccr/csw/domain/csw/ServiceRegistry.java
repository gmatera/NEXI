package com.cbi.ccr.csw.domain.csw;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.cbi.frw.persistence.domain.HasLiveness;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "csw_service_registry")
public class ServiceRegistry extends HasLiveness{

	@Id
	@Column(name = "id", length = 60)
	private String id;

	@NotNull
	@Column(name = "group_id", length = 60)
	private String groupId;
	
	// list of ServiceType
	@NotNull
	@Convert(converter = StringSetConverter.class)
	@Column(name = "roles", length = 255)
	private Set<String> roles;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "service_status", length = 30)
	private ServiceStatus serviceStatus;
	
	@NotNull
	@Column(name = "host_name", length = 80)
	private String hostName;
	
	@NotNull
	@Column(name = "port")
	private Integer port;

}
