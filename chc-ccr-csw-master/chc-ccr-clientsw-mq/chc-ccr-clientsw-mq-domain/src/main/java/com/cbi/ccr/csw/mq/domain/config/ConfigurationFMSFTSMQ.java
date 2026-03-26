package com.cbi.ccr.csw.mq.domain.config;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;

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
public abstract class ConfigurationFMSFTSMQ extends ConfigurationFMSFTSCommon {
	
	@Column(name = "MQI_POS_CREATE_IND")
	private Boolean mqiPosCreateInd;
	
	@Column(name = "RCV_AUTO_READ")
	private Boolean rcvAutoRead;
	
	@Column(name = "RCV_PRIM_CONV_FORMAT")
	private String rcvPrimConvFormat;
	
	@Column(name = "UPLOAD_Q_NAME")
	private String uploadQName;
	
}
