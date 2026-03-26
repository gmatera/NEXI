package com.cbi.ccr.csw.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long>{
	
	Optional<Users> findByUsername(String username);
	Boolean existsByUsername(String username);
	Optional<Users> findById(Long id);

	

}
