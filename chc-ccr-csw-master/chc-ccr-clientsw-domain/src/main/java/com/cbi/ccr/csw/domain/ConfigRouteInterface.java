package com.cbi.ccr.csw.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.dto.fms.ServiceType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "CONF_ROUTE_INTERFACE", uniqueConstraints = {@UniqueConstraint(columnNames = { "LOCALBA_ID", "REMOTEBA_ID","INTERFACE","SERVICE" }, name = "UNIQUE_CONF_ROUTE_INTERFACE")})
public class ConfigRouteInterface {
	
	public enum RouteInterface {
		MQ, DB, FS
	}
	
//	public enum MessageService {
//		FMS, FTS, MSS
//	}
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(generator="SEQ_CONF_ROUTE_INTERFACE", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_CONF_ROUTE_INTERFACE", sequenceName="SEQ_CONF_ROUTE_INTERFACE", allocationSize=1)
	private Long id;
	
	@Column(name = "LOCALBA_ID", length = 12, nullable = false)
	@Size(max = 12)
	private String localBaId;

	@Column(name = "REMOTEBA_ID", length = 12, nullable = false)
	@Size(max = 12)
	private String remoteBaId;
	
	@Column(name = "INTERFACE", nullable = false)
	@Enumerated(EnumType.STRING)
	private RouteInterface interFace;

	@Column(name = "SERVICE", nullable = false)
	@Enumerated(EnumType.STRING)
	private ServiceType service;
	
}
