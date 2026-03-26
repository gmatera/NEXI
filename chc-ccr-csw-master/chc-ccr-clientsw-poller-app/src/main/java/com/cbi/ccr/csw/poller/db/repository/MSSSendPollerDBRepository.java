package com.cbi.ccr.csw.poller.db.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;

public interface MSSSendPollerDBRepository extends CSWCommonPollerRepositoryDB<MSSSend, Long>{
	
	@Query(value = "FROM MSSSend WHERE cswStatus is null OR cswStatus = 'WAITING_FOR_RETRY' ")
	List<MSSSend> findAllMessagesToProcess(Pageable pageable);
	
	// OR cswStatus = 'SENDING' è un po pericoloso perchè se il poller si riavvia metre una richiesta è veramente in sending
		// la ripocesserebbe malamente
	@Query(value = "FROM MSSSend WHERE cswStatus = 'NEW' ")
	List<MSSSend> findAllNotSubmitted();
}
