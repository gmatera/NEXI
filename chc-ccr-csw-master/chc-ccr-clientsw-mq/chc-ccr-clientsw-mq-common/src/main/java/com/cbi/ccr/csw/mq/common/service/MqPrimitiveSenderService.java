package com.cbi.ccr.csw.mq.common.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.InvalidDestinationException;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;

import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePoolRepository;
import com.cbi.ccr.csw.dto.fms.MSSInboundMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1401SecSendFilecnf;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1402SecSendFileind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1405SecReceiveind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1406SecReleasereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1407SecReleasecnf;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1413SecPosCreateFileind;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1911SecSendMsgreq;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1921SecSendMsgcnf;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1941SecSendMsgind;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1951SecReceiveMsgind;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1991SecNotAckMsgreq;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1992SecNotAckMsgcnf;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationMSSMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSRecvMQ;
import com.cbi.ccr.csw.mq.domain.mss.MSSSendMQ;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.util.DateUtils;
import com.cbi.frw.persistence.service.AbstractService;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import chc.framework.util.parsing.input.FlowioOutputMapper;
import liquibase.pro.packaged.en;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MqPrimitiveSenderService extends AbstractService {

	@Autowired
	private CommonConfigurationServiceMQ configurationService;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private PrimitivePoolRepository primitivePoolRepository;

//	@Autowired
//	private FileQueueUtil fileQueueUtil;

	@PersistenceContext
	protected EntityManager entityManager;

	private FlowioOutputMapper<MQ1401SecSendFilecnf> flowioMapper1401;
	private FlowioOutputMapper<MQ1941SecSendMsgind> flowioMapper1941;
	private FlowioOutputMapper<MQ1402SecSendFileind> flowioMapper1402;
	private FlowioOutputMapper<MQ1413SecPosCreateFileind> flowioMapper1413;
	private FlowioOutputMapper<MQ1407SecReleasecnf> flowioMapper1407;
	private FlowioOutputMapper<MQ1992SecNotAckMsgcnf> flowioMapper1992;

	@PostConstruct
	public void initBinders() {
		flowioMapper1401 = new FlowioOutputMapper<>(BinderFactory.binder1401());
		flowioMapper1402 = new FlowioOutputMapper<>(BinderFactory.binder1402());
		flowioMapper1941 = new FlowioOutputMapper<>(BinderFactory.binder1941());
		flowioMapper1413 = new FlowioOutputMapper<>(BinderFactory.binder1413());
		flowioMapper1407 = new FlowioOutputMapper<>(BinderFactory.binder1407());
		flowioMapper1992 = new FlowioOutputMapper<>(BinderFactory.binder1992());
	}
	@Transactional
	public void send1401Confirm(@NonNull FMSSendMQ entity, MQ1400SecSendFilereq primitiveDto, String message,
			LocalDateTime acceptTms) throws ChcRollbackException, InvalidDestinationException {

		try {
			MQ1401SecSendFilecnf confirm = mapper.map(primitiveDto, MQ1401SecSendFilecnf.class);

			confirm.setId("1401");
			confirm.setBaLoc(primitiveDto.getBaLoc());
			confirm.setBaRem(primitiveDto.getBaRem());
			confirm.setSendType(primitiveDto.getSendType());
			confirm.setSyncFlag(primitiveDto.getSyncFlag());
			confirm.setCorrId(primitiveDto.getCorrId());
			confirm.setVfn(StringUtils.isEmpty(primitiveDto.getVfn()) ? entity.getVfn() : primitiveDto.getVfn());
			confirm.setGroupId(primitiveDto.getGroupId());
			confirm.setBaFileSize(primitiveDto.getBaFileSize());
			confirm.setQueueFileName(primitiveDto.getQueueFileName());

			if (primitiveDto.getCharType().equals(CodePage.O.getValueMq())) {
				confirm.setCharType(entity.getCharType().getValueMq());
			} else {
				confirm.setCharType(primitiveDto.getCharType());
			}

			if (primitiveDto.getLineSeparator().equals(LineSeparator.O.getLabelMq())) {
				if (entity.getLineSeparator() == null)
					throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "LineSeparator not configured");
				confirm.setLineSeparator(entity.getLineSeparator().getLabelMq());
			} else {
				confirm.setLineSeparator(primitiveDto.getLineSeparator());
			}

			if (primitiveDto.getRecType().equals(RecordFormat.O.getLabelMQ())) {
				if (entity.getRecordFormat() == null)
					throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "Record Format not configured");
				confirm.setRecType(entity.getRecordFormat().getLabelMQ());
			} else {
				confirm.setRecType(primitiveDto.getRecType());
			}

			if (primitiveDto.getMaxRecLen() == 0) {
				if (entity.getMaxRecLen() == null)
					throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "Max Record Length not configured");
				confirm.setMaxRecLen(entity.getMaxRecLen());
			} else {
				confirm.setMaxRecLen(primitiveDto.getMaxRecLen());
			}

			confirm.setUdrLen(primitiveDto.getUdrLen());
			confirm.setUdr(primitiveDto.getUdr());
			confirm.setMsgType(primitiveDto.getMsgType());
			confirm.setTur(primitiveDto.getTur());
			confirm.setCatAppl(primitiveDto.getCatAppl());
			confirm.setLocalBaData(primitiveDto.getLocalBaData());
			confirm.setSndBaFileDigestAlg(primitiveDto.getSndBaFileDigestAlg());
			confirm.setSndBaFileDigestLen(primitiveDto.getSndBaFileDigestLen());
			confirm.setSndBaFileDigest(primitiveDto.getSndBaFileDigest());
			confirm.setMabDigestAlg(primitiveDto.getMabDigestAlg());
			confirm.setMabDigestLen(primitiveDto.getMabDigestLen());
			confirm.setMabDigest(primitiveDto.getMabDigest());
			confirm.setLocalAuthInfoAlg(primitiveDto.getLocalAuthInfoAlg());
			confirm.setLocalAuthInfoLen(primitiveDto.getLocalAuthInfoLen());
			confirm.setLocalAuthInfo(primitiveDto.getLocalAuthInfo());

			if (StringUtils.isEmpty(message)) {
				confirm.setResult(0);
				confirm.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(acceptTms));
			} else {
				confirm.setResult(1);
				confirm.setRejReason(entity.getRejectReason());
			}

			debugPrimitive1401(confirm);

			byte[] primitiveByte = flowioMapper1401.writeByte(confirm);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(confirm.getId());
			p.setServiceType((confirm.getSyncFlag() == 1) ? ServiceType.FMS : ServiceType.FTS);
			p.setEntityId(entity.getId());
			
			storePrimitiveOnDB(primitiveByte, p);

		} catch (ChcException e) {
			throw new ChcRollbackException(I18nService.ERR_MISSING_CONFIGURATION, e, e.toString());
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}

	}

	private void send1402Common(long entityId, MQ1402SecSendFileind primitive)
			throws NoSuchFieldException, InvalidDestinationException {

		byte[] primitiveByte = flowioMapper1402.writeByte(primitive);

		debugPrimitive1402(primitive, flowioMapper1402);

		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(primitive.getId());
		p.setServiceType((primitive.getSyncFlag() == 1) ? ServiceType.FMS : ServiceType.FTS);
		p.setEntityId(entityId);
		
		storePrimitiveOnDB(primitiveByte, p);
	}
	@Transactional
	public void send1402(FMSSendMQ entity, String message, String cmdTms) {
		MQ1402SecSendFileind primitive;
		try {
			primitive = MqPrimitiveService.get1402PrimitiveFMS(entity, message, cmdTms,
					configurationService.loadFMSMQConfiguration(entity.getLocalBaId(), entity.getRemoteBaId()));
			send1402Common(entity.getId(), primitive);
		} catch (TransactionException e) {
			throw new ChcRollbackException(I18nCommon.ERR_DATABASE_CONNECTION, e, e.toString());
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}
	@Transactional
	public void send1402(FTSSendMQ entity, String message, String cmdTms) {
		entity.setAcceptTime(LocalDateTime.now());
		MQ1402SecSendFileind primitive;
		try {
			primitive = MqPrimitiveService.get1402PrimitiveFTS(entity, message, cmdTms, configurationService
					.loadFTSMQConfiguration(entity.getLocalBaId(), entity.getRemoteBaId()));
			send1402Common(entity.getId(), primitive);
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}
	@Transactional
	public void send1407(long entityId, Integer rejectReason, MQ1406SecReleasereq primitiveDTO, ZonedDateTime siStdProcessTms) {
		try {
			MQ1407SecReleasecnf mq1407 = MqPrimitiveService.get1407PrimitiveMq(rejectReason, primitiveDTO,
					siStdProcessTms);

			if (rejectReason != null) {
				CswLog.info(log, String.format("Primitive MQ 1407 Reject Reason: %s", rejectReason));
			}

			debugPrimitive1407(mq1407);

			byte[] primitiveByte = flowioMapper1407.writeByte(mq1407);
			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(mq1407.getId());
			p.setServiceType((primitiveDTO.getSyncFlag() == 1) ? ServiceType.FMS : ServiceType.FTS);
			p.setEntityId(entityId);
			
			storePrimitiveOnDB(primitiveByte, p);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	protected void debugPrimitive1407(MQ1407SecReleasecnf primitive) throws NoSuchFieldException {

		if (!configurationService.isDebugPrimitiveMq())
			return;

		try (FileOutputStream fos = new FileOutputStream(getTemp("1407"))) {
			fos.write(flowioMapper1407.writeByte(primitive));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send1413Common(long entityId, MQ1413SecPosCreateFileind primitive) {

		try {
			byte[] primitiveByte = flowioMapper1413.writeByte(primitive);
			debugPrimitive1413(primitive, flowioMapper1413);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(primitive.getId());
			p.setServiceType((primitive.getSyncFlag() == 1) ? ServiceType.FMS : ServiceType.FTS);
			p.setEntityId(entityId);
			
			storePrimitiveOnDB(primitiveByte, p);
		} catch (NoSuchFieldException e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		} catch (TransactionException e) {
			throw new ChcRollbackException(I18nCommon.ERR_DATABASE_CONNECTION, e, e.toString());
		}
	}
	@Transactional
	public void send1413(FMSSendMQ entity, MQ1400SecSendFilereq primitiveDto) {
		try {
			if (entity.getSendType() == 0) {
				entity.setAcceptTime(LocalDateTime.now());
				MQ1413SecPosCreateFileind primitive = MqPrimitiveService.get1413PrimitiveFMS(entity, primitiveDto);
				send1413Common(entity.getId(), primitive);
			}
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}
	@Transactional
	public void send1413(FTSSendMQ entity, MQ1400SecSendFilereq primitiveDto) {
		try {
			if (entity.getSendType() == 0) {
				entity.setAcceptTime(LocalDateTime.now());
				MQ1413SecPosCreateFileind primitive = MqPrimitiveService.get1413PrimitiveFTS(entity, primitiveDto);
				send1413Common(entity.getId(), primitive);
			}
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}
	@Transactional
	public void send1401ErrorWithPrimitive(long entityId, @NonNull MQ1401SecSendFilecnf confirm) {
		try {

			byte[] primitiveByte = flowioMapper1401.writeByte(confirm);
			
			debugPrimitive1401(confirm);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(confirm.getId());
			p.setServiceType((confirm.getSyncFlag() == 1) ? ServiceType.FMS : ServiceType.FTS);
			p.setEntityId(entityId);
			storePrimitiveOnDB(primitiveByte, p);
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}

	private String createVfn(@NonNull MQ1400SecSendFilereq dto) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyMMdd");
		return dto.getBaLoc() + dto.getBaRem() + LocalDate.now().format(df) + RandomStringUtils.random(2, true, true);
	}
	@Transactional
	public void send1401Confirm(@NonNull FTSSendMQ entity, MQ1400SecSendFilereq primitiveDto, String message,
			LocalDateTime acceptTms) {

		try {
			MQ1401SecSendFilecnf confirm = new MQ1401SecSendFilecnf();
			confirm.setBaLoc(primitiveDto.getBaLoc());
			confirm.setBaRem(primitiveDto.getBaRem());
			confirm.setSendType(primitiveDto.getSendType());
			confirm.setSyncFlag(primitiveDto.getSyncFlag());
			confirm.setCorrId(primitiveDto.getCorrId());
//			confirm.setVfn(StringUtils.isEmpty(primitiveDto.getVfn()) ? createVfn(primitiveDto) : primitiveDto.getVfn());
			confirm.setVfn(StringUtils.isEmpty(primitiveDto.getVfn()) ? entity.getVfn() : primitiveDto.getVfn());

			if (primitiveDto.getSendType() != null) {
				confirm.setGroupId(primitiveDto.getGroupId());
				confirm.setBaFileSize(primitiveDto.getBaFileSize());
				confirm.setQueueFileName(primitiveDto.getQueueFileName());
			}

			if (primitiveDto.getCharType().equals(CodePage.O.getValueMq())) {
				confirm.setCharType(entity.getCharType().getValueMq());
			} else {
				confirm.setCharType(primitiveDto.getCharType());
			}

			if (primitiveDto.getLineSeparator().equals(LineSeparator.O.getLabelMq())) {
				if (entity.getLineSeparator() == null)
					throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "LineSeparator not configured");
				confirm.setLineSeparator(entity.getLineSeparator().getLabelMq());
			} else {
				confirm.setLineSeparator(primitiveDto.getLineSeparator());
			}

			if (primitiveDto.getRecType().equals(RecordFormat.O.getLabelMQ())) {
				if (entity.getRecordFormat() == null)
					throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "RecordFormat not configured");
				confirm.setRecType(entity.getRecordFormat().getLabelMQ());
			} else {
				confirm.setRecType(primitiveDto.getRecType());
			}

			if (primitiveDto.getMaxRecLen() == 0) {
				if (entity.getMaxRecLen() == null)
					throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, "MaxRecordLength not configured");
				confirm.setMaxRecLen(entity.getMaxRecLen());
			} else {
				confirm.setMaxRecLen(primitiveDto.getMaxRecLen());
			}

			confirm.setUdrLen(primitiveDto.getUdrLen());
			confirm.setUdr(primitiveDto.getUdr());
			confirm.setTur(primitiveDto.getTur());
			confirm.setMsgType(primitiveDto.getMsgType());
			confirm.setCatAppl(primitiveDto.getCatAppl());
			confirm.setLocalBaData(primitiveDto.getLocalBaData());

			if (primitiveDto.getSndBaFileDigest() != null) {
				confirm.setSndBaFileDigestAlg(primitiveDto.getSndBaFileDigestAlg());
				confirm.setSndBaFileDigestLen(primitiveDto.getSndBaFileDigestLen());
				confirm.setSndBaFileDigest(primitiveDto.getSndBaFileDigest());
			}
			if (primitiveDto.getMabDigest() != null) {
				confirm.setMabDigestAlg(primitiveDto.getMabDigestAlg());
				confirm.setMabDigestLen(primitiveDto.getMabDigestLen());
				confirm.setMabDigest(primitiveDto.getMabDigest());
			}
			if (primitiveDto.getLocalAuthInfo() != null) {
				confirm.setLocalAuthInfoAlg(primitiveDto.getLocalAuthInfoAlg());
				confirm.setLocalAuthInfoLen(primitiveDto.getLocalAuthInfoLen());
				confirm.setLocalAuthInfo(primitiveDto.getLocalAuthInfo());
			}

			if (StringUtils.isEmpty(message)) {
				confirm.setResult(0);
				confirm.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(LocalDateTime.now()));
			} else {
				confirm.setResult(1);
				confirm.setRejReason(entity.getRejectReason());
			}

			byte[] primitiveByte = flowioMapper1401.writeByte(confirm);
			debugPrimitive1401(confirm);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(confirm.getId());
			p.setServiceType((confirm.getSyncFlag() == 1) ? ServiceType.FMS : ServiceType.FTS);
			p.setEntityId(entity.getId());
			
			storePrimitiveOnDB(primitiveByte, p);
		} catch (ChcException e) {
			throw new ChcRollbackException(I18nService.ERR_MISSING_CONFIGURATION, e, e.toString());
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}

	}
	@Transactional
	public void send1921Confirm(@NonNull MSSSendMQ entity, MQ1911SecSendMsgreq primitive, String message,
			LocalDateTime acceptTms) {

		try {
			MQ1921SecSendMsgcnf confirm = new MQ1921SecSendMsgcnf();
			confirm.setLocBaData(primitive.getLocBaData());
			CswLog.debug(log, String.format("LocBaData: %s", new String(confirm.getLocBaData())));

			confirm.setBaLoc(primitive.getBaLoc());
			confirm.setBaRem(primitive.getBaRem());
			confirm.setPriority(primitive.getPriority());
			confirm.setTur(primitive.getTur());
			confirm.setBaReqTms(primitive.getBaReqTms());
			confirm.setMsgType(primitive.getMsgType());
			confirm.setCatAppl(primitive.getCatAppl());
			confirm.setCorrId(primitive.getCorrId());
			confirm.setUdrLen(primitive.getUdrLen());
			confirm.setUdr(primitive.getUdr());

			if (!StringUtils.isEmpty(primitive.getMabDigest())) {
				confirm.setMabDigestAlg(primitive.getMabDigestAlg());
				confirm.setMabDigestLen(primitive.getMabDigestLen());
				confirm.setMabDigest(primitive.getMabDigest());
			}

			if (!StringUtils.isEmpty(primitive.getLocalAuthInfo())) {
				confirm.setReqLocalAuthInfoAlg(primitive.getLocalAuthInfoAlg());
				confirm.setReqLocalAuthInfoLen(primitive.getLocalAuthInfoLen());
				confirm.setReqLocalAuthInfo(primitive.getLocalAuthInfo());
			}

			if (StringUtils.isEmpty(message)) {
				confirm.setMabLen(0);
				confirm.setResult(0);
				confirm.setRejReason(0);
				confirm.setMsgId(entity.getId().intValue());
				confirm.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(acceptTms));
			} else {
				confirm.setMabLen(primitive.getMabLen());
				confirm.setMab(primitive.getMab());
				confirm.setResult(1);
				confirm.setRejReason(entity.getRejectReason());
			}

			FlowioOutputMapper<MQ1921SecSendMsgcnf> flowioMapper1921New = new FlowioOutputMapper<>(
					BinderFactory.binder1921(confirm.getMabLen()));

			byte[] primitiveByte = flowioMapper1921New.writeByte(confirm);
			debugPrimitive1921(confirm);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(confirm.getId());
			p.setServiceType(ServiceType.MSS);
			p.setEntityId(entity.getId());
			
			storePrimitiveOnDB(primitiveByte, p);
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}
	@Transactional
	public void send1921ErrorWithPrimitive(long entityId, MQ1921SecSendMsgcnf confirm) {
		try {

			CswLog.info(log, String.format("LocBaData: %s", new String(confirm.getLocBaData())));

			FlowioOutputMapper<MQ1921SecSendMsgcnf> flowioMapper1921 = new FlowioOutputMapper<>(
					BinderFactory.binder1921(confirm.getMabLen()));
			byte[] primitiveByte = flowioMapper1921.writeByte(confirm);

			debugPrimitive1921(confirm);
			
			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(confirm.getId());
			p.setServiceType(ServiceType.MSS);
			p.setEntityId(entityId);
			
			storePrimitiveOnDB(primitiveByte, p);

		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}
	@Transactional
	public void send1941(MSSSendMQ entity, String message) {
		entity.setAcceptTime(LocalDateTime.now());
		MQ1941SecSendMsgind primitive = MqPrimitiveService.get1941PrimitiveMSS(entity, message);
		try {
			byte[] primitiveByte = flowioMapper1941.writeByte(primitive);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(primitive.getId());
			p.setServiceType(ServiceType.MSS);
			p.setEntityId(entity.getId());
			
			storePrimitiveOnDB(primitiveByte, p);
		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}

	@Transactional
	public void send1951(@NonNull List<MSSRecvMQ> mssRecv, LocalDateTime tmsEndSending, LocalDateTime tmsCswSave,
			MSSInboundMessageDTO dto, ConfigurationMSSMQ configMSS, File decryptedMessage) {
		try {

			MQ1951SecReceiveMsgind mq1951 = new MQ1951SecReceiveMsgind();
			mq1951.setBaLoc(mssRecv.get(0).getLocalBaId());
			mq1951.setBaRem(mssRecv.get(0).getRemoteBaId());
			mq1951.setPriority((mssRecv.get(0).getPriority() == null) ? 0 : mssRecv.get(0).getPriority());
			mq1951.setMsgId(mssRecv.get(0).getId().intValue());
			if (mssRecv.get(0).getUdrLen() != null && mssRecv.get(0).getUdr() != null)
				mq1951.setUdr(mssRecv.get(0).getUdr());
			if (mssRecv.get(0).getUdrLen() != null)
				mq1951.setUdrLen(mssRecv.get(0).getUdrLen());
			mq1951.setTur(mssRecv.get(0).getTur());
			mq1951.setMsgType(mssRecv.get(0).getMessageType());
			mq1951.setCatAppl(mssRecv.get(0).getCatAppl());
			if (mssRecv.get(0).getMsgDigest() != null) {
				mq1951.setMabDigest(mssRecv.get(0).getMsgDigest());
				mq1951.setMabDigestAlg(mssRecv.get(0).getMsgDigestAlg());
				mq1951.setMabDigestLen(mq1951.getMabDigest().length());
			}
			mq1951.setMabLen((int) decryptedMessage.length());
			mq1951.setMab(new String(Files.readAllBytes(Paths.get(decryptedMessage.getAbsolutePath()))));

			if (mssRecv.get(0).getLocalAuthInfo() != null) {
				mq1951.setLocalAuthInfo(mssRecv.get(0).getLocalAuthInfo());
				mq1951.setLocalAuthInfoAlg(mssRecv.get(0).getLocalAuthInfoAlg());
				mq1951.setLocalAuthInfoLen(mq1951.getLocalAuthInfo().length());
			}
			mq1951.setHostFirstSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(dto.getTmsReceived()));
			mq1951.setFerSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(dto.getTmsStartSending()));
			mq1951.setFenDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(dto.getTmsStartSending()));
			mq1951.setHostFirstDelTms(DateUtils.fromLocalDateTimeToZonedDateTime(dto.getTmsStartSending()));
			mq1951.setFenSubTms(DateUtils.fromLocalDateTimeToZonedDateTime(tmsEndSending));
			mq1951.setFerDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(tmsEndSending));
			mq1951.setFerDlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(tmsEndSending));
			mq1951.setFirstBADlvTms(DateUtils.fromLocalDateTimeToZonedDateTime(tmsCswSave));

			if (configMSS.getRcvCompletionAlgo() != null && configMSS.getRcvCompletionAlgo()) {
				mssRecv.get(0).setStatus(RecvStatusMQ.CLEANABLE);
				mssRecv.get(0).setComplete(1);
			}

			FlowioOutputMapper<MQ1951SecReceiveMsgind> flowioMapper1951 = new FlowioOutputMapper<>(
					BinderFactory.binder1951(mq1951.getMabLen()));

			byte[] primitiveByte = flowioMapper1951.writeByte(mq1951);

			PrimitivePool p = new PrimitivePool();
			p.setPrimitiveId(mq1951.getId());
			p.setServiceType(ServiceType.MSS);
			p.setEntityId(mssRecv.get(0).getId());
			
			storePrimitiveOnDB(primitiveByte, p);

		} catch (Exception e) {
			throw new ChcRollbackException(I18nCommon.ERR_SYSTEM, e, e.toString());
		}
	}
	@Transactional
	public void send1992(long entityId, MQ1991SecNotAckMsgreq mq1991) throws NoSuchFieldException {
		MQ1992SecNotAckMsgcnf mq1992 = common1992(mq1991);
		mq1992.setResult(000);
		send1992JMS(entityId, mq1992);
	}

	@Transactional
	public void send1992Error(long entityId, MQ1991SecNotAckMsgreq primitiveDTO) throws NoSuchFieldException {
		MQ1992SecNotAckMsgcnf mq1992 = common1992(primitiveDTO);
		mq1992.setResult(001);
		mq1992.setRejReason(33);
		send1992JMS(entityId, mq1992);
	}

	private MQ1992SecNotAckMsgcnf common1992(MQ1991SecNotAckMsgreq mq1991) {
		MQ1992SecNotAckMsgcnf mq1992 = new MQ1992SecNotAckMsgcnf();
		mq1992.setReqUdr(mq1991.getUdr());
		mq1992.setReqUdrLen(mq1991.getUdrLen());
		mq1992.setReqUdr(mq1991.getUdr());
		mq1992.setReqBaLoc(mq1991.getBaLoc());
		mq1992.setReqBaRem(mq1991.getBaRem());
		mq1992.setReqLocalAuthInfo(mq1991.getLocalAuthInfo());
		mq1992.setReqLocalAuthInfoAlg(mq1991.getLocalAuthInfoAlg());
		mq1992.setReqLocalAuthInfoLen(mq1991.getLocalAuthInfoLen());
		mq1992.setReqBaProcessTms(mq1991.getBaProcessTms());
		return mq1992;
	}

	private void send1992JMS(long entityId, MQ1992SecNotAckMsgcnf mq1992) throws NoSuchFieldException {

		byte[] primitiveByte = flowioMapper1992.writeByte(mq1992);

		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1992.getId());
		p.setServiceType(ServiceType.MSS);
		p.setEntityId(entityId);		
		storePrimitiveOnDB(primitiveByte, p);
	}

	protected void debugPrimitive1401(MQ1401SecSendFilecnf confirm) {

		if (!configurationService.isDebugPrimitiveMq())
			return;

		try (FileOutputStream fos = new FileOutputStream(getTemp(MQ1401SecSendFilecnf.PRIMITIVE_ID))) {
			fos.write(flowioMapper1401.writeByte(confirm));
		} catch (Exception e) {
			CswLog.error(log, String.format("debug print primitive 1401 err: %s", e.toString()));
		}
	}

	protected void debugPrimitive1405(MQ1405SecReceiveind ind, FlowioOutputMapper<MQ1405SecReceiveind> flowioWriter) {

		if (!configurationService.isDebugPrimitiveMq())
			return;

		try (FileOutputStream fos = new FileOutputStream(getTemp(MQ1405SecReceiveind.PRIMITIVE_ID))) {
			fos.write(flowioWriter.writeByte(ind));
		} catch (Exception e) {
			CswLog.error(log, String.format("debug print primitive 1405 err:%s", e.toString()));
		}
	}

	private void debugPrimitive1413(MQ1413SecPosCreateFileind primitive,
			FlowioOutputMapper<MQ1413SecPosCreateFileind> flowioWriter) {
		if (!configurationService.isDebugPrimitiveMq())
			return;
		try (FileOutputStream fos = new FileOutputStream(getTemp(MQ1413SecPosCreateFileind.PRIMITIVE_ID))) {
			fos.write(flowioWriter.writeByte(primitive));
		} catch (Exception e) {
			CswLog.error(log, String.format("debug print primitive 1413 err: %s", e.toString()));
		}
	}

	private void debugPrimitive1402(MQ1402SecSendFileind primitive,
			FlowioOutputMapper<MQ1402SecSendFileind> flowioWriter) {
		if (!configurationService.isDebugPrimitiveMq())
			return;

		try (FileOutputStream fos = new FileOutputStream(getTemp(MQ1402SecSendFileind.PRIMITIVE_ID))) {
			fos.write(flowioWriter.writeByte(primitive));
		} catch (Exception e) {
			CswLog.error(log, String.format("debug print primitive 1402 err: %s", e.toString()));
		}
	}

	private void debugPrimitive1921(MQ1921SecSendMsgcnf confirm) throws NoSuchFieldException {
		if (!configurationService.isDebugPrimitiveMq())
			return;

		try (FileOutputStream fos = new FileOutputStream(getTemp(MQ1921SecSendMsgcnf.PRIMITIVE_ID))) {
			FlowioOutputMapper<MQ1921SecSendMsgcnf> flowioMapper1921 = new FlowioOutputMapper<>(
					BinderFactory.binder1921(confirm.getMabLen()));
			fos.write(flowioMapper1921.writeByte(confirm));
		} catch (IOException e) {
			CswLog.error(log, String.format("debug print primitive 1921 err: %s", e.toString()));
		}
	}

	private static File getTemp(String id) {
		return new File(System.getProperty("java.io.tmpdir"), id + ".txt");
	}

	@Transactional
	public void storePrimitiveOnDB(byte[] primitiveByte, PrimitivePool p) {
//		PrimitivePool p2 = transactionTemplate.execute(t -> 
//			primitivePoolRepository.save(p)
//		);
//
//		transactionTemplate.executeWithoutResult(t -> {
//			BlobHelper.saveBlobMQ(BlobEntity.builder()
//					.key(p2.getId())
//					.keyColum(PrimitivePool.PROP_ID)
//					.tableName(PrimitivePool.TABLE_NAME)
//					.blobColum(PrimitivePool.PROP_POSITIVE_PRIMITIVE)
//					.build(), primitiveByte, entityManager);
//			entityManager.flush();
//		});
		
		PrimitivePool p2 = primitivePoolRepository.save(p);
		entityManager.flush();
		BlobHelper.saveBlobMQ(BlobEntity.builder()
				.key(p2.getId())
				.keyColum(PrimitivePool.PROP_ID)
				.tableName(PrimitivePool.TABLE_NAME)
				.blobColum(PrimitivePool.PROP_POSITIVE_PRIMITIVE)
				.build(), primitiveByte, entityManager);
		entityManager.flush();
	}
	
	public String generateGroupId() {
		return new BigInteger(12 * 8, new Random()).toString(16);
	}

	@Transactional
	public void storePrimitiveWithFile(byte[] primitivePositive, byte[] primitiveNegative, PrimitivePool p) {

//		PrimitivePool p2 = transactionTemplate.execute(t -> {
//			return primitivePoolRepository.save(p);
//		});
//
//		transactionTemplate.executeWithoutResult(t -> {
//			BlobHelper.saveBlobMQ(BlobEntity.builder()
//					.key(p2.getId())
//					.keyColum(PrimitivePool.PROP_ID)
//					.tableName(PrimitivePool.TABLE_NAME)
//					.blobColum(PrimitivePool.PROP_POSITIVE_PRIMITIVE)
//					.build(), primitivePositive, entityManager);
//			entityManager.flush();
//		});
//
//		transactionTemplate.executeWithoutResult(t -> {
//			BlobHelper.saveBlobMQ(BlobEntity.builder()
//					.key(p2.getId())
//					.keyColum(PrimitivePool.PROP_ID)
//					.tableName(PrimitivePool.TABLE_NAME)
//					.blobColum(PrimitivePool.PROP_NEGATIVE_PRIMITIVE)
//					.build(), primitiveNegative, entityManager);
//			entityManager.flush();
//		});
		
		PrimitivePool p2 =  primitivePoolRepository.save(p);
		entityManager.flush();
		BlobHelper.saveBlobMQ(BlobEntity.builder()
				.key(p2.getId())
				.keyColum(PrimitivePool.PROP_ID)
				.tableName(PrimitivePool.TABLE_NAME)
				.blobColum(PrimitivePool.PROP_POSITIVE_PRIMITIVE)
				.build(), primitivePositive, entityManager);
		entityManager.flush();
		
		BlobHelper.saveBlobMQ(BlobEntity.builder()
				.key(p2.getId())
				.keyColum(PrimitivePool.PROP_ID)
				.tableName(PrimitivePool.TABLE_NAME)
				.blobColum(PrimitivePool.PROP_NEGATIVE_PRIMITIVE)
				.build(), primitiveNegative, entityManager);
		entityManager.flush();
	}

}
