package com.cbi.ccr.csw.mq.outbound.listener;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1419SecNotAckFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1420SecNotAckFilecnf;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSSendMQRepository;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSSendMQRepository;
import com.cbi.ccr.csw.mq.domain.i.CswEntityOutMqWithFile;
import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.mq.outbound.service.MqServiceBase;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;

import chc.framework.util.parsing.input.FlowioInputMapper;
import chc.framework.util.parsing.input.FlowioOutputMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * This handler  works on the sender client software, 
 * waiting for the primitive 1419 to update all the entities to a status called CLEANABLE.
 * If the handler manage correctly the update, then a 1420 primitive is sent with Ok as status, 
 * otherwise the same primitive will contain the error
 *
 */
@Slf4j
@Service
public class Mq1419Handler extends MqServiceBase<MQ1419SecNotAckFilereq> {

	private FlowioInputMapper<MQ1419SecNotAckFilereq> flowioMapper1419;
	private FlowioOutputMapper<MQ1420SecNotAckFilecnf> flowioMapper1420;

	@Autowired
	private FMSSendMQRepository repositoryFMS;

	@Autowired
	private FTSSendMQRepository repositoryFTS;
	
	@Autowired
	private MqPrimitiveSenderService mqPrimitiveSenderService;

	@PostConstruct
	private void initBinder() {
		flowioMapper1419 = new FlowioInputMapper<>(BinderFactory.binder1419());
		flowioMapper1420 = new FlowioOutputMapper<>(BinderFactory.binder1420());
	}

	@Override
	protected FlowioInputMapper<MQ1419SecNotAckFilereq> getFlowioMapper() {
		return flowioMapper1419;
	}

	@Override
	protected MQ1419SecNotAckFilereq getPrimitiveDto(Byte[] data) throws ChcUnrecoverableException, PrimitiveLengthMismatchException {
		try {
			checkSize(flowioMapper1419, data.length);
			return flowioMapper1419.read(Arrays.asList(data));
		} catch (PrimitiveLengthMismatchException e) {
			throw e;
		} catch (Exception e) {
			throw new ChcUnrecoverableException(I18nService.ERR_PROCESSING_MESSAGE);
		}
	}

	@Override
	protected void processMessage(Byte[] bytedata) throws  ChcUnrecoverableException, PrimitiveLengthMismatchException{
		MQ1419SecNotAckFilereq primitiveDTO;
		primitiveDTO = getPrimitiveDto(bytedata);
		CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
		CswLog.getLogData().setService(ServiceEnum.FTS.name());
		CswLog.getLogData().setSystem(LogSystemEnum.CSW.name());
		CswLog.getLogData().setFunction("processMessage");
		CswLog.getLogData().setLocalBaId(primitiveDTO.getBaLoc());
		CswLog.getLogData().setRemoteBaId(primitiveDTO.getBaRem());
		CswLog.getLogData().setVfn(primitiveDTO.getVfn());
		if(primitiveDTO.getSyncFlag() == 1) {
			CswLog.getLogData().setService(ServiceEnum.FMS.name());
			CswLog.getLogData().setUdr(primitiveDTO.getUdr());
		}
		CswLog.info(log, String.format("Processing primitive id: %s", primitiveDTO.getId()));
		try {
			if (primitiveDTO.getSyncFlag() == 1) {
				processFMS(primitiveDTO);
			} else {
				processFTS(primitiveDTO);
			}
		} catch (Exception e) {
			throw new ChcRollbackException(I18nService.ERR_PROCESSING_MESSAGE, e.getLocalizedMessage());
		}

	}

	private void processFMS(MQ1419SecNotAckFilereq primitiveDTO) throws ChcException, NoSuchFieldException {
		FMSSendMQ entity = repositoryFMS.findOneByLocalBaIdAndRemoteBaIdAndVfn(primitiveDTO.getBaLoc(),
				primitiveDTO.getBaRem(), primitiveDTO.getVfn());
		primitiveDTO.setBaProcessTms(LocalDateTime.now());
		entity.setCswProcessTms(LocalDateTime.now());
		commonProcess(entity, repositoryFMS, primitiveDTO);
	}

