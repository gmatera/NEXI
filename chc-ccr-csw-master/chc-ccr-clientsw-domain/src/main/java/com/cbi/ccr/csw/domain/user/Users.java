package com.cbi.ccr.csw.domain.user;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USERS", uniqueConstraints = {@UniqueConstraint(columnNames = { "username"}, name = "UNIQUE_USER")})
public class Users {

	public static final String PROP_FULL_NAME = "fullName";
	
	@Id
	@GeneratedValue(generator="SEQ_USERS", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="SEQ_USERS", sequenceName="SEQ_USERS", allocationSize=1)
	private Long id;
	
	@NotBlank
	@Size(max = 255)
	private String username;
	
	@NotBlank
	@Column(length = 255)
	private String password;
	
	@Column(length = 255, name = "full_Name")
	private String fullName;
	
	// comma separated ROLES
	@Column(length = 300)
	private String roles;
	
	@Column(name = "SECRET_ANSWER_ONE")
	private String secretAnswerOne;

	@Column(name = "SECRET_ANSWER_TWO")
	private String secretAnswerTwo;

	@Column(name = "SECRET_RESPONSE_ONE")
	private String secretResponseOne;

	@Column(name = "SECRET_RESPONSE_TWO")
	private String secretResponseTwo;


	public Users(Users user) {
		this.id = user.id;
		this.username= user.username;
		this.password= user.password;
		this.roles =user.roles;
		this.fullName = user.fullName;
	}
}
