package com.cbi.ccr.csw.mq.common.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.jms.JMSException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.GlobalProperties;
import com.cbi.ccr.csw.domain.csw.config.GlobalPropertiesRepository;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.PropertiesEnum;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.FTSMessageDTO;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1402SecSendFileind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1405SecReceiveind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1406SecReleasereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1407SecReleasecnf;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1409SecReceiveFileInd;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1410SecReadFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1411SecReadFilecnf;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1412SecReadFileind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1413SecPosCreateFileind;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1934SecReleaseMsgcnf;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1941SecSendMsgind;
import com.cbi.ccr.csw.mq.common.mq.FileQueueUtil;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSFTSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFTSMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.mq.domain.i.CswEntityOutMqWithFile;
import com.cbi.ccr.csw.mq.domain.i.CswInboundMqWithFileEntity;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQ;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.util.DateUtils;

import liquibase.util.StringUtil;
import lombok.Getter;

@Service
public class MqPrimitiveService<E extends CswInboundMqWithFileEntity> {

	@Getter
	@Value("${enable_debug_primitive_mq}")
	private boolean debugPrimitiveMQ;

	@Autowired
	private FileQueueUtil fileQueueUtil;

	@Autowired
	private GlobalPropertiesRepository propertiesRepo;

	@Autowired
	protected MqPrimitiveConfigurationLoader mqConf;

	@Autowired
	protected JmsTemplate jmsTemplate;

	public static final byte[] LOW_VALUE = "x'00'".getBytes();

