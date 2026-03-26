package com.cbi.ccr.csw.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.i.CswOutboundEntity;

@NoRepositoryBean
public interface CSWCommonRepositoryDB<E extends CswOutboundEntity, T extends Object> extends CSWCommonOutboundRepository<E,T> {
	
	// moved into chc-ccr-clientsw-poller-app
	//List<E> findAllMessagesToProcess();
	
}
