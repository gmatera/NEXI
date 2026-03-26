package com.cbi.ccr.csw.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.cbi.ccr.csw.domain.i.CswOutboundEntity;

@NoRepositoryBean
public interface CSWCommonOutboundRepository<E extends CswOutboundEntity, T extends Object> extends JpaRepository<E, T> ,JpaSpecificationExecutor<E> {

	List<E> findByCswStatus(ClientTaskStatus cswStatus, Pageable pageRequest);
	
}
