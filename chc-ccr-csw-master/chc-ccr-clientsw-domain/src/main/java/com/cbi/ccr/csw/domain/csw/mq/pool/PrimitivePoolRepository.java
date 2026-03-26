package com.cbi.ccr.csw.domain.csw.mq.pool;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PrimitivePoolRepository extends JpaRepository<PrimitivePool, Long> ,JpaSpecificationExecutor<PrimitivePool> {

	
	@Query(value = "FROM PrimitivePool WHERE status = 'NEW' OR status = 'FILE_SENT'")
	List<PrimitivePool> findAllMessagesToProcess(Pageable pageable);
}
