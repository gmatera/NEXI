package com.cbi.ccr.csw.domain.csw.mq.pool;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.dto.fms.ServiceType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = PrimitivePool.TABLE_NAME)
public class PrimitivePool {

	public static final String TABLE_NAME = "PRIMITIVE_POOL";
	public static final String PROP_ID = "ID";
	public static final String PROP_POSITIVE_PRIMITIVE = "POSITIVE_PRIMITIVE";
	public static final String PROP_NEGATIVE_PRIMITIVE = "NEGATIVE_PRIMITIVE";
	
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "ID")
	@NotNull
	@GeneratedValue(generator="SEQ_PRIMITIVE_POOL", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_PRIMITIVE_POOL", sequenceName="SEQ_PRIMITIVE_POOL", allocationSize=1)
	private Long id;
	
	@Column(name = "PRIMITIVE_ID")
	@Size(max = 4)
	private String primitiveId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SERVICE_TYPE")
	@Size(max = 3)
	private ServiceType serviceType;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "FILENAME")
	private String fileName;
	
	@Column(name = "DESTINATION_QUEUE")
	private String destinationQueue;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private PrimitivePoolStatus status = PrimitivePoolStatus.NEW;

	@Column(name = "FILE_GROUP_ID")
	private String fileGroupid;

	
}
