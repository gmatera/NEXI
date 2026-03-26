package com.cbi.ccr.csw.poller.db.repository.mq;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.poller.db.repository.CSWCommonPollerRepositoryDB;

@Profile("MQ")
public interface FMSSendMQPollerDBRepository extends CSWCommonPollerRepositoryDB<FMSSendMQ, Long>{

	@Query(value = "FROM FMSSendMQ WHERE cswStatus is null OR cswStatus = 'WAITING_FOR_RETRY' ")
	List<FMSSendMQ> findAllMessagesToProcess(Pageable pageable);
		
	// OR cswStatus = 'SENDING' è un po pericoloso perchè se il poller si riavvia metre una richiesta è veramente in sending
		// la ripocesserebbe malamente
	@Query(value = "FROM FMSSendMQ WHERE cswStatus = 'NEW' ")
	List<FMSSendMQ> findAllNotSubmitted();
}
