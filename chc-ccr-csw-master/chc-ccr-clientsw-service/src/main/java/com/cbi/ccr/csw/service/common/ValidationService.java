package com.cbi.ccr.csw.service.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.ConfigRouteInterface;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.ConfigRouteInterfaceRepository;
import com.cbi.ccr.csw.domain.fms.FMSSend;
import com.cbi.ccr.csw.domain.fts.FTSSend;
import com.cbi.ccr.csw.domain.mss.MSSSend;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.util.Color;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ValidationService {

	@Autowired
	private ConfigRouteInterfaceRepository routeInterfaceRepo;

	protected static final String REGEX_LOCAL_REMOTE_BA = "[0-9A-Z]{12}";
	protected static final String REGEX_VFN = "[0-9A-Z()$#@.\\-+]{0,32}";
	protected static final String REGEX_USER_DATA_REMOTE = "[0-9A-Za-z()$#@.\\-+]{1,80}";
	protected static final String REGEX_TUR = "[0-9A-Za-z()$#@.\\-+]{0,16}";
	protected static final String REGEX_MESSAGE_TYPE = "[0-9A-Z]{1,3}";
	protected static final String REGEX_CATAPPL = "[0-9A-Z]{0,4}";
	private static final String REGEX_APPLICATIVE_DATA_FIELD_OR_REGEX_REMOTE_REF = "[0-9A-Za-z()$#@.\\-+]{0,80}";
	private static final String REGEX_MSGID = "[0-9A-Z]{1,30}";
	protected static final String REGEX_PRIORITY = "[0-2]{0,1}";
	private static final String REGEX_ZERO = "[0]{1}";

	public static final Long MESSAGE_MAX_SIZE = 1024L * 1024L; // 1Mb
	public static final Long FILE_MAX_SIZE = MESSAGE_MAX_SIZE * 1024 * 8; // 8Gb

	public static final String SHA_256 = "SHA-256";

	
	public void validateFMSRegex(FMSSend fmsSend) throws ChcException {
		validateRegex(REGEX_LOCAL_REMOTE_BA, fmsSend.getLocalBaId(), "LOCAL_BA");
		validateRegex(REGEX_LOCAL_REMOTE_BA, fmsSend.getRemoteBaId(), "REMOTE_BA");
		validateRegex(REGEX_VFN, fmsSend.getVfn(), "VFN");
		validateRegex(REGEX_USER_DATA_REMOTE, fmsSend.getUdr(), "USERDATAREMOTE");
		if (fmsSend.getTur() != null)
			validateRegex(REGEX_TUR, fmsSend.getTur(), "TUR");
		validateRegex(REGEX_MESSAGE_TYPE, fmsSend.getMessageType(), "MESSAGETYPE");
		if (fmsSend.getCatAppl() != null)
			validateRegex(REGEX_CATAPPL, fmsSend.getCatAppl(), "CATAPPL");
		validateFileName(fmsSend.getFileName());
		if (fmsSend.getComplete() != null)
			validateRegex(REGEX_ZERO, String.valueOf(fmsSend.getComplete()), "COMPLETE");
	}

	public void validateFTSRegex(FTSSend ftsSend) throws ChcException {
		validateRegex(REGEX_LOCAL_REMOTE_BA, ftsSend.getLocalBaId(), "LOCAL_BA");
		validateRegex(REGEX_LOCAL_REMOTE_BA, ftsSend.getRemoteBaId(), "REMOTE_BA");
		validateRegex(REGEX_VFN, ftsSend.getVfn(), "VFN");
		if (ftsSend.getApplicativeDataField() != null) {
			if (ftsSend.getApplicativeDataFieldLength() > 81 || ftsSend.getApplicativeDataFieldLength() < 0)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "APPLICATIVE_DATA_FIELD_LENGTH"));

			if (ftsSend.getApplicativeDataField().length() != ftsSend.getApplicativeDataFieldLength())
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "APPLICATIVE_DATA_FIELD_LENGTH"));
			validateRegex(REGEX_APPLICATIVE_DATA_FIELD_OR_REGEX_REMOTE_REF, ftsSend.getApplicativeDataField(),
					"APPLICATIVE_DATA_FIELD");
		}
		validateFileName(ftsSend.getFileName());
		if (ftsSend.getComplete() != null)
			validateRegex(REGEX_ZERO, String.valueOf(ftsSend.getComplete()), "COMPLETE");
		if (ftsSend.getApplCheck() != null)
			validateRegex(REGEX_ZERO, String.valueOf(ftsSend.getApplCheck()), "APPL_CHECK");
	}

	public void validateMSSRegex(MSSSend mssSend) throws ChcException {
		validateRegex(REGEX_LOCAL_REMOTE_BA, mssSend.getLocalBaId(), "LOCAL_BA");
		validateRegex(REGEX_LOCAL_REMOTE_BA, mssSend.getRemoteBaId(), "REMOTE_BA");
		if (mssSend.getMessageType() == null)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "MESSAGETYPE"));
		validateRegex(REGEX_MESSAGE_TYPE, mssSend.getMessageType(), "MESSAGETYPE");
		if (mssSend.getTur() != null)
			validateRegex(REGEX_TUR, mssSend.getTur(), "TUR");
		if (mssSend.getRemoteRef() != null)
			validateRegex(REGEX_APPLICATIVE_DATA_FIELD_OR_REGEX_REMOTE_REF, mssSend.getRemoteRef(), "REMOTEREF");
		if (mssSend.getCatAppl() != null)
			validateRegex(REGEX_CATAPPL, mssSend.getCatAppl(), "CATAPPL");
		validateRegex(REGEX_MSGID, mssSend.getMsgId(), "MSGID");
		if (mssSend.getPriority() != null)
			validateRegex(REGEX_PRIORITY, mssSend.getPriority().toString(), "PRIORITY");
		if (mssSend.getComplete() != null)
			validateRegex(REGEX_ZERO, String.valueOf(mssSend.getComplete()), "COMPLETE");
		if (mssSend.getApplCheck() != null)
			validateRegex(REGEX_ZERO, String.valueOf(mssSend.getApplCheck()), "APPL_CHECK");
		if (mssSend.getMessageLeng() == null || mssSend.getMessageLeng() < 1)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "MESSAGESIZE"));
	}
	
	public void validateBaAndInterface(String localBaId, String remoteBaId, ServiceType service,
			RouteInterface routeInterface) throws ChcException {
		boolean valid = false;
		List<ConfigRouteInterface> entityList = routeInterfaceRepo.findByLocalBaIdAndRemoteBaIdAndService(localBaId,
				remoteBaId, service);
		if (entityList.isEmpty())
			throw new ChcException(I18nService.ERR_INVALID_BA);
		for (ConfigRouteInterface entity : entityList) {
			if (entity.getInterFace().equals(routeInterface))
				valid = true;
		}
		if (!valid)
			throw new ChcException(I18nService.ERR_INVALID_INTERFACE);
	}

	public Long validateSize(String fileName, Long isExpectedLength, Long maxSize, boolean isMessage)
			throws ChcException {
		try {
			long size = Files.size(Paths.get(fileName));
			CswLog.debug(log, String.format("expected: %s, calculated: %s", String.valueOf(isExpectedLength), String.valueOf(size)));

			if (!isMessage) {

				if (size == 0)
					throw new ChcException(I18nService.CSW_ERR_ZERO_FILE_LENGTH);

				if (size > maxSize)
					throw new ChcException(I18nService.CSW_ERR_FILE_LENGTH_EXCEEDED, maxSize);

				if (isExpectedLength != null && size != isExpectedLength)
					throw new ChcException(I18nCommon.ERR_FILE_SIZE_VALIDATION_EXCEPTION,
							String.format("FSIZE doesn't match file size of %s", size));

			} else {

				if (size > maxSize)
					throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
							String.format("MESSAGELENG is greater than max size. Size is: '%s'", size));

				if (isExpectedLength != null && size != isExpectedLength)
					throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
							String.format("Message size doesn't match MESSAGELENG. Size is: '%s'", size));
			}

			return Long.valueOf(size);
		} catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS);
		}
	}

	public String validateSha256(InputStream file, String sha256) throws ChcException {
		try {
			return validateHashCommon(sha256, DigestUtils.sha256(file));
		} catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, "failed to calculate the digest");
		}
	}
	
	public String validateSha256(byte[] file, String sha256) throws ChcException {
		return validateHashCommon(sha256, DigestUtils.sha256(file));
	}

	public String validateMD5(InputStream file, String md5) throws ChcException {
		try {
			return validateHashCommon(md5, DigestUtils.md5(file));
		} catch (IOException e) {
			throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, "failed to calculate the digest");
		}
	}

	public void validateFileName(String fileName) throws ChcException {
		if (fileName == null) {
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, "Validation failed due to FNAME");
		}
	}

	private String validateHashCommon(String expectedHashBase64, byte[] hash) throws ChcException {
		String base64Sha256 = Base64.getEncoder().encodeToString(hash);
		if (!StringUtils.isEmpty(expectedHashBase64) && !base64Sha256.equals(expectedHashBase64)) {
			CswLog.error(log, String.format("Hash not valid, expected: %s, actual: %s", expectedHashBase64, base64Sha256));
			
			throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, "hash not valid");
		}
		return base64Sha256;
	}

	protected void validateRegex(String regex, String value, String fieldName) throws ChcException {
		Pattern p = Pattern.compile(regex);
		if (value == null) {
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", fieldName));
		}
		Matcher m = p.matcher(value);
		if (!m.matches())
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", fieldName));
	}
}
