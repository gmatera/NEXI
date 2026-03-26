package com.cbi.ccr.csw.domain.fms;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.CSWCommonRepositoryDB;
import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface FMSSendDBRepository extends CSWCommonRepositoryDB<FMSSend, Long>{

//	List<FMSSend> findByStatusCodeBAAndStatusCodeSync(Integer statusCodeBA, Integer statusCodeSync);
	
	// not used
//	List<FMSSend> findByStatusCodeBAAndStatusCodeSyncAndCswStatus(Integer statusCodeBA, Integer statusCodeSync, ClientTaskStatus status);
	// not used
//	List<FMSSend> findByStatusCodeBAAndStatusCodeSyncAndStatus(Integer statusCodeBA, Integer statusCodeSync, FMSSendStatus status);
	
	List<FMSSend> findAllByStatus(FMSSendStatus status);
	
	@Query(value = "FROM FMSSend WHERE cswStatus is null OR cswStatus = 'WAITING_FOR_RETRY' ")
	List<FMSSend> findAllMessagesToProcess();
	
	@Query(value = "select count(s) from FMSSend s where s.baInsertTimestamp >= :from and s.baInsertTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
	@Query(value = "select count(s) from FMSSend s where s.cswStatus = ?1")
	long countByCswStatus(ClientTaskStatus cswStatus);
	
}
