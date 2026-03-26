package com.cbi.ccr.csw.mq.inbound.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cbi.ccr.csw.domain.i.CswInboundEntity;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.inbound.common.service.CSWCommonInboundService;
import com.cbi.ccr.csw.mq.common.service.CommonConfigurationServiceMQ;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveConfigurationLoader;
import com.cbi.ccr.csw.mq.common.service.ValidationServiceMQ;

import lombok.Getter;
import lombok.NonNull;

public abstract class CSWCommonInboundServiceMQ<E extends CswInboundEntity, R extends JpaRepository<E, Long>, D extends ClientMessageDTO>
	extends CSWCommonInboundService<E, R, D> {

	
	public CSWCommonInboundServiceMQ(@NonNull R dbRepository, @NonNull Class<E> entityClass) {
		super(dbRepository, entityClass);
		
	}
	
//	@Autowired
//	protected JmsTemplate jmsTemplate;
	
	@Autowired
	protected MqPrimitiveConfigurationLoader mqConf;
	
	@Autowired
	protected CommonConfigurationServiceMQ configurationService;
	@Getter
	@Autowired
	protected ValidationServiceMQ validationService;
}
