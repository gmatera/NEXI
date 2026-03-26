package com.cbi.ccr.csw.mq.domain.fms;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface FMSRecvMQRepository extends  JpaRepository<FMSRecvMQ, Long>, JpaSpecificationExecutor<FMSRecvMQ>{
	
	@Query(value = "select count(s) from FMSRecvMQ s where s.udr = ?1")
	long countUdr(String udr);
	
	@Query(value = "select count(s) from FMSRecvMQ s where s.cswInsertTimestamp >= :from and s.cswInsertTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
//	@Query(value = "select count(s) from FMSRecvMQ s where s.cswStatus = ?1")
//	long countByCswStatus(ClientTaskStatus cswStatus);
	
	FMSRecvMQ findOneByLocalBaIdAndRemoteBaIdAndVfn(String localBaId, String remoteBaId, String vfn);
	
	FMSRecvMQ findOneByLocalBaIdAndRemoteBaIdAndUdr(String localBaId, String remoteBaId, String udr);

}
