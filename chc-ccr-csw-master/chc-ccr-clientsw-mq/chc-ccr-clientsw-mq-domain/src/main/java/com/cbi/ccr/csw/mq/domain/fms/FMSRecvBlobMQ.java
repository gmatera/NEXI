package com.cbi.ccr.csw.mq.domain.fms;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;

@Entity
@Table(name = "FMS_RECV_MQI")
public class FMSRecvBlobMQ {
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "ID")
	@NotNull
	private Long id;
	
	@Lob
	@NotNull
	@Column(name = "MESSAGE",updatable= false)
	private Blob message;
	
}