	public static MQ1402SecSendFileind get1402PrimitiveFMS(FMSSendMQ entity, String message, String cmdTms,
			ConfigurationFMSMQ configuration) throws ChcException {

		MQ1402SecSendFileind req1402 = new MQ1402SecSendFileind();
		req1402.setBaFileSize(entity.getFileSize() == null ? 0 : entity.getFileSize().intValue());
		req1402.setBaLoc(entity.getLocalBaId());
		req1402.setBaRem(entity.getRemoteBaId());
		req1402.setSyncFlag(1);
		req1402.setCorrId(entity.getCorrelationId());
		req1402.setVfn(entity.getVfn());
		req1402.setUdrLen(entity.getUdrLen());
		req1402.setUdr(entity.getUdr());
		req1402.setTur(entity.getTur());
		if (entity.getLocalBaData() != null)
			req1402.setLocBaData(entity.getLocalBaData().getBytes());
		req1402.setTransferId(entity.getTransferId());

		setLineSeparatorRecTypeAndCharType(entity, configuration, req1402);

		req1402.setMaxRecLen(entity.getMaxRecLen());
		req1402.setBaFileSize(entity.getFileSize() == null ? 0 : entity.getFileSize().intValue());
		req1402.setNetFileSize(entity.getFileSize() == null ? 0 : entity.getFileSize().intValue());

		if (entity.getCompleteTime() != null)
			req1402.setCompleteTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));

		req1402.setFileApplDataDigest(entity.getFileDigest());
		req1402.setFileApplDataDigestAlg(entity.getFileDigestAlg());
		if (req1402.getFileApplDataDigest() != null)
			req1402.setFileApplDataDigestLen(req1402.getFileApplDataDigest().length());
		req1402.setNetFileDigest(entity.getFileDigest());
		req1402.setNetFileDigestAlg(entity.getFileDigestAlg());
		if (req1402.getNetFileDigest() != null)
			req1402.setNetFileDigestLen(req1402.getNetFileDigest().length());
		req1402.setMabDigest(entity.getMsgDigest());
		req1402.setMabDigestAlg(entity.getMsgDigestAlg());
		if (req1402.getMabDigest() != null)
			req1402.setMabDigestLen(req1402.getMabDigest().length());
		req1402.setLocalAuthInfo(entity.getLocalAuthInfo());
		req1402.setLocalAuthInfoAlg(entity.getLocalAuthInfoAlg());
		if (req1402.getLocalAuthInfo() != null)
			req1402.setLocalAuthInfoLen(req1402.getLocalAuthInfo().length());

		if (StringUtil.isEmpty(message)) {
			req1402.setResult(0);
			req1402.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getAcceptTime()));
			if (entity.getSendTime() != null) {
				req1402.setHostFirstSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
				req1402.setHostSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
				req1402.setHostLastSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
				req1402.setFerFirstSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
				req1402.setFerLastSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
				req1402.setFermsSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
			}
			if (entity.getCompleteTime() != null) {
				req1402.setFenFirstSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
				req1402.setFenFirstDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
				req1402.setFenLastSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
				req1402.setFenLastDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
				req1402.setFenmsSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
				req1402.setFenmsDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
			}
			req1402.setNegDetailedOper("0");

			if (cmdTms != null) {
				ZonedDateTime tms = DateUtils.parseZonedDateTime(cmdTms, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				req1402.setFerLastDlvTms(tms);
				req1402.setFerFirstDlvTms(tms);
				req1402.setHostFirstDlvTms(tms);
				req1402.setFermsDlvTms(tms);
				req1402.setFirstAckMSTms(tms);
				req1402.setCompleteTms(tms);
			}
		} else {
			if(entity.getStatus().equals(SendStatusMQ.CREATE_ERROR))
				req1402.setNegDetailedOper("6");
			else
				req1402.setNegDetailedOper("7");
			
			req1402.setResult(3);
//			req1402.setNegDetailedOper("6");
			req1402.setRejReason(entity.getRejectReason());

			req1402.setErrorTms(DateUtils.fromLocalDateTimeToZonedDateTime(
					(entity.getErrorTimestamp() != null) ? entity.getErrorTimestamp() : LocalDateTime.now()));
		}
		if (entity.getStartCreateTimestamp() != null)
			req1402.setStartCreateTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getStartCreateTimestamp()));
		if (entity.getEndCreateTimestamp() != null)
			req1402.setEndCreateTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getEndCreateTimestamp()));

		return req1402;
	}

	public static MQ1402SecSendFileind get1402PrimitiveFTS(FTSSendMQ entity, String message, String cmdTms,
			ConfigurationFTSMQ configuration) throws ChcException {

		MQ1402SecSendFileind req1402 = new MQ1402SecSendFileind();
		req1402.setStartCreateTms(ZonedDateTime.now());

		if (entity.getFileSize() != null)
			req1402.setBaFileSize(entity.getFileSize().intValue());
		req1402.setBaLoc(entity.getLocalBaId());
		req1402.setBaRem(entity.getRemoteBaId());
		req1402.setSyncFlag(0);
		req1402.setCorrId(entity.getCorrelationId());
		req1402.setVfn(entity.getVfn());

		if (entity.getLocalBaData() != null)
			req1402.setLocBaData(entity.getLocalBaData().getBytes());

		req1402.setTransferId(entity.getTransferId());

		setLineSeparatorRecTypeAndCharType(entity, configuration, req1402);

		req1402.setMaxRecLen(entity.getMaxRecLen());

		if (entity.getFileSize() != null)
			req1402.setBaFileSize(entity.getFileSize().intValue());

		if (entity.getCompleteTime() != null)
			req1402.setCompleteTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));

		req1402.setNetFileSize(entity.getFileSize() == null ? 0 : entity.getFileSize().intValue());

		req1402.setFileApplDataDigest(entity.getFileDigest());
		req1402.setFileApplDataDigestAlg(entity.getFileDigestAlg());

		if (req1402.getFileApplDataDigest() != null)
			req1402.setFileApplDataDigestLen(req1402.getFileApplDataDigest().length());
		req1402.setNetFileDigest(entity.getFileDigest());
		req1402.setNetFileDigestAlg(entity.getFileDigestAlg());

		if (req1402.getNetFileDigest() != null)
			req1402.setNetFileDigestLen(req1402.getNetFileDigest().length());

		req1402.setLocalAuthInfo(entity.getLocalAuthInfo());
		req1402.setLocalAuthInfoAlg(entity.getLocalAuthInfoAlg());

		if (req1402.getLocalAuthInfo() != null)
			req1402.setLocalAuthInfoLen(req1402.getLocalAuthInfo().length());

		if (StringUtil.isEmpty(message)) {
			req1402.setResult(0);
			req1402.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getAcceptTime()));
			req1402.setHostFirstSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
			req1402.setHostSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
			req1402.setHostLastSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
			req1402.setFerFirstSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
			req1402.setFerLastSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));
			req1402.setFermsSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getSendTime()));

			req1402.setFenFirstSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
			req1402.setFenFirstDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
			req1402.setFenLastSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
			req1402.setFenLastDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
			req1402.setFenmsSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
			req1402.setFenmsDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getCompleteTime()));
			req1402.setNegDetailedOper("0");

			if (cmdTms != null) {
				ZonedDateTime tms = DateUtils.parseZonedDateTime(cmdTms, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				req1402.setFerLastDlvTms(tms);
				req1402.setFerFirstDlvTms(tms);
				req1402.setHostFirstDlvTms(tms);
				req1402.setFermsDlvTms(tms);
				req1402.setFirstAckMSTms(tms);
				req1402.setCompleteTms(tms);
			}
		} else {
			if(entity.getStatus().equals(SendStatusMQ.CREATE_ERROR))
				req1402.setNegDetailedOper("6");
			else
				req1402.setNegDetailedOper("7");
			req1402.setResult(3);
//			req1402.setNegDetailedOper("6");
			req1402.setRejReason(entity.getRejectReason());
			req1402.setErrorTms(entity.getErrorTimestamp() != null
					? DateUtils.fromLocalDateTimeToZonedDateTime(entity.getErrorTimestamp())
					: ZonedDateTime.now());
		}
		req1402.setEndCreateTms(ZonedDateTime.now());

		return req1402;
	}

	private static <T extends CswEntityOutMqWithFile, U extends ConfigurationFMSFTSMQ> void setLineSeparatorRecTypeAndCharType(
			T entity, U configuration, MQ1402SecSendFileind req1402) throws ChcException {

		Integer lineSeparator;
		if (entity.getLineSeparator().equals(LineSeparator.O)) {
			if (configuration.getSndLineSeparator() == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "invalid configuration");
			lineSeparator = configuration.getSndLineSeparator().getLabelMq();
		} else {
			lineSeparator = entity.getLineSeparator().getLabelMq();
		}
		Integer recType;
		if (entity.getRecordFormat().equals(RecordFormat.O)) {
			if (configuration.getSndRecordFormat() == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "invalid configuration");
			recType = configuration.getSndRecordFormat().getLabelMQ();
		} else {
			recType = entity.getRecordFormat().getLabelMQ();
		}

		Integer charType;
		if (entity.getCharType().equals(CodePage.O)) {
			if (configuration.getSndCodePage() == null)
				throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "invalid configuration");
			charType = configuration.getSndCodePage().getValueMq();
		} else {
			charType = entity.getCharType().getValueMq();
		}

		req1402.setLineSeparator(lineSeparator);
		req1402.setRecType(recType);
		req1402.setCharType(charType);
	}

	public static MQ1941SecSendMsgind get1941PrimitiveMSS(MSSSendMQ entity, String message) {

		MQ1941SecSendMsgind req1941 = new MQ1941SecSendMsgind();
		req1941.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getAcceptTime()));
		req1941.setBaLoc(entity.getLocalBaId());
		req1941.setBaRem(entity.getRemoteBaId());
		req1941.setPriority(entity.getPriority());
		req1941.setTur(entity.getTur());
		req1941.setMsgType(entity.getMessageType());
		req1941.setCatAppl(entity.getCatAppl());
		req1941.setCorrId(entity.getCorrelationId());
		req1941.setMsgId(entity.getId().intValue());

		if (!StringUtils.isEmpty(entity.getUdr())) {
			req1941.setUdrLen(entity.getUdrLen());
			req1941.setUdr(entity.getUdr());
		}

		if (StringUtil.isEmpty(message)) {
			req1941.setResult(0);

			req1941.setHostFirstsubTms(ZonedDateTime.now());
			req1941.setFerSubTms(ZonedDateTime.now());

			req1941.setHostSubTms(ZonedDateTime.now());

			req1941.setFenSubTms(ZonedDateTime.now());
			req1941.setFenDlvTms(ZonedDateTime.now());

			req1941.setFerDlvTms(ZonedDateTime.now());
			req1941.setHostFirstDlvTms(ZonedDateTime.now());
			req1941.setCompleteTms(ZonedDateTime.now());
			req1941.setRejReason(0);

		} else {
			req1941.setResult(1);
			req1941.setRejReason(entity.getRejectReason());
		}

		if (!StringUtils.isEmpty(entity.getMsgDigest())) {
			req1941.setMabDigestAlg(entity.getMsgDigestAlg());
			req1941.setMabDigestLen(entity.getMsgDigestLen());
			req1941.setMabDigest(entity.getMsgDigest());
		}

		if (!StringUtils.isEmpty(entity.getLocalAuthInfo())) {
			req1941.setLocalAuthInfoAlg(entity.getLocalAuthInfoAlg());
			req1941.setLocalAuthInfoLen(entity.getLocalAuthInfoLen());
			req1941.setLocalAuthInfo(entity.getLocalAuthInfo());
		}
		return req1941;
	}

	public MQ1409SecReceiveFileInd get1409PrimitiveMqFMS(FMSRecvMQ fmsRecv, FMSMessageDTO dto) {
		MQ1409SecReceiveFileInd mq1409 = new MQ1409SecReceiveFileInd();
		mq1409.setBaLoc(fmsRecv.getLocalBaId());
		mq1409.setBaRem(fmsRecv.getRemoteBaId());
		mq1409.setSyncFlag(1);
		mq1409.setVfn(fmsRecv.getVfn());
		mq1409.setRecType(fmsRecv.getRecordFormat() == null ? RecordFormat.FIXED.getLabelMQ()
				: fmsRecv.getRecordFormat().getLabelMQ());
		mq1409.setMaxRecLen(fmsRecv.getMaxRecLen());
		mq1409.setCharType(fmsRecv.getSndCharType() == null ? CodePage.BINARY.getValueMq()
				: fmsRecv.getSndCharType().getValueMq());
		mq1409.setNetFileSize(fmsRecv.getNetFileSize());
		mq1409.setTransferId(fmsRecv.getTransferId());

		if (!StringUtils.isEmpty(fmsRecv.getLocalAuthInfo())) {
			mq1409.setLocalAuthInfo(fmsRecv.getLocalAuthInfo());
			mq1409.setLocalAuthInfoAlg(fmsRecv.getLocalAuthInfoAlg());
			mq1409.setLocalAuthInfoLen(new Long(mq1409.getLocalAuthInfo().length()));

		}

		mq1409.setHostFirstSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(dto.getTmsReceived()));

		mq1409.setFerFirstBSubTms(ZonedDateTime.now());
		mq1409.setFenFirstBDlvTms(ZonedDateTime.now());
		mq1409.setFerLastBSubTms(ZonedDateTime.now());
		mq1409.setFenLastBDlvTms(ZonedDateTime.now());

		if (mq1409.getSyncFlag() == 1) {
			mq1409.setFermsSubTms(ZonedDateTime.now());
			mq1409.setFenmsSubTms(ZonedDateTime.now());
		}

		mq1409.setFenFirstBSubTms(ZonedDateTime.now());
		mq1409.setFerFirstBDlvTms(ZonedDateTime.now());
		mq1409.setFenLastBSubTms(ZonedDateTime.now());
		mq1409.setFerLastBDeliveryTms(ZonedDateTime.now());

		if (mq1409.getSyncFlag() == 1) {
			mq1409.setFenmsDlvTms(ZonedDateTime.now());
			mq1409.setFermsDlvTms(ZonedDateTime.now());
		}

		mq1409.setHostFirstDlvTms(ZonedDateTime.now());
		mq1409.setFtsEndReceiveTms(ZonedDateTime.now());

		return mq1409;
	}

	public MQ1409SecReceiveFileInd get1409PrimitiveMqFTS(FTSRecvMQ ftsRecv, FTSMessageDTO dto) {
		MQ1409SecReceiveFileInd mq1409 = new MQ1409SecReceiveFileInd();
		mq1409.setBaLoc(ftsRecv.getLocalBaId());
		mq1409.setBaRem(ftsRecv.getRemoteBaId());
		mq1409.setSyncFlag(1);
		mq1409.setVfn(ftsRecv.getVfn());
		mq1409.setRecType(ftsRecv.getRecordFormat() == null ? RecordFormat.FIXED.getLabelMQ()
				: ftsRecv.getRecordFormat().getLabelMQ());
		mq1409.setMaxRecLen(ftsRecv.getMaxRecLen());
		mq1409.setCharType(ftsRecv.getSndCharType() == null ? CodePage.BINARY.getValueMq()
				: ftsRecv.getSndCharType().getValueMq());
		mq1409.setNetFileSize(ftsRecv.getNetFileSize());
		mq1409.setTransferId(ftsRecv.getTransferId());

		if (!StringUtils.isEmpty(ftsRecv.getLocalAuthInfo())) {
			mq1409.setLocalAuthInfo(ftsRecv.getLocalAuthInfo());
			mq1409.setLocalAuthInfoAlg(ftsRecv.getLocalAuthInfoAlg());
			mq1409.setLocalAuthInfoLen(new Long(mq1409.getLocalAuthInfo().length()));

		}
		mq1409.setHostFirstSubTms(ZonedDateTime.now());

		mq1409.setFerFirstBSubTms(ZonedDateTime.now());
		mq1409.setFenFirstBDlvTms(ZonedDateTime.now());
		mq1409.setFerLastBSubTms(ZonedDateTime.now());
		mq1409.setFenLastBDlvTms(ZonedDateTime.now());

		if (mq1409.getSyncFlag() == 1) {
			mq1409.setFermsSubTms(ZonedDateTime.now());
			mq1409.setFenmsSubTms(ZonedDateTime.now());
		}

		mq1409.setFenFirstBSubTms(ZonedDateTime.now());
		mq1409.setFerFirstBDlvTms(ZonedDateTime.now());
		mq1409.setFenLastBSubTms(ZonedDateTime.now());
		mq1409.setFerLastBDeliveryTms(ZonedDateTime.now());

		if (mq1409.getSyncFlag() == 1) {
			mq1409.setFenmsDlvTms(ZonedDateTime.now());
			mq1409.setFermsDlvTms(ZonedDateTime.now());
		}

		mq1409.setHostFirstDlvTms(ZonedDateTime.now());
		mq1409.setFtsEndReceiveTms(ZonedDateTime.now());

		return mq1409;
	}

	public MQ1411SecReadFilecnf get1411PrimitiveMq(E entity, String errorMessage, MQ1410SecReadFilereq primitiveDTO,
			boolean lauEnabled, LocalDateTime acceptTms) {
		if (entity instanceof FMSRecvMQ)
			return get1411PrimitiveMqFMS((FMSRecvMQ) entity, errorMessage, primitiveDTO, lauEnabled, acceptTms);
		else
			return get1411PrimitiveMqFTS((FTSRecvMQ) entity, errorMessage, primitiveDTO, lauEnabled, acceptTms);
	}

	public MQ1405SecReceiveind build1405PrimitiveMqFMS(FMSRecvMQ fmsRecv, String groupId, ConfigurationFMSMQ config,
			ZonedDateTime tmsEndSending, ZonedDateTime tmsStartWritingFileInQueue,
			ZonedDateTime tmsEndWritingFileInQueue, ZonedDateTime tmsCswSave, ZonedDateTime acceptTms,
			FMSMessageDTO dto, File decryptedMessage) throws ChcException, IOException {
		MQ1405SecReceiveind mq1405 = new MQ1405SecReceiveind();
		mq1405.setBaLoc(fmsRecv.getLocalBaId());
		mq1405.setBaRem(fmsRecv.getRemoteBaId());
		mq1405.setCorrId(fmsRecv.getCorrelationId());
		mq1405.setVfn(fmsRecv.getVfn());
		mq1405.setQueueFileName(config.getUploadQName());
		mq1405.setGroupId(groupId.getBytes());

		mq1405.setLineSeparatorRcv(fmsRecv.getLineSeparator() == null ? LineSeparator.NONE.getLabelMq()
				: fmsRecv.getLineSeparator().getLabelMq());

		mq1405.setRecType(fmsRecv.getRecordFormat() == null ? RecordFormat.FIXED.getLabelMQ()
				: fmsRecv.getRecordFormat().getLabelMQ());

		mq1405.setMaxRecLen(config.getSndMaxRecLength());
		mq1405.setBaFileSize(fmsRecv.getFileSize());

		mq1405.setSndCharType(fmsRecv.getSndCharType() == null ? CodePage.BINARY.getValueMq()
				: fmsRecv.getSndCharType().getValueMq());

		mq1405.setRcvCharType(fmsRecv.getRcvCharType() == null ? String.format("%s", CodePage.BINARY.getValueMq())
				: String.format("%s", fmsRecv.getRcvCharType().getValueMq()));

		mq1405.setNetFileSize(fmsRecv.getNetFileSize());
		mq1405.setUdrLen(fmsRecv.getUdrLen());
		mq1405.setUdr(fmsRecv.getUdr());
		mq1405.setTur(fmsRecv.getTur());
		mq1405.setMsgType(fmsRecv.getMessageType());
		mq1405.setCatAppl(fmsRecv.getCatAppl());
		mq1405.setTransferId(fmsRecv.getTransferId());
		mq1405.setRcvBaFileDigest(fmsRecv.getFileDigest());
		mq1405.setRcvBaFileDigestAlg(fmsRecv.getFileDigestAlg());
		mq1405.setRcvBaFileDigestLen(fmsRecv.getFileDigestLen());

		if (dto != null) {

			ZonedDateTime tmsReceived = null;
			ZonedDateTime tmsStartedSending = null;

			if (dto.getTmsReceived() != null)
				tmsReceived = DateUtils.fromLocalDateTimeToZonedDateTime(dto.getTmsReceived());

			if (dto.getTmsStartSending() != null)
				tmsStartedSending = DateUtils.fromLocalDateTimeToZonedDateTime(dto.getTmsStartSending());

			mq1405.setHostFirstSubTms(tmsReceived);
			mq1405.setFerFirstBSubTms(tmsStartedSending);
			mq1405.setFenFirstBDlvTms(tmsStartedSending);
			mq1405.setFerLastBSubTms(tmsStartedSending);
			mq1405.setFenLastBDlvTms(tmsStartedSending);
			mq1405.setFermsSubTms(tmsStartedSending);
			mq1405.setFenmsDlvTms(tmsStartedSending);
		}

		mq1405.setFenFirstBSubTms(tmsEndSending);
		mq1405.setFerFirstBDlvTms(tmsEndSending);
		mq1405.setFenLastBSubTms(tmsEndSending);
		mq1405.setFerLastBDlvTms(tmsEndSending);
		mq1405.setHostFirstDlvTms(tmsCswSave);
		mq1405.setFtsEndReceiveTms(tmsCswSave);

		mq1405.setFenmsSubTms(tmsEndSending);
		mq1405.setFermsDlvTms(tmsEndSending);
		mq1405.setAcceptTms(acceptTms);
		mq1405.setStartReadTms(tmsStartWritingFileInQueue);
		mq1405.setEndReadTms(tmsEndWritingFileInQueue);

		if (fmsRecv.getLocalBaData() != null)
			mq1405.setLocalBaData(fmsRecv.getLocalBaData().getBytes());

		if (!StringUtils.isEmpty(fmsRecv.getFileDigest())) {
			mq1405.setFileApplDataDigest(
					StringUtils.isEmpty(fmsRecv.getFileDigest()) ? null : fmsRecv.getFileDigest().getBytes());
			mq1405.setFileApplDataDigestAlg(fmsRecv.getFileDigestAlg());
			mq1405.setFileApplDataDigestLen(
					(mq1405.getFileApplDataDigest() == null) ? null : mq1405.getFileApplDataDigest().length);
		}

		mq1405.setRcvBaFileDigest(fmsRecv.getFileDigest());
		mq1405.setRcvBaFileDigestAlg(fmsRecv.getFileDigestAlg());
		mq1405.setRcvBaFileDigestLen(fmsRecv.getFileDigestLen());

		if (fmsRecv.getNetFileSize() != null) {
			mq1405.setNetFileDigest(fmsRecv.getFileDigest());
			mq1405.setNetFileDigestAlg(fmsRecv.getFileDigestAlg());
			mq1405.setNetFileDigestLen(fmsRecv.getFileDigestLen());
		}

		if (fmsRecv.getMsgDigest() != null) {
			mq1405.setMabDigest(fmsRecv.getMsgDigest());
			mq1405.setMabDigestAlg(fmsRecv.getMsgDigestAlg());
			mq1405.setMabDigestLen(fmsRecv.getMsgDigestLen());
		}

		if (decryptedMessage != null) {
			mq1405.setMabLen((int) decryptedMessage.length());
			mq1405.setMab(new String(Files.readAllBytes(Paths.get(decryptedMessage.getAbsolutePath()))));
		}
		if (fmsRecv.getLocalAuthInfo() != null) {
			mq1405.setLocalAuthInfoAlg(fmsRecv.getLocalAuthInfoAlg());
			mq1405.setLocalAuthInfoLen(
					(fmsRecv.getLocalAuthInfoLen() == null) ? null : fmsRecv.getLocalAuthInfoLen().intValue());
			mq1405.setLocalAuthInfo(fmsRecv.getLocalAuthInfo());
		}

		if (config.getRcvAutoRead() == null)
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "Rcv Auto read not present");

		if (Boolean.FALSE.equals(config.getRcvAutoRead()))
			mq1405.setAcceptTms(ZonedDateTime.now()); // solo se è NO AUTOREAD

		return mq1405;
	}

	public static MQ1407SecReleasecnf get1407PrimitiveMq(Integer rejectReason, MQ1406SecReleasereq primitiveDTO, ZonedDateTime siStdProcessTms) {

		MQ1407SecReleasecnf mq1407 = new MQ1407SecReleasecnf();
		mq1407.setReqBaLoc(primitiveDTO.getBaLoc());
		mq1407.setReqBaRem(primitiveDTO.getBaRem());
		mq1407.setReqSyncFlag(primitiveDTO.getSyncFlag());
		mq1407.setReqUdrLen(primitiveDTO.getUdrLen());
		mq1407.setReqUdr(primitiveDTO.getUdr());
		mq1407.setReqVfn(primitiveDTO.getVfn());
		mq1407.setReqBaProcessTms(primitiveDTO.getBaProcessTms());

		if (rejectReason == null) {
			mq1407.setResult(0);
			mq1407.setSiStdProcessTms(siStdProcessTms);
		} else {
			mq1407.setResult(1);
			mq1407.setRejReason(rejectReason);
		}
		if (StringUtils.isEmpty(primitiveDTO.getLocalAuthInfo())) {
			mq1407.setReqLocalAuthInfo(primitiveDTO.getLocalAuthInfo());
			mq1407.setReqLocalAuthInfoAlg(primitiveDTO.getLocalAuthInfoAlg());
			mq1407.setReqLocalAuthInfoLen(mq1407.getReqLocalAuthInfo().length());
			mq1407.setLocalAuthInfo(primitiveDTO.getLocalAuthInfo());
			mq1407.setLocalAuthInfoAlg(primitiveDTO.getLocalAuthInfoAlg());
			mq1407.setLocalAuthInfoLen(mq1407.getLocalAuthInfo().length());
		}

		return mq1407;
	}

	public static MQ1934SecReleaseMsgcnf get1934PrimitiveMq(MSSRecvMQ mssRecv) {
		MQ1934SecReleaseMsgcnf mq1934 = new MQ1934SecReleaseMsgcnf();
		mq1934.setReqBaLoc(mssRecv.getLocalBaId());
		mq1934.setReqBaRem(mssRecv.getRemoteBaId());
		mq1934.setReqUdrLen(mssRecv.getUdrLen());
		mq1934.setReqUdr(mssRecv.getUdr());
		mq1934.setReqUdr(mssRecv.getUdr());
		mq1934.setRejReason(mssRecv.getRejectReason());
		return mq1934;
	}

	public MQ1411SecReadFilecnf get1411PrimitiveMqFMS(FMSRecvMQ entity, String errorMessage,
			MQ1410SecReadFilereq primitiveDTO, boolean lauEnabled, LocalDateTime acceptTms) {
		MQ1411SecReadFilecnf mq1411 = new MQ1411SecReadFilecnf();
		mq1411.setReqBaLoc(entity.getLocalBaId());
		mq1411.setReqBaRem(entity.getRemoteBaId());
		mq1411.setReqReadType(primitiveDTO.getReadType());
		mq1411.setReqSyncFlag(primitiveDTO.getSyncFlag());
		mq1411.setReqCorrId(primitiveDTO.getCorrId());
		mq1411.setReqVfn(primitiveDTO.getVfn());
		mq1411.setReqQueueFileName(primitiveDTO.getQueueFileName());
		mq1411.setReqLineSeparatorRcv(primitiveDTO.getLineSeparator());
		if (entity.getRcvCharType() != null)
			mq1411.setReqRcvCharType(primitiveDTO.getRcvCharType());
		if (entity.getLocalBaData() != null)
			mq1411.setReqLocalBaData(entity.getLocalBaData().getBytes());

		mq1411.setReqRcvBaFileDigestLen(primitiveDTO.getRcvBaFileDigestLen());
		mq1411.setReqRcvBaFileDigestAlg(primitiveDTO.getRcvBaFileDigestAlg());

		if (StringUtil.isEmpty(errorMessage)) {
			mq1411.setResult(0);
			mq1411.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(acceptTms));
			mq1411.setRejReason(0);
		} else {
			mq1411.setResult(1);
			mq1411.setRejReason(entity.getRejectReason());
		}

		if (!StringUtils.isEmpty(primitiveDTO.getLocalAuthInfo())) {
			mq1411.setReqLocalAuthInfoAlg(primitiveDTO.getLocalAuthInfoAlg());
			mq1411.setReqLocalAuthInfoLen(primitiveDTO.getLocalAuthInfoLen());
			mq1411.setReqLocalAuthInfo(primitiveDTO.getLocalAuthInfo());
		} else {
			mq1411.setReqLocalAuthInfoLen(0);
		}
		if (lauEnabled) {
			mq1411.setLocalAuthInfoAlg(primitiveDTO.getLocalAuthInfoAlg());
			mq1411.setLocalAuthInfoLen(primitiveDTO.getLocalAuthInfoLen());
			mq1411.setLocalAuthInfo(primitiveDTO.getLocalAuthInfo());
		} else {
			mq1411.setLocalAuthInfoLen(0);
		}
		return mq1411;
	}

	public static MQ1411SecReadFilecnf get1411PrimitiveMqFTS(FTSRecvMQ entity, String errorMessage,
			MQ1410SecReadFilereq primitiveDTO, boolean lauEnabled, LocalDateTime acceptTms) {
		MQ1411SecReadFilecnf mq1411 = new MQ1411SecReadFilecnf();
		mq1411.setReqBaLoc(entity.getLocalBaId());
		mq1411.setReqBaRem(entity.getRemoteBaId());
		mq1411.setReqReadType(primitiveDTO.getReadType());
		mq1411.setReqSyncFlag(primitiveDTO.getSyncFlag());
		mq1411.setReqCorrId(primitiveDTO.getCorrId());
		mq1411.setReqVfn(primitiveDTO.getVfn());
		mq1411.setReqQueueFileName(primitiveDTO.getQueueFileName());
		mq1411.setReqLineSeparatorRcv(primitiveDTO.getLineSeparator());
		if (entity.getRcvCharType() != null)
			mq1411.setReqRcvCharType(primitiveDTO.getRcvCharType());
		if (entity.getLocalBaData() != null)
			mq1411.setReqLocalBaData(entity.getLocalBaData().getBytes());

		mq1411.setReqRcvBaFileDigestLen(primitiveDTO.getRcvBaFileDigestLen());
		mq1411.setReqRcvBaFileDigestAlg(primitiveDTO.getRcvBaFileDigestAlg());

		if (StringUtil.isEmpty(errorMessage)) {
			mq1411.setResult(0);
			mq1411.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(acceptTms));
			mq1411.setRejReason(0);
		} else {
			mq1411.setResult(1);
			mq1411.setRejReason(entity.getRejectReason());
		}

		if (!StringUtils.isEmpty(primitiveDTO.getLocalAuthInfo())) {
			mq1411.setReqLocalAuthInfoAlg(primitiveDTO.getLocalAuthInfoAlg());
			mq1411.setReqLocalAuthInfoLen(primitiveDTO.getLocalAuthInfoLen());
			mq1411.setReqLocalAuthInfo(primitiveDTO.getLocalAuthInfo());
		} else {
			mq1411.setReqLocalAuthInfoLen(0);
		}
		if (lauEnabled) {
			mq1411.setLocalAuthInfoAlg(primitiveDTO.getLocalAuthInfoAlg());
			mq1411.setLocalAuthInfoLen(primitiveDTO.getLocalAuthInfoLen());
			mq1411.setLocalAuthInfo(primitiveDTO.getLocalAuthInfo());
		} else {
			mq1411.setLocalAuthInfoLen(0);
		}
		return mq1411;
	}

	public static MQ1413SecPosCreateFileind get1413PrimitiveFMS(FMSSendMQ entity, MQ1400SecSendFilereq primitiveDto) {

		MQ1413SecPosCreateFileind req1413 = new MQ1413SecPosCreateFileind();
		if (entity.getStartCreateTimestamp() != null)
			req1413.setStartCreateTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getStartCreateTimestamp()));
		if (entity.getAcceptTime() != null)
			req1413.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getAcceptTime()));
		req1413.setBaLoc(entity.getLocalBaId());
		req1413.setBaRem(entity.getRemoteBaId());
		req1413.setSyncFlag(1);
		req1413.setVfn(entity.getVfn());
		req1413.setCorrId(entity.getCorrelationId());
		req1413.setQueueFileName(entity.getQuequeFileName());
		if (entity.getLocalBaData() != null)
			req1413.setLocalBaData(entity.getLocalBaData().getBytes());
		if (entity.getGroupId() != null) {
//			req1413.setGroupId(entity.getGroupId().getBytes());
			req1413.setGroupId(primitiveDto.getGroupId());
		}
		req1413.setLineSeparator(entity.getLineSeparator().getLabelMq());
		req1413.setRecType(entity.getRecordFormat().getLabelMQ());
		req1413.setMaxRecLen(entity.getMaxRecLen());
		if (entity.getFileSize() != null) {
			req1413.setBaFileSize(entity.getFileSize().intValue());
			req1413.setNetFileSize(entity.getFileSize().intValue());
		}
		if (entity.getEndCreateTimestamp() != null)
			req1413.setEndCreateTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getEndCreateTimestamp()));

		return req1413;
	}

	public static MQ1413SecPosCreateFileind get1413PrimitiveFTS(FTSSendMQ entity, MQ1400SecSendFilereq primitiveDto) {
		MQ1413SecPosCreateFileind req1413 = new MQ1413SecPosCreateFileind();
		if (entity.getStartCreateTimestamp() != null)
			req1413.setStartCreateTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getStartCreateTimestamp()));
		if (entity.getAcceptTime() != null)
			req1413.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getAcceptTime()));
		req1413.setBaLoc(entity.getLocalBaId());
		req1413.setBaRem(entity.getRemoteBaId());
		req1413.setSyncFlag(0);
		req1413.setVfn(entity.getVfn());
		req1413.setCorrId(entity.getCorrelationId());
		req1413.setQueueFileName(entity.getQuequeFileName());
		if (entity.getLocalBaData() != null)
			req1413.setLocalBaData(entity.getLocalBaData().getBytes());
		if (entity.getGroupId() != null) {
//			req1413.setGroupId(entity.getGroupId().getBytes());
			req1413.setGroupId(primitiveDto.getGroupId());
		}
		req1413.setLineSeparator(entity.getLineSeparator().getLabelMq());
		req1413.setRecType(entity.getRecordFormat().getLabelMQ());
		req1413.setMaxRecLen(entity.getMaxRecLen());
		if (entity.getFileSize() != null) {
			req1413.setBaFileSize(entity.getFileSize().intValue());
			req1413.setNetFileSize(entity.getFileSize().intValue());
		}
		if (entity.getEndCreateTimestamp() != null)
			req1413.setEndCreateTms(DateUtils.fromLocalDateTimeToZonedDateTime(entity.getEndCreateTimestamp()));
		return req1413;
	}

	public MQ1411SecReadFilecnf get1411PrimitveMq(E entity) {
		MQ1411SecReadFilecnf mq1411 = new MQ1411SecReadFilecnf();
		mq1411.setReqBaLoc(entity.getLocalBaId()); // valore della 1410
		mq1411.setReqBaRem(entity.getRemoteBaId()); // valore della 1410
		mq1411.setReqReadType(null); // valore della 1410

		mq1411.setReqVfn(entity.getVfn()); // valore della 1410
		mq1411.setReqQueueFileName(entity.getQueueFileName()); // valore della 1410

		mq1411.setAcceptTms(ZonedDateTime.now());
		mq1411.setResult(null);
		mq1411.setRejReason(entity.getRejectReason());

		return mq1411;
	}

	public MQ1412SecReadFileind build1412PrimitiveFTS(FTSRecvMQ ftsRecv, String groupId, String errorMessage,
			MQ1410SecReadFilereq primitiveDTO) {
		MQ1412SecReadFileind mq1412 = new MQ1412SecReadFileind();
		mq1412.setBaLoc(ftsRecv.getLocalBaId());
		mq1412.setBaRem(ftsRecv.getRemoteBaId());
		mq1412.setSyncFlag(0);
		mq1412.setCorrId(ftsRecv.getCorrelationId());
		mq1412.setVfn(ftsRecv.getVfn());

		if (primitiveDTO != null && primitiveDTO.getQueueFileName() != null)
			mq1412.setQueueFileName(primitiveDTO.getQueueFileName());

		if (!StringUtils.isEmpty(groupId))
			mq1412.setGroupId(groupId.getBytes());

		if (StringUtils.isEmpty(errorMessage))
			mq1412.setResult(0);
		else {
			mq1412.setResult(1);
			mq1412.setRejReason(ftsRecv.getRejectReason());
		}

		if (ftsRecv.getLineSeparator() != null)
			mq1412.setLineSeparatorRcv(ftsRecv.getLineSeparator().getLabelMq());

		if (ftsRecv.getRecordFormat() != null)
			mq1412.setRecType(ftsRecv.getRecordFormat().getLabelMQ());

		if (ftsRecv.getSndCharType() != null)
			mq1412.setSndCharType(ftsRecv.getSndCharType().getValueMq());

		if (ftsRecv.getRcvCharType() != null)
			mq1412.setRcvCharType(ftsRecv.getRcvCharType().getValueMq());

		mq1412.setBaFileSize(ftsRecv.getFileSize());
		mq1412.setNetFileSize(ftsRecv.getNetFileSize());
		mq1412.setTransferId(ftsRecv.getTransferId());

		if (ftsRecv.getLocalBaData() != null)
			mq1412.setLocalBaData(ftsRecv.getLocalBaData().getBytes());

		if (!StringUtils.isEmpty(ftsRecv.getFileDigest())) {
			mq1412.setFileApplDataDigest(ftsRecv.getFileDigest());
			mq1412.setFileApplDataDigestAlg(ftsRecv.getFileDigestAlg());
			mq1412.setFileApplDataDigestLen(new Long(mq1412.getFileApplDataDigest().length()));
		}

		if (!StringUtils.isEmpty(ftsRecv.getFileDigest())) {
			mq1412.setRcvBaFileDigest(ftsRecv.getFileDigest());
			mq1412.setRcvBaFileDigestAlg(ftsRecv.getFileDigestAlg());
			mq1412.setRcvBaFileDigestLen(new Long(mq1412.getRcvBaFileDigest().length()));
		}

		if (!StringUtils.isEmpty(ftsRecv.getFileDigest())) {
			mq1412.setNetFileDigest(ftsRecv.getFileDigest());
			mq1412.setNetFileDigestAlg(ftsRecv.getFileDigestAlg());
			mq1412.setNetFileDigestLen(new Long(mq1412.getNetFileDigest().length()));
		}

		if (!StringUtils.isEmpty(ftsRecv.getLocalAuthInfo())) {
			mq1412.setLocalAuthInfo(ftsRecv.getLocalAuthInfo());
			mq1412.setLocalAuthInfoAlg(ftsRecv.getLocalAuthInfoAlg());
			mq1412.setLocalAuthInfoLen(
					(ftsRecv.getLocalAuthInfoLen() != null) ? new Long(mq1412.getLocalAuthInfo().length()) : 0L);
		}
		return mq1412;
	}

	public MQ1412SecReadFileind build1412PrimitiveFMS(FMSRecvMQ fmsRecv, String groupId, String errorMessage) {
		MQ1412SecReadFileind mq1412 = new MQ1412SecReadFileind();
		mq1412.setBaLoc(fmsRecv.getLocalBaId());
		mq1412.setBaRem(fmsRecv.getRemoteBaId());
		mq1412.setVfn(fmsRecv.getVfn());
		if (!StringUtils.isEmpty(groupId))
			mq1412.setGroupId(groupId.getBytes());

		if (StringUtils.isEmpty(errorMessage))
			mq1412.setResult(0);
		else {
			mq1412.setResult(1);
		}
		mq1412.setRejReason(fmsRecv.getRejectReason());

		if (!fmsRecv.getSndCharType().equals(CodePage.BINARY)) {
			mq1412.setLineSeparatorRcv(fmsRecv.getLineSeparator().getLabelMq());
			mq1412.setRecType(fmsRecv.getRecordFormat().getLabelMQ());
			mq1412.setSndCharType(fmsRecv.getSndCharType().getValueMq());
			mq1412.setRcvCharType(fmsRecv.getRcvCharType().getValueMq());
		}

		if (fmsRecv.getRejectReason() != null)
			mq1412.setRejReason(fmsRecv.getRejectReason());

		mq1412.setGroupId(groupId.getBytes());
		mq1412.setBaFileSize(fmsRecv.getFileSize());
		mq1412.setNetFileSize(fmsRecv.getNetFileSize());
		
		if(fmsRecv.getLocalBaData() != null)
			mq1412.setLocalBaData(fmsRecv.getLocalBaData().getBytes());
		
		mq1412.setFileApplDataDigestAlg(fmsRecv.getFileDigestAlg());
		mq1412.setFileApplDataDigestLen(fmsRecv.getFileDigestLen());
		mq1412.setFileApplDataDigest(fmsRecv.getFileDigest());
		mq1412.setRcvBaFileDigestAlg(fmsRecv.getFileDigestAlg());
		mq1412.setRcvBaFileDigestLen(fmsRecv.getFileDigestLen());
		mq1412.setRcvBaFileDigest(fmsRecv.getFileDigest());
		mq1412.setNetFileDigestAlg(fmsRecv.getFileDigestAlg());
		mq1412.setNetFileDigestLen(fmsRecv.getFileDigestLen());
		mq1412.setNetFileDigest(fmsRecv.getFileDigest());
		mq1412.setLocalAuthInfoAlg(fmsRecv.getLocalAuthInfoAlg());
		mq1412.setLocalAuthInfoLen(fmsRecv.getLocalAuthInfoLen());
		mq1412.setLocalAuthInfo(fmsRecv.getLocalAuthInfo());
		return mq1412;
	}

}
