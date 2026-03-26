package com.cbi.ccr.csw.poller.db.repository.mq;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.poller.db.repository.CSWCommonPollerRepositoryDB;

@Profile("MQ")
public interface FTSSendMQPollerDBRepository  extends CSWCommonPollerRepositoryDB<FTSSendMQ, Long>{
	
	
	@Query(value = "FROM FTSSendMQ WHERE cswStatus is null OR cswStatus = 'WAITING_FOR_RETRY' ")
	List<FTSSendMQ> findAllMessagesToProcess(Pageable pageable);
	
	// OR cswStatus = 'SENDING' è un po pericoloso perchè se il poller si riavvia metre una richiesta è veramente in sending
		// la ripocesserebbe malamente
	@Query(value = "FROM FTSSendMQ WHERE cswStatus = 'NEW'")
	List<FTSSendMQ> findAllNotSubmitted();
}
