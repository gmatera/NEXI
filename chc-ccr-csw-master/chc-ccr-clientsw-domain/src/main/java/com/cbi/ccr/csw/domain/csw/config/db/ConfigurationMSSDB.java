package com.cbi.ccr.csw.domain.csw.config.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.i.CswFmsFtsMssConfigEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "CONFIGURATION_MSS_LOCALBA_REMOTEBA", uniqueConstraints = {@UniqueConstraint(columnNames = { "INTERFACE_TYPE", "LOCALBAID","REMOTEBAID"}, name = "UNIQUE_CONFIGURATION_MSS_LOCALBA_REMOTEBA")})
public class ConfigurationMSSDB implements CswFmsFtsMssConfigEntity {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(generator="SEQ_CONFIGURATION_MSS_LOCALBA_REMOTEBA", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_CONFIGURATION_MSS_LOCALBA_REMOTEBA", sequenceName="SEQ_CONFIGURATION_MSS_LOCALBA_REMOTEBA", allocationSize=1)
	private Long id;
	
	// spostati nella super classe, da controllare ed eliminare commento 
	@Column(name = "INTERFACE_TYPE", nullable = false)
	private String interfaceType;
	
	@Column(name = "LOCALBAID", length = 12)
	@Size(max = 12)
	private String localBaId;

	@Column(name = "REMOTEBAID", length = 12)
	@Size(max = 12)
	private String remoteBaId;
	
	@Column(name = "SND_COMPLETION_ALGO")
	private Boolean sndCompletionAlgo;
	
	@Column(name = "RCV_COMPLETION_ALGO")
	private Boolean rcvCompletionAlgo;
	
	@Column(name = "LAU_ENABLED")
	private Boolean lauEnabled;
	
	@Column(name = "LAU_KEY")
	private String lauKey;
	
}
