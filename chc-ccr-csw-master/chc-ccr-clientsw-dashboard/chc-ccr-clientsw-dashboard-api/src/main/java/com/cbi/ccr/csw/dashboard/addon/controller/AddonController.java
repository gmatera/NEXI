package com.cbi.ccr.csw.dashboard.addon.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cbi.ccr.csw.dashboard.I18nDashboard;
import com.cbi.ccr.csw.dashboard.addon.dto.AddonConfigDTO;
import com.cbi.ccr.csw.dashboard.addon.dto.AddonConfigFilterDTO;
import com.cbi.ccr.csw.dashboard.controller.CommonConfigurationController;
import com.cbi.ccr.csw.dashboard.dto.ControllerPath;
import com.cbi.ccr.csw.domain.csw.config.AddOnConfigurationFTSRepository;
import com.cbi.ccr.csw.domain.csw.config.AddOnFTSConfiguration;
import com.cbi.frw.api.dto.PagedResultDTO;
import com.cbi.frw.common.exception.ChcException;

@RestController
@RequestMapping(ControllerPath.ADDON_PREFIX + ControllerPath.CONFIG)
public class AddonController extends
		CommonConfigurationController<AddonConfigDTO, AddonConfigFilterDTO, AddOnConfigurationFTSRepository, AddOnFTSConfiguration> {
	
	
	public AddonController() {
		super(AddonConfigDTO.class, AddOnFTSConfiguration.class);
	}

	@Override
	@PostMapping(ControllerPath.CONFIG_SAVE)
	public ResponseEntity<Void> saveOrUpdate(@RequestBody AddonConfigDTO dto) throws ChcException {

		Path fileFolder = Paths.get(dto.getSndPath());
		Path fileFolder2 = Paths.get(dto.getRcvPath());

		if (Files.notExists(fileFolder)) {
			throw new ChcException(I18nDashboard.FOLDER_DOES_NOT_EXISTS, fileFolder);
		}
		if (Files.notExists(fileFolder2)) {
			throw new ChcException(I18nDashboard.FOLDER_DOES_NOT_EXISTS, fileFolder2);
		}
			return super.saveOrUpdate(dto);
	

	}
	
	@Override
	@PostMapping(ControllerPath.CONFIG_LIST)
	public ResponseEntity<PagedResultDTO<AddonConfigDTO>> configurations(@RequestBody AddonConfigFilterDTO filter) {
		filter.setOrderBylocaRemoteBa(true);
		PagedResultDTO<AddonConfigDTO> list = getPageableEntity(filter);
		return ResponseEntity.ok(list);
	}


}
