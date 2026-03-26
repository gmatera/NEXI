package com.cbi.ccr.csw.mq.domain.fts;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface FTSRecvMQRepository extends  JpaRepository<FTSRecvMQ, Long>, JpaSpecificationExecutor<FTSRecvMQ>{
	
	@Query(value = "select count(s) from FTSRecvMQ s where s.cswInsertTimestamp >= :from and s.cswInsertTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
	@Query(value = "select count(s) from FTSRecvMQ s where s.cswStatus = ?1")
	long countByCswStatus(ClientTaskStatus cswStatus);
	
	FTSRecvMQ findOneByLocalBaIdAndRemoteBaIdAndVfn(String localBa, String remoteBa, String vfn);

}
