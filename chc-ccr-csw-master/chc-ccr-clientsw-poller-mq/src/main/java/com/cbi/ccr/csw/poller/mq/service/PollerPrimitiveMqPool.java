package com.cbi.ccr.csw.poller.mq.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cbi.ccr.csw.domain.csw.config.GlobalProperties;
import com.cbi.ccr.csw.domain.csw.config.GlobalPropertiesRepository;
import com.cbi.ccr.csw.domain.csw.config.PropertiesEnum;
import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePoolRepository;
import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePoolStatus;
import com.cbi.ccr.csw.mq.common.mq.FileQueueUtil;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveConfigurationLoader;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData;
import com.cbi.ccr.csw.service.common.logger.CswLogData.LogSystemEnum;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.logging.Log;
import com.cbi.frw.persistence.service.BlobHelper;
import com.cbi.frw.persistence.service.BlobHelper.BlobEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("MQ")
public class PollerPrimitiveMqPool {

	@Autowired
	private PrimitivePoolRepository primitivePoolRepository;

	@Autowired
	private MqPrimitiveConfigurationLoader primitiveConf;

	@Autowired
	protected JmsTemplate jmsTemplate;

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	private FileQueueUtil fileQueueUtil;

	@Autowired
	private GlobalPropertiesRepository propertiesRepo;
	

	@Scheduled(fixedDelayString = "${fixed_mq_poller_delay}")
	public void startProcess() {

		List<PrimitivePool> primitiveToSubmit = getPrimitiveToSubmit();
		Iterator<PrimitivePool> iterator = primitiveToSubmit.iterator();

		while (iterator.hasNext()) {

			PrimitivePool primitive = iterator.next();

			if (primitive.getStatus().equals(PrimitivePoolStatus.FILE_SENT)
					|| (primitive.getStatus().equals(PrimitivePoolStatus.NEW)
							&& StringUtils.isEmpty(primitive.getFileGroupid()))) {
				sendPrimitive(iterator, primitive, PrimitivePool.PROP_POSITIVE_PRIMITIVE);
				primitive.setStatus(PrimitivePoolStatus.COMPLETED);
			}

			if (primitive.getStatus().equals(PrimitivePoolStatus.NEW) && !StringUtils.isEmpty(primitive.getFileGroupid())) {
				try {
					putFileInQueue(iterator, primitive);
					primitive.setStatus(PrimitivePoolStatus.FILE_SENT);
				} catch (ChcException e) {
					iterator.remove();
				} catch (IOException e) {
					primitive.setStatus(PrimitivePoolStatus.FAILED);
				}
			}
		}

		if (!primitiveToSubmit.isEmpty()){
			primitivePoolRepository.saveAll(primitiveToSubmit);
		}

	}

	private List<PrimitivePool> getPrimitiveToSubmit() {
		return primitivePoolRepository.findAllMessagesToProcess(PageRequest.of(0, 50));
	}

	private void sendPrimitive(Iterator<PrimitivePool> iterator, PrimitivePool primitive, String columnMessage) {
		String destinationQueue = null;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			CswLogData logData = new CswLogData();
			logData.setFunction("sendPrimitive");
			logData.setModule(ModuleEnum.PRIMITIVE_POOL_MQ.name());
			logData.setSystem(LogSystemEnum.CSW.name());
			logData.setCorrelationId("");
			logData.setService(primitive.getServiceType().name());
			logData.setId(primitive.getEntityId().toString());

			BlobEntity entity = BlobEntity.builder().keyColum(PrimitivePool.PROP_ID).tableName(PrimitivePool.TABLE_NAME).blobColum(columnMessage)
					.key(primitive.getId()).build();

			BlobHelper.readblobAsByte(entity, entityManager, bos);
			byte[] primitiveByteArray = bos.toByteArray();

			destinationQueue = primitiveConf.getQueueByPrimitive(primitive.getPrimitiveId());
						
			jmsTemplate.convertAndSend(destinationQueue, primitiveByteArray);

			logData.setMessage(String.format("primitive %s sent", primitive.getPrimitiveId()));
			Log.info(log, logData);

		} catch (JmsException e) {
			CswLog.error(log, String.format("MQ error with retry, sending primitive to queue:%s err:%s", destinationQueue, e.toString()));
		} catch (IOException | ChcException e) {
			CswLog.error(log, String.format("Fatal error with FAILED sending primitive to queue:%s err:%s", destinationQueue, e.toString()));
			// eccezione lanciata in lettura del blob
			iterator.remove();
		} 
	}

	/**
	 * Se l'invio del file fallisce dobbiamo inviare la primitiva di notigfica negativa:
	 * FMS postivo -> 1405 (blob message), negativo -> 1412 (blob negative_message)
	 * FTS postivo -> 1412 (blob message), negativo -> 1412 (blob negative_message)
	 */
	private void putFileInQueue(Iterator<PrimitivePool> iterator, PrimitivePool primitive) throws ChcException, IOException {

		File fileToSend = new File(primitive.getFileName());
		GlobalProperties prop = propertiesRepo.findFirstByPropertyName(PropertiesEnum.UPLOAD_QUEUE_NAME_GROUP_SIZE.getLabel());
		if (prop == null)
			throw new ChcException(I18nService.ERR_MISSING_CONFIGURATION, String.format("Global Property %s not found", PropertiesEnum.UPLOAD_QUEUE_NAME_GROUP_SIZE.getLabel()));

		Long groupSize = Long.valueOf(prop.getValue());
		try {
			fileQueueUtil.sendFileGrouped(fileToSend, groupSize, primitive.getDestinationQueue(), primitive.getFileGroupid());
		} catch (IOException | JmsException e) {
			sendPrimitive(iterator, primitive, PrimitivePool.PROP_NEGATIVE_PRIMITIVE);
		}
	}

}
