package com.cbi.ccr.csw.mq.domain.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "CONFIGURATION_MQ_PRIMITIVE")
public class ConfigurationMqPrimitive {

	@EqualsAndHashCode.Include
	@Id
	@Column(name = "id")
	@GeneratedValue(generator="SEQ_CONFIGURATION_MQ_PRIMITIVE", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_CONFIGURATION_MQ_PRIMITIVE", sequenceName="SEQ_CONFIGURATION_MQ_PRIMITIVE", allocationSize=1)
	private Long id;
	
	@Column(name = "QUEUE_NAME")
	private String queueName;
	
	@Column(name = "PRIMITIVE")
	private String primitive;
	
	@Column(name = "MQ_CHANNEL")
	private String mqChannel;
	
	@Column(name = "TO_LOAD")
	private Boolean toLoad;
}
