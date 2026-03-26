package com.cbi.ccr.csw.mq.domain.fms;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.CSWCommonRepositoryMQ;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;

public interface FMSSendMQRepository extends CSWCommonRepositoryMQ<FMSSendMQ, Long>{

	FMSSendMQ findOneByLocalBaIdAndRemoteBaIdAndVfnAndUdr(String baLoc, String baRem, String vfn, String udr);
	
	FMSSendMQ findOneByLocalBaIdAndRemoteBaIdAndVfn(String baLoc, String baRem, String vfn);
	
	@Query(value = "select count(s) from FMSSendMQ s where s.udr = ?1")
	long countUdr(String udr);

	@Query(value = "select count(s) from FMSSendMQ s where s.cswInsertTimestamp >= :from and s.cswInsertTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
	@Query(value = "select count(s) from FMSSendMQ s where s.status = ?1")
	long countByStatus(SendStatusMQ cswStatus);
}
