package com.cbi.ccr.csw.domain.csw.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
@Table(name = "GLOBAL_CONFIGURATION")
public class GlobalProperties {
	
	@GeneratedValue(generator="SEQ_GLOBAL_CONFIGURATION", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_GLOBAL_CONFIGURATION", sequenceName="SEQ_GLOBAL_CONFIGURATION", allocationSize=1)
	@EqualsAndHashCode.Include
	@Id
	private Long id;
	
	@Column(name = "PROPERTY")
	@NotNull
	private String propertyName;
	
	@Column(name = "VALUE" , length = 3000)
	@NotNull
	private String value;
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "MANDATORY")
	private Boolean mandatory;
	
	@Column(name = "REQUIRED_MSG")
	private String requiredMsg;
	
	@Column(name = "MIN_LENGTH")
	private Integer minLength;
	
	@Column(name = "MAX_LENGTH")
	private Integer maxLength;
	
	@Column(name = "DISPLAY_NAME")
	private String displayName;
	
}
