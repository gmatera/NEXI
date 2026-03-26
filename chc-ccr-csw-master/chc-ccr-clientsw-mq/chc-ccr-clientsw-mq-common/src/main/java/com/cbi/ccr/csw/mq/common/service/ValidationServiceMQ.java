package com.cbi.ccr.csw.mq.common.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1911SecSendMsgreq;
import com.cbi.ccr.csw.service.common.ValidationService;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;

import liquibase.util.StringUtil;

@Service
public class ValidationServiceMQ extends ValidationService{
	private static final String REGEX_MQ1400 = "(1400)";
	private static final String REGEX_MQ1911 = "(1911)";
	private static final String REGEX_SEND_TYPE = "( |0|1){1}"; // vedere lo spazio
	private static final String REGEX_SYNC_FLAG = "(0|1){1}";
	private static final String REGEX_CORR_ID = "[0-9A-Za-z()$#@.\\-+]{0,30}";
	private static final String CHAR_TYPE = "[0123]{1}";
	private static final String REGEX_LINESEPARATOR = "[01234567]{1}";
	private static final String REGEX_REC_TYPE = "[012]{1}";
	private static final String REGEX_GET_NOT_TYPE = "[123]{1}"; // vedere
	private static final String REGEX_GET_FILE_TYPE = "[1234]{1}"; // vedere
	private static final String REGEX_GET_MSG_TYPE = "[23]{1}"; // vedere
//	private static final String REGEX_COMPRESS_ALGO = "(EASCOMP|********|        )";
	private static final String REGEX_NUM_ELEM_OUT = "[1-20]{0,2}"; // vedere

	private static final String HS256 = "HS256";

