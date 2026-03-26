package com.cbi.ccr.csw.domain.csw.config.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//TODO define not null field
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "CONFIGURATION_FMS_LOCALBA_REMOTEBA", uniqueConstraints = {@UniqueConstraint(columnNames = { "INTERFACE_TYPE", "LOCALBAID","REMOTEBAID"}, name = "UNIQUE_CONFIGURATION_FMS_LOCALBA_REMOTEBA")})
public class ConfigurationFMSDB extends ConfigurationFMSFTSDB {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(generator="SEQ_CONFIGURATION_FMS_LOCALBA_REMOTEBA", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_CONFIGURATION_FMS_LOCALBA_REMOTEBA", sequenceName="SEQ_CONFIGURATION_FMS_LOCALBA_REMOTEBA", allocationSize=1)
	private Long id;
	
}
