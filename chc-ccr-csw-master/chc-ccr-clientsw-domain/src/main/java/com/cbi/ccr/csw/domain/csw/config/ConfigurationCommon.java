package com.cbi.ccr.csw.domain.csw.config;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.i.CswEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ConfigurationCommon implements CswEntity{

	public static final String PROP_LOCALBAID = "localBaId";
	public static final String PROP_REMOTEBAID = "remoteBaId";
	
	@Column(name = "LOCALBAID", length = 12)
	@Size(max = 12)
	private String localBaId;

	@Column(name = "REMOTEBAID", length = 12)
	@Size(max = 12)
	private String remoteBaId;
	

}
