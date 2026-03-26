package com.cbi.ccr.csw.domain.mss;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "FAS_MSG_RECV")
public class MSSRecvBlob {
	
	@EqualsAndHashCode.Include
	@Id
	@Column(name = "SEQID")
	@NotNull
	private Long id;
	
	@Lob
	@NotNull
	@Column(name = "MAB", updatable= false)
	private Blob message;
	
}
