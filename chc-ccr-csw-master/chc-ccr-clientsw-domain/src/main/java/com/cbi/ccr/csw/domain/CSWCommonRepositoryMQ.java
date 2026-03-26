package com.cbi.ccr.csw.domain;

import org.springframework.data.repository.NoRepositoryBean;

import com.cbi.ccr.csw.domain.i.CswOutboundEntity;

@NoRepositoryBean
public interface CSWCommonRepositoryMQ<E extends CswOutboundEntity, T extends Object> extends CSWCommonOutboundRepository<E,T> { 

}
