package com.cbi.ccr.csw.dashboard.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cbi.ccr.csw.dashboard.I18nDashboard;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.dto.PageableFilterDTO;
import com.cbi.ccr.csw.dashboard.jwt.annotation.PreAuthorizeRoleAdmin;
import com.cbi.frw.api.dto.GenericDTO;
import com.cbi.frw.api.dto.PagedResultDTO;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.json.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CommonConfigurationController<D extends GenericDTO, F extends PageableFilterDTO, R extends JpaRepository<E, Long> & JpaSpecificationExecutor<E>, 
	E > extends CommonMessageController<D, F, R, E>{
	
	public CommonConfigurationController(Class<D> dtoClass, Class<E> entityClass) {
		super(dtoClass, entityClass);
	}

	@Autowired
	protected ModelMapper mapper;
	
	
	@PreAuthorizeRoleAdmin
	@PostMapping(ControllerPath.CONFIG_SAVE)
	public ResponseEntity<Void> saveOrUpdate(@RequestBody D dto) throws ChcException {
		E config = mapper.map(dto, getEntityClass());
				try {
			repo.save(config);
		} catch (DataIntegrityViolationException e) {
			throw new ChcException(I18nDashboard.RECORD_ALREADY_PRESENT);
		}
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorizeRoleAdmin
	@PostMapping(ControllerPath.CONFIG_DELETE+"/{id}")
	public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
		repo.deleteById(id);
		if(log.isDebugEnabled()) {
			log.debug("Id received: {}", id);
		}
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorizeRoleAdmin
	@PostMapping(ControllerPath.CONFIG_LIST)
	public ResponseEntity<PagedResultDTO<D>> configurations(@RequestBody F filter) {
		
		PagedResultDTO<D> list = getPageableEntity(filter);
		
		if(log.isDebugEnabled()) {
			log.debug(" dto {}", JSON.toJson(list.getList()));
		}
		return ResponseEntity.ok(list);
	}
	
	
}
