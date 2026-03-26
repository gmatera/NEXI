package com.cbi.ccr.csw.domain.csw.config;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import com.cbi.ccr.csw.domain.i.CswFmsFtsMssConfigEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ConfigurationFMSFTSMSSCommon extends ConfigurationCommon implements CswFmsFtsMssConfigEntity{

	@Column(name = "INTERFACE_TYPE", nullable = false)
	private String interfaceType;
	
	
	@Column(name = "SND_COMPLETION_ALGO")
	private Boolean sndCompletionAlgo = Boolean.TRUE;
	
	@Column(name = "RCV_COMPLETION_ALGO")
	private Boolean rcvCompletionAlgo = Boolean.TRUE;

	@Column(name = "LAU_ENABLED")
	private Boolean lauEnabled;
	
	@Column(name = "LAU_KEY")
	private String lauKey;

}
