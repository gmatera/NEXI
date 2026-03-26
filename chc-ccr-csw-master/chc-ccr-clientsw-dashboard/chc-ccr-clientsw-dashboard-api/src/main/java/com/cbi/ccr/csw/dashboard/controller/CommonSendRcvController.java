package com.cbi.ccr.csw.dashboard.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;
import com.cbi.ccr.csw.dashboard.dto.sendrcv.SendReceiveDTO;
import com.cbi.ccr.csw.domain.i.CswEntity;
import com.cbi.frw.api.dto.PagedResultDTO;
import com.cbi.frw.common.json.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CommonSendRcvController<D extends SendReceiveDTO, F extends PageableFilterDTO, R extends JpaRepository<E, Long> & JpaSpecificationExecutor<E>, 
	E extends CswEntity> extends CommonMessageController<D, F, R, E>{
	
	@Autowired
	protected ModelMapper mapper;
	
	protected CommonSendRcvController(Class<D> dtoClass, Class<E> entityClass) {
		super(dtoClass, entityClass);
	}
	
	public ResponseEntity<PagedResultDTO<D>> messages(@RequestBody F filter) {
		
		PagedResultDTO<D> list = getPageableEntity(filter);
		
		if(log.isDebugEnabled()) {
			log.debug(" dto {}", JSON.toJson(list.getList()));
		}
		return ResponseEntity.ok(list);
	}
	
}
