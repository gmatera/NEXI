package com.cbi.ccr.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommonMessageRepository<T extends CommonEntity> {
	List<T> findByStatus(ClientMessageStatus status);
	
	List<T> findBySubStatus(ClientMessageSubStatus subSstatus);
	
	List<T> findByStatusAndSubStatus(ClientMessageStatus status, ClientMessageSubStatus subSstatus);
	
	Optional<T> findFirstByChcId(Long chcId);
	int setStatus(ClientMessageStatus status, Long id);
	int setStatusAndLog(ClientMessageStatus status, String log, Long id);
	int setSubStatusAndLog(ClientMessageSubStatus subStatus, String log, Long id);
	int setSubStatus(ClientMessageSubStatus subStatus, Long id);
	int setStatusAndSubStatus(ClientMessageStatus status, ClientMessageSubStatus subStatus, Long id);

	@Query(value = "SELECT ?1.NEXTVAL FROM DUAL", nativeQuery = true)
	Long getSequenceNextVal(String sequence);

	Optional<T> findById(Long valueOf);

	T save(T fmsMessage);

	int setLog(String string, Long id);
	
	List<T> findAllByStatus(ClientMessageStatus status);
}
