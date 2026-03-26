package com.cbi.ccr.csw.poller.db.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.cbi.ccr.csw.domain.i.CswOutboundEntity;

@NoRepositoryBean
public interface CSWCommonPollerRepositoryDB<E extends CswOutboundEntity, K> extends PagingAndSortingRepository<E, K>{
	
	
	List<E> findAllMessagesToProcess(Pageable pageable);
	
	List<E> findAllNotSubmitted();
}
