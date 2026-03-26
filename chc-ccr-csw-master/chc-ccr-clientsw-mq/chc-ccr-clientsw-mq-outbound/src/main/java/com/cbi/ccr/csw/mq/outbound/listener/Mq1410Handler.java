package com.cbi.ccr.csw.mq.outbound.listener;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1405SecReceiveind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1410SecReadFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1411SecReadFilecnf;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1412SecReadFileind;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.CommonConfigurationServiceMQ;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveService;
import com.cbi.ccr.csw.mq.domain.RecvStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFTSMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQRepository;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQRepository;
import com.cbi.ccr.csw.mq.outbound.exception.PrimitiveLengthMismatchException;
import com.cbi.ccr.csw.mq.outbound.service.MqServiceBase;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import chc.framework.util.parsing.input.FlowioInputMapper;
import chc.framework.util.parsing.input.FlowioOutputMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Mq1410Handler extends MqServiceBase<MQ1410SecReadFilereq> {

	@Autowired
	private MqPrimitiveService<FMSRecvMQ> fmsMqPrimitiveService;

	@Autowired
	private MqPrimitiveService<FTSRecvMQ> ftsMqPrimitiveService;

	@Autowired
	private MqPrimitiveSenderService mqPrimitiveSenderService;

	@Autowired
	private FMSRecvMQRepository repositoryFMS;

	@Autowired
	private FTSRecvMQRepository repositoryFTS;

	@Autowired
	protected CommonConfigurationServiceMQ configurationService;

	private FlowioInputMapper<MQ1410SecReadFilereq> flowioMapper1410;
	private FlowioOutputMapper<MQ1411SecReadFilecnf> flowioMapper1411;
	private FlowioOutputMapper<MQ1412SecReadFileind> flowioMapper1412;

	@Value("${local_temp_folder_file_mq}")
	private String localTempFolderfileMq;

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	private void initBinder() {
		flowioMapper1410 = new FlowioInputMapper<>(BinderFactory.binder1410());
		flowioMapper1411 = new FlowioOutputMapper<>(BinderFactory.binder1411());
		flowioMapper1412 = new FlowioOutputMapper<>(BinderFactory.binder1412());
	}

	@Override
	protected FlowioInputMapper<MQ1410SecReadFilereq> getFlowioMapper() {
		return flowioMapper1410;
	}

	@Override
	protected MQ1410SecReadFilereq getPrimitiveDto(Byte[] data)
			throws ChcUnrecoverableException, PrimitiveLengthMismatchException {
		try {
			checkSize(flowioMapper1410, data.length);
			return flowioMapper1410.read(Arrays.asList(data));
		} catch (PrimitiveLengthMismatchException e) {
			throw e;
		} catch (Exception e) {
			throw new ChcUnrecoverableException(I18nService.ERR_PROCESSING_MESSAGE);
		}
	}

	@Override
	protected void processMessage(Byte[] bytedata) throws ChcUnrecoverableException, PrimitiveLengthMismatchException {

		MQ1410SecReadFilereq primitiveDTO;
		primitiveDTO = getPrimitiveDto(bytedata);
		CswLog.getLogData().setLocalBaId(primitiveDTO.getBaLoc());
		CswLog.getLogData().setRemoteBaId(primitiveDTO.getBaRem());
		CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
		CswLog.info(log, String.format("Processing primitive id: %s", primitiveDTO.getId()));
		try {
			if (primitiveDTO.getSyncFlag() == 1) {
				processFMS(primitiveDTO);

			} else {
				processFTS(primitiveDTO);
			}
		} catch (Exception e1) {
			throw new ChcRollbackException(I18nService.ERR_PROCESSING_MESSAGE, e1);
		}
	}

	private void processFTS(MQ1410SecReadFilereq primitiveDTO) throws ChcException, NoSuchFieldException {
		FTSRecvMQ entity = repositoryFTS.findOneByLocalBaIdAndRemoteBaIdAndVfn(primitiveDTO.getBaLoc(),
				primitiveDTO.getBaRem(), primitiveDTO.getVfn());
		if (entity == null)
			throw new ChcException(I18nCommon.ERR_NOT_FOUND);

		ConfigurationFTSMQ configFTS = configurationService.loadFTSMQConfiguration(primitiveDTO.getBaLoc(), primitiveDTO.getBaRem());

		entity.setAcceptTms(LocalDateTime.now());
		LocalDateTime acceptTms1411 = LocalDateTime.now();
		send1411CnfFTS(configFTS, entity, primitiveDTO, acceptTms1411);
		
		entity.setStatus(RecvStatusMQ.DELIVERED);
		List<FTSRecvMQ> list = new ArrayList<>();
		list.add(entity);
		
		transactionTemplate.executeWithoutResult(t -> list.add(0, repositoryFTS.save(entity)));
		
		String groupId = mqPrimitiveSenderService.generateGroupId();
		
		MQ1412SecReadFileind mq1412 = ftsMqPrimitiveService.build1412PrimitiveFTS(entity, groupId, null, primitiveDTO);
		byte[] primitiveByte = flowioMapper1412.writeByte(mq1412);
		
		MQ1412SecReadFileind mq1412Negative = ftsMqPrimitiveService.build1412PrimitiveFTS(entity, groupId, "ErrorMessage", primitiveDTO);
		byte[] primitiveNegative = flowioMapper1412.writeByte(mq1412Negative);
		
		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1412.getId());
		p.setFileGroupid(groupId);
		p.setDestinationQueue(primitiveDTO.getQueueFileName());
		p.setFileName(entity.getFileName());
		p.setServiceType(ServiceType.FTS);
		p.setEntityId(entity.getId());
		
		mqPrimitiveSenderService.storePrimitiveWithFile(primitiveByte, primitiveNegative, p);
		
		if (configFTS.getRcvCompletionAlgo() != null && configFTS.getRcvCompletionAlgo()) {
			list.get(0).setStatus(RecvStatusMQ.CLEANABLE);
			list.get(0).setComplete(1);
		}
		transactionTemplate.executeWithoutResult(t -> repositoryFTS.save(list.get(0)));
	}

	private void processFMS(MQ1410SecReadFilereq primitiveDTO) throws ChcException, NoSuchFieldException, IOException {
		ZonedDateTime tmsEndSending = ZonedDateTime.now();
		ConfigurationFMSMQ configFMS = configurationService.loadFMSMQConfiguration(primitiveDTO.getBaLoc(),
				primitiveDTO.getBaRem());
		FMSRecvMQ entity = repositoryFMS.findOneByLocalBaIdAndRemoteBaIdAndVfn(primitiveDTO.getBaLoc(),
				primitiveDTO.getBaRem(), primitiveDTO.getVfn());

		if (entity == null)
			throw new ChcException(I18nCommon.ERR_NOT_FOUND);

		entity.setAcceptTms(LocalDateTime.now());
		entity.setStartReadTms(LocalDateTime.now());
		entity.setEndReadTms(LocalDateTime.now());
		LocalDateTime acceptTms1411 = LocalDateTime.now();
		send1411CnfFMS(configFMS, entity, primitiveDTO, acceptTms1411);
		ZonedDateTime tmsStartWritingFileInQueue = ZonedDateTime.now();

		ZonedDateTime tmsEndWritingFileInQueue = ZonedDateTime.now();
		ZonedDateTime acceptTms1405 = ZonedDateTime.now();
		entity.setStatus(RecvStatusMQ.DELIVERED);
		ZonedDateTime tmsCswSave = ZonedDateTime.now();
		entity = genericDAO.update(entity);

		FMSMessageDTO dto = new FMSMessageDTO();
		dto.setTmsReceived(entity.getHostFirstSubTms());
		dto.setTmsStartSending(entity.getFerSubTime());

		BlobEntity blob = BlobEntity.builder().blobColum("MESSAGE").tableName("FMS_RECV_MQI").keyColum("ID")
				.key(entity.getId()).build();

		File message = new File(localTempFolderfileMq,
				String.format("%s_%s_message_dec", entity.getClass().getSimpleName(), entity.getId()));
		BlobHelper.readblobAsFile(blob, entityManager, message);

		String groupId = mqPrimitiveSenderService.generateGroupId();

		MQ1405SecReceiveind mq1405 = fmsMqPrimitiveService.build1405PrimitiveMqFMS(entity, groupId, configFMS, tmsEndSending, tmsStartWritingFileInQueue, tmsEndWritingFileInQueue, tmsCswSave, acceptTms1405, dto, message);
		FlowioOutputMapper<MQ1405SecReceiveind> flowioMapper1405 = new FlowioOutputMapper<>(BinderFactory.binder1405(entity.getMessageLeng()));
		byte[] primitiveByte = flowioMapper1405.writeByte(mq1405);
		
		MQ1412SecReadFileind mq1412 = fmsMqPrimitiveService.build1412PrimitiveFMS(entity, groupId, "ErrorMessage");
		byte[] primitiveNegative =  flowioMapper1412.writeByte(mq1412);
		
		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1405.getId());	
		p.setFileGroupid(groupId);
		p.setDestinationQueue(primitiveDTO.getQueueFileName());
		p.setFileName(entity.getFileName());
		p.setServiceType(ServiceType.FMS);
		p.setEntityId(entity.getId());
		
		mqPrimitiveSenderService.storePrimitiveWithFile(primitiveByte, primitiveNegative, p);
		
		if (configFMS.getRcvCompletionAlgo() != null && configFMS.getRcvCompletionAlgo()) {
			entity.setStatus(RecvStatusMQ.CLEANABLE);
			entity.setComplete(1);
		}
		genericDAO.update(entity);

	}

	private void send1411CnfFMS(ConfigurationFMSMQ configFMS, FMSRecvMQ entity, MQ1410SecReadFilereq primitiveDTO,
			LocalDateTime acceptTms) throws ChcException, NoSuchFieldException {
		MQ1411SecReadFilecnf mq1411 = fmsMqPrimitiveService.get1411PrimitiveMq(entity, null, primitiveDTO,
				configFMS.getLauEnabled(), acceptTms);

		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1411.getId());
		p.setEntityId(entity.getId());
		p.setFileGroupid(null);
		p.setServiceType(ServiceType.FMS);
		
		mqPrimitiveSenderService.storePrimitiveOnDB(flowioMapper1411.writeByte(mq1411), p);

	}

	private void send1411CnfFTS(ConfigurationFTSMQ configFTS, FTSRecvMQ entity, MQ1410SecReadFilereq primitiveDTO,
			LocalDateTime acceptTms) throws ChcException, NoSuchFieldException {
		MQ1411SecReadFilecnf mq1411 = ftsMqPrimitiveService.get1411PrimitiveMq(entity, null, primitiveDTO,
				configFTS.getLauEnabled(), acceptTms);

		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1411.getId());
		p.setEntityId(entity.getId());
		p.setFileGroupid(null);
		p.setServiceType(ServiceType.FTS);
		
		mqPrimitiveSenderService.storePrimitiveOnDB(flowioMapper1411.writeByte(mq1411), p);
	}
}
