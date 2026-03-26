package com.cbi.ccr.csw.domain;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@MappedSuperclass
public abstract class FMSRecvBlobAbstract {
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "FAS_SEQID")
	@NotNull
	private Long id;
	
	@Lob
	@NotNull
	@Column(name = "MESSAGE",updatable= false)
	private Blob message;
	
}
