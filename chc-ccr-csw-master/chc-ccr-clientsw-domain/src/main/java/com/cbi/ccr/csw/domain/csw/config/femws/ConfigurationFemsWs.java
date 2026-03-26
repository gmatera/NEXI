package com.cbi.ccr.csw.domain.csw.config.femws;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "CONFIGURATION_FEM_WS", uniqueConstraints = {@UniqueConstraint(columnNames = { "BAID"}, name = "UNIQUE_CONFIGURATION_FEMWS")})
public class ConfigurationFemsWs{
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(generator="SEQ_CONFIGURATION_FEMS_WS", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_CONFIGURATION_FEMS_WS", sequenceName="SEQ_CONFIGURATION_FEMS_WS", allocationSize=1)
	private Long id;
		
	@NotNull
	@Column(name = "BAID", length = 12, nullable = false)
	private String baId;
	
	@Column(name = "WS_SOAP_ACTION", length = 50)
	private String wsSoapAction;

	@Column(name = "WEB_SERVER_URL")
	private String webServerUrl;
}
