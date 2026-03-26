package com.cbi.ccr.csw.dashboard.globalproperties.controller;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.I18nDashboard;
import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.dashboard.globalproperties.dto.GlobalPropertiesDTO;
import com.cbi.ccr.csw.dashboard.globalproperties.dto.GlobalPropertiesFilterDTO;
import com.cbi.ccr.csw.domain.csw.config.GlobalProperties;
import com.cbi.ccr.csw.domain.csw.config.GlobalPropertiesRepository;
import com.cbi.ccr.csw.domain.csw.config.PropertiesEnum;
import com.cbi.ccr.csw.encryption.LocalEncryptionService;
import com.cbi.frw.common.exception.ChcException;


@RestController
@RequestMapping(ControllerPath.GLOBAL_PROPERTIES_PREFIX + ControllerPath.CONFIG)
public class GlobalPropertiesController extends
		CommonConfigurationController<GlobalPropertiesDTO, GlobalPropertiesFilterDTO, GlobalPropertiesRepository, GlobalProperties> {

	
	@Autowired
	private LocalEncryptionService localEncryptionService;
	
	@Autowired 
	private EntityManager entityManager;
	

	public GlobalPropertiesController() {
		super(GlobalPropertiesDTO.class, GlobalProperties.class);
	}

	

	@Override
	public ResponseEntity<Void> saveOrUpdate(@RequestBody GlobalPropertiesDTO dto) throws ChcException {
		
		if(dto.getId() == null) {
			Query query = entityManager.createQuery("SELECT g FROM GlobalProperties g WHERE propertyName=?1");
			query.setParameter(1, dto.getPropertyName());
			if(!query.getResultList().isEmpty()) throw new ChcException(I18nDashboard.RECORD_ALREADY_PRESENT);
		}
		checkForDataToEncrypt(dto);
		return super.saveOrUpdate(dto);
	}

	public void checkForDataToEncrypt(GlobalPropertiesDTO dto) {
		
		if(dto.getPropertyName().equals(PropertiesEnum.ENC_PRIVATE_KEY.name()) ||  
				dto.getPropertyName().equals(PropertiesEnum.SIGN_PRIVATE_KEY.name())) {
			
			String encryptedValue = localEncryptionService.encrypt(dto.getValue());
			dto.setValue( encryptedValue);
		}
		
	}

	
}