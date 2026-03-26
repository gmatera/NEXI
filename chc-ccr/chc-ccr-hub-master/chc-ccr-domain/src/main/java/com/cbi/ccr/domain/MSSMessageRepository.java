package com.cbi.ccr.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MSSMessageRepository extends CommonMessageRepository<MSSMessage>, JpaRepository<MSSMessage, Long>{
	
	MSSMessage findFirstByMsgIdAndLocalBaId(String msgId, String localBaId);
	
	List<MSSMessage> findByStatus(ClientMessageStatus status);
	
	List<MSSMessage> findBySubStatus(ClientMessageSubStatus subSstatus);
	
	List<MSSMessage> findByStatusAndSubStatus(ClientMessageStatus status, ClientMessageSubStatus subSstatus);
	
	Optional<MSSMessage> findFirstByChcId(Long chcId);
	
	@Modifying
	@Query("update MSSMessage e set e.status = ?1 where e.id = ?2")
	int setStatus(ClientMessageStatus status, Long id);
	
	@Modifying
	@Query("update MSSMessage e set e.status=?1, e.log=?2 where e.id = ?3")
	int setStatusAndLog(ClientMessageStatus status, String log, Long id);
	
	@Modifying
	@Query("update MSSMessage e set e.log=?1 where e.id = ?2")
	int setLog(String log, Long id);
	
	@Modifying
	@Query("update MSSMessage e set e.subStatus=?1, e.log=?2 where e.id=?3")
	int setSubStatusAndLog(ClientMessageSubStatus subStatus, String log, Long id);
	
	@Modifying
	@Query("update MSSMessage e set e.subStatus = ?1 where e.id = ?2")
	int setSubStatus(ClientMessageSubStatus subStatus, Long id);
	
	@Modifying
	@Query("update MSSMessage e set e.status =?1, e.subStatus=?2 where e.id = ?3")
	int setStatusAndSubStatus(ClientMessageStatus status, ClientMessageSubStatus subStatus, Long id);
	

}
