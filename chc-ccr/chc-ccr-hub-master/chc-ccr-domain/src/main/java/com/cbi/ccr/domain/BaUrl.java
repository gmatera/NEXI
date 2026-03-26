package com.cbi.ccr.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "BA_URL")
public class BaUrl {
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(generator="seq_ba_url")
	@SequenceGenerator(name="seq_ba_url", sequenceName="SEQ_BA_URL", allocationSize=1)
	private Long id;
	
	@Column(name = "BA_ID", nullable = false)
	@Size(max = 12)
	private String baId;
	
	@Column(name = "URL", nullable = false)
	@Size(max = 256)
	private String url;
	
	@Column(name="CLIENT_ID")
	private String clientId;
	
	@Column(name = "active", nullable = false)
	private Boolean active;
	
}
