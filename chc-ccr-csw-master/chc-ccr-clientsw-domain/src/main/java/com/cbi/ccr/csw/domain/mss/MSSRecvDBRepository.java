package com.cbi.ccr.csw.domain.mss;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface MSSRecvDBRepository extends JpaRepository<MSSRecv, Long>, JpaSpecificationExecutor<MSSRecv>{
	
	@Query(value = "select count(s) from MSSRecv s where s.receiveTimestamp >= :from and s.receiveTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
	@Query(value = "select count(s) from MSSRecv s where s.cswStatus = ?1")
	long countByCswStatus(ClientTaskStatus cswStatus);
}
