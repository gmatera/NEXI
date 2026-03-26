package com.cbi.ccr.csw.db.outbound.service;

import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;
import com.cbi.ccr.csw.service.common.FileRecordFormatValidator;
import com.cbi.frw.common.exception.ChcException;

@Service
public class FileFileRecordFormatValidatorDB extends FileRecordFormatValidator {


	public void processFileValidation(ConfigurationFMSFTSCommon conf, String fileName) throws ChcException {
		processFileValidation(fileName, conf.getSndCodePage(), conf.getSndLineSeparator(),
				conf.getSndMaxRecLength(), conf.getSndRecordFormat());
	}
}
