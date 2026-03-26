package com.cbi.ccr.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BaUrlRepository extends JpaRepository<BaUrl, Long>{

	
	
	Optional<BaUrl> findFirstByBaIdAndActive(String baId, Boolean active);
	
	
	
}
