package com.cbi.ccr.csw.domain.fms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.cbi.ccr.csw.domain.FMSSendBlobAbstract;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "SYNC_SEND")
public class FMSSendBlob extends FMSSendBlobAbstract {

//	@EqualsAndHashCode.Include
//	@Id
//	@Column(name = "ID_SYNC_SEND")
//	@NotNull
//	private Long id;
//	
//	@Lob
//	@NotNull
//	@Column(name = "MESSAGE",updatable= false)
//	private Blob message;

}
