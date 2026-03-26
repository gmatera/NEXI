package com.cbi.ccr.csw.domain.csw.config.db;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;
import com.cbi.ccr.csw.domain.csw.config.DSNCreationAlgo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//TODO define not null field
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@MappedSuperclass
public abstract class ConfigurationFMSFTSDB extends ConfigurationFMSFTSCommon {
	@Column(name = "RCV_DSN_PREFIX")
	@Size(max = 44)
	private String rcvDsnPrefix;

	@Column(name = "RCV_DSN_CREATION_ALGO")
//	@Enumerated(EnumType.ORDINAL)
	private DSNCreationAlgo rcvDnsCreationAlgo = DSNCreationAlgo.DSN_MINUS_1;

}