	private void processFTS(MQ1419SecNotAckFilereq primitiveDTO) throws ChcException, NoSuchFieldException {
		FTSSendMQ entity = repositoryFTS.findOneByLocalBaIdAndRemoteBaIdAndVfn(primitiveDTO.getBaLoc(),
				primitiveDTO.getBaRem(), primitiveDTO.getVfn());
		entity.setCswProcessTms(LocalDateTime.now());
		commonProcess(entity, repositoryFTS, primitiveDTO);
	}

	private <E extends CswEntityOutMqWithFile, R extends JpaRepository<E, Long>> void commonProcess(E entity,
			R repository, MQ1419SecNotAckFilereq mq1419) throws ChcException, NoSuchFieldException {
		if (entity == null) {
			CswLog.info(log, "Entity not found sending 1420");
			send1420Error(mq1419, entity);
			throw new ChcException(I18nCommon.ERR_NOT_FOUND);
		}

		if (entity.getStatus().equals(SendStatusMQ.SENT)) {
			entity.setStatus(SendStatusMQ.CLEANABLE);
			entity.setComplete(1);
			entity.setBaProcessTms(LocalDateTime.now());
			transactionTemplate.executeWithoutResult(t -> repository.save(entity));
			send1420(mq1419, entity);
			return;
		}

		send1420Error(mq1419, entity);
		throw new ChcException(I18nService.CSW_WRONG_STATUS);
	}

	private <E extends CswEntityOutMqWithFile> void send1420(MQ1419SecNotAckFilereq mq1419, E entity) throws NoSuchFieldException, ChcException {
		MQ1420SecNotAckFilecnf mq1420 = common1420(mq1419);
		mq1420.setResult(000);
		send1420JMS(mq1420, entity);
	}

	private <E extends CswEntityOutMqWithFile> void send1420JMS(MQ1420SecNotAckFilecnf mq1420, E entity) throws ChcException, NoSuchFieldException {
		CswLog.debug(log, "Handler 1419 - Sending primitive 1420 ..");
		
		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1420.getId());
		p.setEntityId(entity.getId());
		p.setFileGroupid(null);
		p.setServiceType((mq1420.getReqSyncFlag() == 1) ? ServiceType.FMS : ServiceType.FTS);
		
		mqPrimitiveSenderService.storePrimitiveOnDB(flowioMapper1420.writeByte(mq1420), p);
		
//		log.info("Handler 1419 - Sending primitive 1420: {}", new String(flowioMapper1420.writeByte(mq1420)));
//		jmsTemplate.convertAndSend(mqPrimitiveConfigurationLoader.getQueueByPrimitive(mq1420.getId()),
//				flowioMapper1420.writeByte(mq1420));
//		CswLog.info(log, "Sent 1420");
	}

	private MQ1420SecNotAckFilecnf common1420(MQ1419SecNotAckFilereq mq1419) {
		
		MQ1420SecNotAckFilecnf mq1420 = new MQ1420SecNotAckFilecnf();
		mq1420.setReqUdr(mq1419.getUdr());
		mq1420.setReqUdrLen(mq1419.getUdrLen());
		mq1420.setReqVfn(mq1419.getVfn());
		mq1420.setReqBaLoc(mq1419.getBaLoc());
		mq1420.setReqBaRem(mq1419.getBaRem());
		mq1420.setReqLocalAuthInfo(mq1419.getLocalAuthInfo());
		mq1420.setReqLocalAuthInfoAlg(mq1419.getLocalAuthInfoAlg());
		mq1420.setReqLocalAuthInfoLen(mq1419.getLocalAuthInfoLen());
		mq1420.setReqSyncFlag(mq1419.getSyncFlag());
		mq1420.setReqBaProcessTms(mq1419.getBaProcessTms());
		mq1420.setSiStdProcessTms(ZonedDateTime.now());
		mq1420.setReqBaProcessTms(LocalDateTime.now());
		return mq1420;
	}

	private <E extends CswEntityOutMqWithFile> void send1420Error(MQ1419SecNotAckFilereq mq1419, E entity) throws NoSuchFieldException, ChcException {
		MQ1420SecNotAckFilecnf mq1420 = common1420(mq1419);
		mq1420.setResult(001);
		mq1420.setRejReason(33);
		send1420JMS(mq1420, entity);
	}

}
