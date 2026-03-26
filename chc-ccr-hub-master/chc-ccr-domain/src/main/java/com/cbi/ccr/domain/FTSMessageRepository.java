package com.cbi.ccr.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FTSMessageRepository extends CommonMessageRepository<FTSMessage>, JpaRepository<FTSMessage, Long>{
	
	FTSMessage findFirstByVfnAndLocalBaId(String vfn, String localBaId);
	
	List<FTSMessage> findAllByStatus(ClientMessageStatus status);

	
	List<FTSMessage> findByStatus(ClientMessageStatus status);
	
	List<FTSMessage> findBySubStatus(ClientMessageSubStatus subSstatus);
	
	List<FTSMessage> findByStatusAndSubStatus(ClientMessageStatus status, ClientMessageSubStatus subSstatus);
	
	@Modifying
	@Query("update FTSMessage e set e.status = ?1 where e.id = ?2")
	int setStatus(ClientMessageStatus status, Long id);
	
	@Modifying
	@Query("update FTSMessage e set e.status=?1, e.log=?2 where e.id = ?3")
	int setStatusAndLog(ClientMessageStatus status, String log, Long id);
	
	@Modifying
	@Query("update FTSMessage e set e.log=?1 where e.id = ?2")
	int setLog(String log, Long id);
	
	@Modifying
	@Query("update FTSMessage e set e.subStatus=?1, e.log=?2 where e.id=?3")
	int setSubStatusAndLog(ClientMessageSubStatus subStatus, String log, Long id);
	
	@Modifying
	@Query("update FTSMessage e set e.subStatus = ?1 where e.id = ?2")
	int setSubStatus(ClientMessageSubStatus subStatus, Long id);
	
	@Modifying
	@Query("update FTSMessage e set e.status =?1, e.subStatus=?2 where e.id = ?3")
	int setStatusAndSubStatus(ClientMessageStatus status, ClientMessageSubStatus subStatus, Long id);

}
