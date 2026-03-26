package com.cbi.ccr.csw.dashboard.fms.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cbi.ccr.csw.dashboard.controller.CommonSendRcvController;
import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;
import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendReceiveDTO;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.i.CswOutboundEntity;

public abstract class CommonOutboundDBController<D extends SendReceiveDTO, F extends PageableFilterDTO, R extends JpaRepository<E, Long> & JpaSpecificationExecutor<E>, 
E extends CswOutboundEntity> extends CommonSendRcvController<D, F, R, E> {

	protected CommonOutboundDBController(Class<D> dtoClass, Class<E> entityClass) {
		super(dtoClass, entityClass);
	}
	
	@Transactional
	public void retry(Class<E> entityClass, List<E> request) {
		for(E message : request) {
			message.setCswStatus(ClientTaskStatus.WAITING_FOR_RETRY);
			message.setRetryCounter(0);
		}
		repo.saveAll(request);
	}
	
}