	public void validateFMSFTSMQRegex(MQ1400SecSendFilereq fmsftsSendMq) throws ChcException {
		validateRegex(REGEX_MQ1400, fmsftsSendMq.getId(), "ID");
		validateRegex(REGEX_LOCAL_REMOTE_BA, fmsftsSendMq.getBaLoc(), "BA_LOC");
		validateRegex(REGEX_LOCAL_REMOTE_BA, fmsftsSendMq.getBaRem(), "BA_REM");
		
		if (fmsftsSendMq.getSendType() != null)
			validateRegex(REGEX_SEND_TYPE, fmsftsSendMq.getSendType().toString(), "SEND_TYPE");
		
		validateRegex(REGEX_SYNC_FLAG, fmsftsSendMq.getSyncFlag().toString(), "SYNC_FLAG");
		if (!StringUtils.isEmpty(fmsftsSendMq.getCorrId()))
			validateRegex(REGEX_CORR_ID, fmsftsSendMq.getCorrId(), "CORR_ID");
		if (fmsftsSendMq.getVfn() != null)
			validateRegex(REGEX_VFN, fmsftsSendMq.getVfn(), "VFN");

		if (fmsftsSendMq.getBaFileSize() > FILE_MAX_SIZE || fmsftsSendMq.getBaFileSize() < 0)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "BA_FILE_SIZE"));

		if (StringUtils.isEmpty(fmsftsSendMq.getQueueFileName()) || fmsftsSendMq.getQueueFileName().isEmpty())
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
					String.format("%s not valid", "QUEUE_FILE_NAME"));

		if (fmsftsSendMq.getGroupId() == null)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "GROUP_ID"));
		

		if(fmsftsSendMq.getLineSeparator() < 0 || fmsftsSendMq.getLineSeparator() > 7 || StringUtil.isWhitespace(fmsftsSendMq.getLineSeparator().toString()))
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "LINE_SEPARATOR"));
		validateRegex(REGEX_LINESEPARATOR, fmsftsSendMq.getLineSeparator().toString(), "LINE_SEPARATOR");
		
		if(fmsftsSendMq.getRecType() < 0 || fmsftsSendMq.getRecType() > 2)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "REC_TYPE"));
		validateRegex(REGEX_REC_TYPE, fmsftsSendMq.getRecType().toString(), "REC_TYPE");

		// maxRecLen
		if (fmsftsSendMq.getMaxRecLen() > 32767 || fmsftsSendMq.getMaxRecLen() < 0)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "MAX_REC_LEN"));

		if(fmsftsSendMq.getCharType() < 0 || fmsftsSendMq.getCharType() > 3)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "CHAR_TYPE"));
		validateRegex(CHAR_TYPE, fmsftsSendMq.getCharType().toString(), "CHAR_TYPE");
		
		if(fmsftsSendMq.getSyncFlag() == 1) {
			if (fmsftsSendMq.getUdrLen() > 80 || fmsftsSendMq.getUdrLen() < 0)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "UDR_LEN"));

			if (fmsftsSendMq.getUdr().length() != fmsftsSendMq.getUdrLen())
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "UDR_LEN"));
			validateRegex(REGEX_USER_DATA_REMOTE, fmsftsSendMq.getUdr(), "UDR");
		}
		if (!StringUtils.isEmpty(fmsftsSendMq.getTur()))
			validateRegex(REGEX_TUR, fmsftsSendMq.getTur(), "TUR");

		if(fmsftsSendMq.getSyncFlag() == 1 && StringUtils.isEmpty(fmsftsSendMq.getMsgType()))
			fmsftsSendMq.setMsgType("-");
		
		if (fmsftsSendMq.getSyncFlag() == 1)
			validateRegex(REGEX_MESSAGE_TYPE, fmsftsSendMq.getMsgType(), "MSG_TYPE");

		if (fmsftsSendMq.getSyncFlag() == 1 && fmsftsSendMq.getCatAppl() != null)
			validateRegex(REGEX_CATAPPL, fmsftsSendMq.getCatAppl(), "CATAPPL");

		if (!StringUtils.isEmpty(fmsftsSendMq.getExtraData())) {
			if (fmsftsSendMq.getExtraDataLen() != 0)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "EXTRA_DATA_LEN"));

			if (fmsftsSendMq.getExtraData().length() != fmsftsSendMq.getExtraDataLen())
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "EXTRA_DATA_LEN"));
		}

		
		if (!StringUtils.isEmpty(fmsftsSendMq.getSndBaFileDigestAlg()) && !fmsftsSendMq.getSndBaFileDigestAlg().equals(SHA_256)) 
			throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "SND_BA_FILE_DIGEST_ALG"));

		if(fmsftsSendMq.getSndBaFileDigestLen() != null) {
			if(fmsftsSendMq.getSndBaFileDigestLen() != 44 && fmsftsSendMq.getSndBaFileDigestLen() != 0) 
				throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "SND_BA_FILE_DIGEST_LEN"));
			
			if(!StringUtils.isEmpty(fmsftsSendMq.getSndBaFileDigest()) && fmsftsSendMq.getSndBaFileDigest().length() != fmsftsSendMq.getSndBaFileDigestLen())
				throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "SND_BA_FILE_DIGEST_LEN"));
		}
		
		
		if(!StringUtils.isEmpty(fmsftsSendMq.getMabDigestAlg()) && !fmsftsSendMq.getMabDigestAlg().equals(SHA_256))
			throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "MAB__DIGEST_ALG"));
		
		if(fmsftsSendMq.getMabDigestLen() != null) {
			if(fmsftsSendMq.getMabDigestLen() != 44 && fmsftsSendMq.getMabDigestLen() != 0)
				throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "MAB__DIGEST_LEN"));
		
			if(!StringUtils.isEmpty(fmsftsSendMq.getMabDigest()) && fmsftsSendMq.getMabDigest().length() != fmsftsSendMq.getMabDigestLen())
				throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "MAB__DIGEST_LEN"));
		}
		

		if (fmsftsSendMq.getSyncFlag() == 1 && fmsftsSendMq.getMabLen() == null)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "MAB_LEN"));
		
		if (fmsftsSendMq.getSyncFlag() == 1 && StringUtils.isEmpty(fmsftsSendMq.getMab())) {
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "MAB"));
		}
		
		
		if (fmsftsSendMq.getSyncFlag() == 1 && !StringUtils.isEmpty(fmsftsSendMq.getMab())) {
			if (fmsftsSendMq.getMabLen() > MESSAGE_MAX_SIZE || fmsftsSendMq.getMabLen() < 0)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "MAB_LEN"));

