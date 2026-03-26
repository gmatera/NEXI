package com.cbi.ccr.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FMSMessageRepository extends CommonMessageRepository<FMSMessage>, JpaRepository<FMSMessage, Long>{

	FMSMessage findFirstByVfnAndUdr(String vfn, String udr);

	@Modifying
	@Query("update FMSMessage e set e.status = ?1 where e.id = ?2")
	int setStatus(ClientMessageStatus status, Long id);
	
	@Modifying
	@Query("update FMSMessage e set e.status=?1, e.log=?2 where e.id = ?3")
	int setStatusAndLog(ClientMessageStatus status, String log, Long id);
	
	@Modifying
	@Query("update FMSMessage e set e.log=?1 where e.id = ?2")
	int setLog(String log, Long id);
	
	@Modifying
	@Query("update FMSMessage e set e.subStatus=?1, e.log=?2 where e.id=?3")
	int setSubStatusAndLog(ClientMessageSubStatus subStatus, String log, Long id);
	
	@Modifying
	@Query("update FMSMessage e set e.subStatus = ?1 where e.id = ?2")
	int setSubStatus(ClientMessageSubStatus subStatus, Long id);
	
	@Modifying
	@Query("update FMSMessage e set e.status =?1, e.subStatus=?2 where e.id = ?3")
	int setStatusAndSubStatus(ClientMessageStatus status, ClientMessageSubStatus subStatus, Long id);

}
