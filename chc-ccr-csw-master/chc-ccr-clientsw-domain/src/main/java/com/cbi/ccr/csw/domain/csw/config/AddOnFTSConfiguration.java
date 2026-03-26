package com.cbi.ccr.csw.domain.csw.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.i.CswAddonConfigEntiry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "ADD_ON_CONFIGURATION_FTS", uniqueConstraints = {@UniqueConstraint(columnNames = { "LOCALBAID","REMOTEBAID"}, name = "UNIQUE_ADD_ON_CONFIGURATION_FTS")})
public class AddOnFTSConfiguration extends ConfigurationCommon implements CswAddonConfigEntiry{
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(generator="SEQ_ADD_ON_CONFIGURATION_FTS", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_ADD_ON_CONFIGURATION_FTS", sequenceName="SEQ_ADD_ON_CONFIGURATION_FTS", allocationSize=1)
	private Long id;
		
	@NotNull
	@Column(name = "SND_PATH")
	private String sndPath;
	
	@Column(name = "RCV_PATH")
	private String rcvPath;
	
	@Column(name = "SENDING_PREFIX")
	private String sendingPrefix;
	
	@Column(name = "ERROR_PREFIX")
	private String errorPrefix;
	
	@Column(name = "SENT_PREFIX")
	private String sentPrefix;
	
	@Column(name = "ERRORDELIVER_PREFIX")
	private String errorDeliverPrefix;

}
