package com.cbi.ccr.csw.mq.domain.mss;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.CSWCommonRepositoryMQ;
import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface MSSSendMQRepository extends CSWCommonRepositoryMQ<MSSSendMQ, Long>{
	
	MSSSendMQ findOneByLocalBaIdAndRemoteBaIdAndUdr(String baLoc, String baRem, String udr);
	
	@Query(value = "select count(s) from MSSSendMQ s where s.cswInsertTimestamp >= :from and s.cswInsertTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
	@Query(value = "select count(s) from MSSSendMQ s where s.cswStatus = ?1")
	long countByCswStatus(ClientTaskStatus cswStatus);
	
}
