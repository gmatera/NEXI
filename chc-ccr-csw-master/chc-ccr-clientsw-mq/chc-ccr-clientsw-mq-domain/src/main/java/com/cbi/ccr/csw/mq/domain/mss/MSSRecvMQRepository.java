package com.cbi.ccr.csw.mq.domain.mss;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cbi.ccr.csw.domain.ClientTaskStatus;

public interface MSSRecvMQRepository extends  JpaRepository<MSSRecvMQ, Long>, JpaSpecificationExecutor<MSSRecvMQ>{
	
	@Query(value = "select count(s) from MSSRecvMQ s where s.cswInsertTimestamp >= :from and s.cswInsertTimestamp <= :to")
	long countMonthly(@Param("from") LocalDateTime from, @Param("to")  LocalDateTime to);
	
	@Query(value = "select count(s) from MSSRecvMQ s where s.cswStatus = ?1")
	long countByCswStatus(ClientTaskStatus cswStatus);
	
	MSSRecvMQ findOneByLocalBaIdAndRemoteBaIdAndUdr(String localBaId, String remoteBaId, String udr);

}