//			if(fmsftsSendMq.getMab().length() != fmsftsSendMq.getMabLen())
//				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "MAB_LEN"));
		}

		if(!StringUtils.isEmpty(fmsftsSendMq.getLocalAuthInfo())) {
			if (fmsftsSendMq.getLocalAuthInfoLen() != 0 && fmsftsSendMq.getLocalAuthInfoLen() != 44)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "LOCAL_AUTH_INFO_LEN"));

			if (fmsftsSendMq.getLocalAuthInfo().length() != fmsftsSendMq.getLocalAuthInfoLen())
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "LOCAL_AUTH_INFO_LEN"));

			if (!StringUtils.isEmpty(fmsftsSendMq.getLocalAuthInfoAlg())
					&& !fmsftsSendMq.getLocalAuthInfoAlg().equals(HS256))
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "LOCAL_AUTH_INFO_ALG"));
			validateRegex(HS256, fmsftsSendMq.getLocalAuthInfoAlg(), "LOCAL_AUTH_INFO_ALG");
		}
	}

	public void validateMSSMQRegex(MQ1911SecSendMsgreq mssSendMq) throws ChcException {
		validateRegex(REGEX_MQ1911, mssSendMq.getId(), "ID");
		validateRegex(REGEX_LOCAL_REMOTE_BA, mssSendMq.getBaLoc(), "BA_LOC");
		validateRegex(REGEX_LOCAL_REMOTE_BA, mssSendMq.getBaRem(), "BA_REM");
		
		if(mssSendMq.getPriority() == null) {
			mssSendMq.setPriority(-1);
		} else {
			validateRegex(REGEX_PRIORITY, mssSendMq.getPriority().toString(), "PRIORITY");
		}
		
		if (mssSendMq.getTur() != null)
			if (mssSendMq.getTur().length() > 16 || mssSendMq.getTur().length() < 0)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "TUR_LEN"));
			validateRegex(REGEX_TUR, mssSendMq.getTur(), "TUR");
			
		if (mssSendMq.getMsgType() != null)
			validateRegex(REGEX_MESSAGE_TYPE, mssSendMq.getMsgType(), "MSG_TYPE");
		if (mssSendMq.getCatAppl() != null)
			validateRegex(REGEX_CATAPPL, mssSendMq.getCatAppl(), "CATAPPL");
		if (mssSendMq.getCorrId() != null)
			validateRegex(REGEX_CORR_ID, mssSendMq.getCorrId(), "CORR_ID");
		if (mssSendMq.getUdr() != null && mssSendMq.getUdrLen() != null) {
			if (mssSendMq.getUdrLen() > 80 || mssSendMq.getUdrLen() < 0)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "UDR_LEN"));

			if (mssSendMq.getUdr().length() != mssSendMq.getUdrLen())
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "UDR_LEN"));
			validateRegex(REGEX_USER_DATA_REMOTE, mssSendMq.getUdr(), "UDR");
		}

//		if (mssSendMq.getExtraData() != null && mssSendMq.getExtraDataLen() != null) {
//			if (mssSendMq.getExtraDataLen() != 0)
//				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
//						String.format("%s not valid", "EXTRA_DATA_LEN"));
//
//			if (mssSendMq.getExtraData().length() != mssSendMq.getExtraDataLen())
//				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
//						String.format("%s not valid", "EXTRA_DATA_LEN"));
//		}

		if(!StringUtils.isEmpty(mssSendMq.getMabDigestAlg()) && !mssSendMq.getMabDigestAlg().equals(SHA_256))
			throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "MAB_DIGEST_ALG"));
		
		if(mssSendMq.getMabDigestLen() != null) {
			if(mssSendMq.getMabDigestLen() != 44 && mssSendMq.getMabDigestLen() != 0)
				throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "MAB_DIGEST_LEN"));
		
			if(!StringUtils.isEmpty(mssSendMq.getMabDigest()) && mssSendMq.getMabDigest().length() != mssSendMq.getMabDigestLen())
				throw new ChcException(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION, String.format("%s not valid", "MAB_DIGEST_LEN"));
		}
		
		if(mssSendMq.getMab() == null)
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "MAB"));
		if (!StringUtils.isEmpty(mssSendMq.getLocalAuthInfo())) {
			if (mssSendMq.getLocalAuthInfoLen() != 0 && mssSendMq.getLocalAuthInfoLen() != 44)
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "LOCAL_AUTH_INFO_LEN"));

			if (mssSendMq.getLocalAuthInfo().length() != mssSendMq.getLocalAuthInfoLen())
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "LOCAL_AUTH_INFO_LEN"));

			if (!mssSendMq.getLocalAuthInfoAlg().equals(HS256))
				throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION,
						String.format("%s not valid", "LOCAL_AUTH_INFO_ALG"));
			validateRegex(HS256, mssSendMq.getLocalAuthInfoAlg(), "LOCAL_AUTH_INFO_ALG");
		}
	}

}
