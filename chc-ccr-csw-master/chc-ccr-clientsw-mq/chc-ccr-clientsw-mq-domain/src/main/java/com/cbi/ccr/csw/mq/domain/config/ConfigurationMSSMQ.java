package com.cbi.ccr.csw.mq.domain.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSMSSCommon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA", uniqueConstraints = {@UniqueConstraint(columnNames = { "INTERFACE_TYPE", "LOCALBAID","REMOTEBAID"}, name = "UNIQUE_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA")})
public class ConfigurationMSSMQ extends ConfigurationFMSFTSMSSCommon{
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(generator="SEQ_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA", sequenceName="SEQ_CONFIGURATION_MSS_MQ_LOCALBA_REMOTEBA", allocationSize=1)
	private Long id;
	
	@Column(name = "RCV_PRIM_CONV_FORMAT")
	private String rcvPrimConvFormat;
	
}
