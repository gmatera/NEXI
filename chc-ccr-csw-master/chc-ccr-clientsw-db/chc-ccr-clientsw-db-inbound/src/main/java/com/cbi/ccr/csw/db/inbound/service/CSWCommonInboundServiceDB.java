package com.cbi.ccr.csw.db.inbound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cbi.ccr.csw.db.common.CommonConfigurationServiceDB;
import com.cbi.ccr.csw.db.common.ValidationServiceDB;
import com.cbi.ccr.csw.domain.i.CswFmsFtsMssConfigEntity;
import com.cbi.ccr.csw.domain.i.CswInboundEntity;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.inbound.common.service.CSWCommonInboundService;

import lombok.Getter;
import lombok.NonNull;

public abstract class CSWCommonInboundServiceDB<E extends CswInboundEntity, R extends JpaRepository<E, Long>, D extends ClientMessageDTO>
	extends CSWCommonInboundService<E, R, D> {
	
	@Autowired
	protected CommonConfigurationServiceDB configurationService;
	@Getter
	@Autowired
	protected ValidationServiceDB validationService; 
	
	protected CSWCommonInboundServiceDB(@NonNull R dbRepository, @NonNull Class<E> entityClass) {
		super(dbRepository, entityClass);
	}
	
	
	protected void checkToComplete(CswFmsFtsMssConfigEntity config, E entity) {
		if(config.getRcvCompletionAlgo() == null)
			config.setRcvCompletionAlgo(true);
		
		if(config.getRcvCompletionAlgo().booleanValue()) {
			entity.setComplete(1);
		} else {
			entity.setComplete(0);
		}
	}
	
	
}
