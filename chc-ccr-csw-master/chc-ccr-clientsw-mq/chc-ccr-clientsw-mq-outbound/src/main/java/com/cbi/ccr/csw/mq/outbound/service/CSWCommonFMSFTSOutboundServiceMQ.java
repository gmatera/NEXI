package com.cbi.ccr.csw.mq.outbound.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;

import com.cbi.ccr.csw.domain.CSWCommonRepositoryMQ;
import com.cbi.ccr.csw.domain.ClientTaskStatus;
import com.cbi.ccr.csw.domain.ConfigRouteInterface.RouteInterface;
import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.ConfigurationFMSFTSCommon;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.domain.i.CswEntityOutWithFile;
import com.cbi.ccr.csw.domain.i.HasFile;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1401SecSendFilecnf;
import com.cbi.ccr.csw.mq.common.mq.FileQueueUtil;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;
import com.cbi.ccr.csw.mq.domain.config.ConfigurationFMSFTSMQ;
import com.cbi.ccr.csw.mq.domain.i.CswEntityOutMqWithFile;
import com.cbi.ccr.csw.service.common.I18nService;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ServiceEnum;
import com.cbi.frw.common.I18nCommon;
import com.cbi.frw.common.exception.ChcException;
import com.cbi.frw.common.exception.ChcRollbackException;
import com.cbi.frw.common.exception.ChcUnrecoverableException;
import com.cbi.frw.common.json.JSON;
import com.cbi.frw.common.util.DateUtils;
import com.cbi.frw.common.util.FileConversionUtils;
import com.cbi.frw.http.AbstractPart;
import com.cbi.frw.http.ChcStubException;

import chc.framework.util.parsing.input.FlowioOutputMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CSWCommonFMSFTSOutboundServiceMQ<
	E extends CswEntityOutMqWithFile, 
	R extends CSWCommonRepositoryMQ<E, Long>, 
	D extends ClientMessageDTO,
	C extends ConfigurationFMSFTSMQ> 
	extends CSWCommonOutboundServiceMQ<E, R, D, MQ1400SecSendFilereq, C> {

	protected CSWCommonFMSFTSOutboundServiceMQ(@NonNull R dbRepository, @NonNull Class<E> entityClass) {
		super(dbRepository, entityClass);
	}
	@Autowired
	protected FileQueueUtil fileQueueUtil;
	
	protected FlowioOutputMapper<MQ1401SecSendFilecnf> flowioMapper1401;
	
	protected abstract E findEntityForChekDuplicates(MQ1400SecSendFilereq messageDto);
	
	protected abstract void send1401Confirm(@NonNull E entity, @NonNull MQ1400SecSendFilereq primitiveDto, String message,	LocalDateTime acceptTms);
	protected abstract void send1402(E entity, String message);
	protected abstract void send1413(E entity, MQ1400SecSendFilereq primitiveDto);
	
	@PostConstruct
	private void initBinders() {
		initBinder1401();
	}
	
	private void initBinder1401() {
		flowioMapper1401= new FlowioOutputMapper<>(BinderFactory.binder1401());
	}
	
	public void submitToHub(@NonNull MQ1400SecSendFilereq primitiveDto) throws ChcUnrecoverableException {
		E entity;
		
		if(primitiveDto.getSyncFlag() == 1)
			CswLog.getLogData().setUdr(primitiveDto.getUdr());
		CswLog.getLogData().setVfn(primitiveDto.getVfn());
		CswLog.debug(log, String.format("Performing validation of primitive: %s", primitiveDto.getId()));
		
		try {
			entity = checkDuplicates(primitiveDto);
		} catch (ChcException e) {
			mqPrimitiveSenderService.send1401ErrorWithPrimitive(0, build1401ErrorWithPrimitive(primitiveDto, e.getLocalizedMessage(), e.getCode()));
			return;
		}
		C conf = null;
		try {
			// validazione conf route e applicazione delle regular expr.
			validateBaAndInterfaceAndApllyRegex(primitiveDto);
			
			conf = getConfiguration(primitiveDto.getBaLoc(),
					primitiveDto.getBaRem());
			// validazioni basate sulla configurazione
			performValidations(primitiveDto, conf);
		} catch (ChcException e) {
			if(entity == null) {
				entity = createEntity(primitiveDto, SendStatusMQ.REJECTED, LocalDateTime.now(), null);
			}

			tryToSaveOnValidationException(entity, primitiveDto, e); 
			return;
		}
		
		LocalDateTime acceptTms = LocalDateTime.now();
		if(entity == null) {
			entity = createEntity(primitiveDto, SendStatusMQ.CREATING, acceptTms, conf);
			CswLog.getLogData().setId(entity.getId().toString());
			CswLog.debug(log,String.format("Saving entity on DB from primitive: %s", primitiveDto.getId()));
		}

		send1401Confirm(entity, primitiveDto, null, acceptTms);
		
		try {
			downloadFile(primitiveDto, entity);
			performFileValidation(entity, conf);
		} catch (ChcException e) {
			LocalDateTime errorTms = LocalDateTime.now();
			CswLog.error(log, e.getLocalizedMessage());
			entity.setStatusInfo(e.getMessage());
			entity.setErrorTimestamp(errorTms);
			handleManagedException(entity, e, primitiveDto);
			setCompleteForErrorMQ(entity, e);
			updateStatus(entity, SendStatusMQ.CREATE_ERROR);

//			Inviare a priori 1402 
//			if (!e.getCode().equals(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION.name()))
			
			send1402(entity, e.getLocalizedMessage());

			return;
		} 
		
		if (isCreateIndicationActive(entity, conf))
			send1413(entity, primitiveDto);
		
		// Update entity and continue
		entity.setCswStatus(ClientTaskStatus.SENDING);
		entity = updateStatus(entity, SendStatusMQ.SENDING);
		
		
		List<AbstractPart> parts = null;
		try {
			parts = createPartsInternal(entity, buildMessageDTO(entity), primitiveDto);
		} catch (ChcStubException | ChcException e) {
			return;
		}
		
		entity.setSendTime(LocalDateTime.now());
		entity = genericDAO.update(entity);
		
		sendToHubInternal(entity, parts, primitiveDto);
	}
	

	
	private boolean isCreateIndicationActive(E entity, ConfigurationFMSFTSMQ config) {
		if (config.getMqiPosCreateInd() == null)
			throw new ChcRollbackException(I18nService.ERR_MISSING_CONFIGURATION, "mqi Pos CreateInd not present");

		if (Boolean.TRUE.equals(config.getMqiPosCreateInd()))
			return true;
		return false;
	}

	/**
	 * FMS\FTS Per i servizi FMS e FTS la BA può chiedere la ripresa di una
	 * precedente richiestra inviando la primitiva 1400.Sec-Send-File_req con
	 * SendType “0” o “1”. Il Reinvio della primitiva con SendType “0” è permesso
	 * quando la precedente richiesta è fallita nella fase di Create ovvero per
	 * errori di validazione formale o di download del file dalla coda MQ per il
	 * quale quindi la richiesta è andata in stato REJECT o CREATE ERROR. In questo
	 * caso il record precedentemente inserito su DB, ed eventuale file già
	 * scaricato, vengono sovrascritti con quanto ricevuto sulla nuova richiesta. Il
	 * Reinvio della primitiva con SendType “1” è permesso solo quando la precedente
	 * richiesta ha superato correttamente la fase di Create ma è andata in errore
	 * sulla fase di Export (Encrypt file-messaggio, conversione, trasmissione al
	 * CCR o ricezione di una notifica negativa da parte dell’HUB). Nel caso quindi
	 * di ricezione della primitiva con SendType = “1” il ClientSW non sovrascrive i
	 * dati del precedente record ma imposta lo stato SENDING e riprende dalla fase
	 * di Encryption utilizzando il file già scaricato sul suo filesystem locale
	 * durante la precedente richiesta.
	 * 
	 * La logica applicativa è quindi la seguente. All’arrivo di una primitiva
	 * 1400.Sec-Send-File_req con SendType = “0” il ClientSW verifica se sulla
	 * tabella FTS SEND_MQI o FMS_SEND_MQI (a seconda del servizio) è già presente
	 * un record avente lo stesso Mittente BA, Destinatario BA, VFN e UDR, in tal
	 * caso agisce come indicato di seguito. Se lo stato della precedente richiesta
	 * è CREATING, SENDING, LOCALLY_CONFIRMED, REMOTELY_CONFIRMED, SENT, CLENABLE o
	 * IN ERROR la nuova richiesta viene ritenuta un duplicato pertanto la primitiva
	 * 1400.Sec-Send-File_req viene rifiutata Se lo stato della precedente richiesta
	 * è REJECTED o CREATE ERROR il ClientSW elimina il precedente record, ed
	 * eventuale file, e gestisce normalmente la nuova richiesta.
	 * 
	 * All’arrivo di una primitiva 1400.Sec-Send-File_req con SendType = “1” il
	 * ClientSW verifica se sulla tabella FTS SEND_MQI o FMS_SEND_MQI (a seconda del
	 * servizio) è già presente un record avente lo stesso Mittente BA, Destinatario
	 * BA, VFN e UDR. Se non esiste alcun record precedente la primitiva viene
	 * rifiutata. Se esiste un precedente record il ClientSW agisce come indicato di
	 * seguito. Se lo stato della precedente richiesta è CREATING, SENDING,
	 * LOCALLY_CONFIRMED, REMOTELY_CONFIRMED, SENT, CLENABLE, REJECTED o CREATE
	 * ERROR la nuova richiesta viene ritenuta un duplicato pertanto la primitiva
	 * 1400.Sec-Send-File_req viene rifiutata. Se lo stato della precedente
	 * richiesta è IN ERROR il ClientSW aggiorna lo stato in SENDING e riprende
	 * l’elaborazione lavorando sul file precedentemente scaricato.
	 */
	@Override
	protected E checkDuplicates(@NonNull MQ1400SecSendFilereq messageDto) throws ChcException {
		E entity = findEntityForChekDuplicates(messageDto);

		// sembra sbagliato
//		if (dbRepository.countUdr(messageDto.getUdr()) > 0)
//			throw new ChcException(I18nService.ERR_DUPLICATE_UDR, messageDto.getUdr());
		
		if (messageDto.getSendType() == null || messageDto.getSendType() == 0) {
			//a new record
			if (entity == null)
				return null;
			
			//duplicate with invalid status
			if(!entity.getStatus().equals(SendStatusMQ.CREATE_ERROR) && !entity.getStatus().equals(SendStatusMQ.REJECTED))
				throw new ChcException(I18nService.ERR_DUPLICATE_UDR_VFN, "Duplicate request rejected, invalid status");
			
			//delete existing record and restart from scratch
			dbRepository.delete(entity);
			return null;
		}else if (messageDto.getSendType() == 1) {
			// record doesn't exist and the message is a duplicate
			if (entity == null)
				throw new ChcException(I18nService.ERR_CHECKING_DUPLICATES, "Request rejected because record doesn't exists and send type equals 1");
			
			//duplicate with invalid status
			if(!entity.getStatus().equals(SendStatusMQ.IN_ERROR))
				throw new ChcException(I18nService.ERR_DUPLICATE_UDR_VFN, "Duplicate request rejected, invalid status");
			

			return entity;
		}else {
			throw new ChcException(I18nCommon.ERR_VALIDATION_EXCEPTION, String.format("%s not valid", "SendType"));
		}
	}
	
	
	protected void validateBaAndInterfaceAndApllyRegex(@NonNull MQ1400SecSendFilereq messageDto) throws ChcException {
		
		ServiceType serviceType = messageDto.getSyncFlag() == 1 ? ServiceType.FMS : ServiceType.FTS;
		
		validationService.validateBaAndInterface(messageDto.getBaLoc(), messageDto.getBaRem(), serviceType ,RouteInterface.MQ);
		validationService.validateFMSFTSMQRegex(messageDto);

	}
	
	protected void downloadFile(@NonNull MQ1400SecSendFilereq dto, E entity) throws ChcException{
		CswLog.debug(log, String.format("Downloading file from queue: %s", dto.getQueueFileName()));
		byte[] receive = null;
		try {
			entity.setStartCreateTimestamp(LocalDateTime.now());
			receive = fileQueueUtil.browseFileGrouped(dto.getQueueFileName(), dto.getGroupId());
			entity.setEndCreateTimestamp(LocalDateTime.now());
		} catch (JmsException | JMSException e) {
			throw new ChcException(I18nCommon.ERR_UNABLE_TO_DOWNLOAD_FILE, String.format("File not found in queue: %s", dto.getQueueFileName()));
		} 

		if(receive == null || receive.length == 0) {
			CswLog.error(log, String.format("received file: %s", receive));
			throw new ChcException(I18nCommon.ERR_GROUP_ID_NOT_EXIST, new String(dto.getGroupId()));
		}
		
		File temp = new File(entity.getFileName());
		try (FileOutputStream fos = new FileOutputStream(temp)) {
			fos.write(receive);
		} catch (IOException e) {
			throw new ChcRollbackException(I18nCommon.ERR_IOEXCEPTION_FILE_ACCESS, String.format("Error saving file %s. err:%s", entity.getFileName(), e));
		}
	}
	
	private void commonRequestRejected(E entity) {
		entity.setStatus(SendStatusMQ.REJECTED);
		entity.setCswStatus(ClientTaskStatus.FAILED);
	}
	
	private void commonRequestError(E entity) {
		entity.setStatus(SendStatusMQ.IN_ERROR);
		entity.setCswStatus(ClientTaskStatus.FAILED);
	}
	@Override
	protected void handleManagedException(E entity, ChcException e, @NonNull MQ1400SecSendFilereq primitive) {
//		protected void processException(String code, FMSSendMQ fmsSend, String message, MQ1400SecSendFilereq primitive) {

			String code = e.getCode();
			String message = e.toString();
			
			entity.setPrimitiveError("1401");
			if (code.equals(I18nService.ERR_INVALID_BA.name())){
				commonRequestRejected(entity);
				entity.setStatusInfo("BA pair not configured in ClientSW.");
				entity.setRejectReason(8);
				send1401Confirm(entity, primitive, message, null);
			}else if(code.equals(I18nService.ERR_MISSING_CONFIGURATION.name())) {
				commonRequestRejected(entity);
				String service = primitive.getSyncFlag()==1 ? "FMS":"FTS";
				entity.setStatusInfo("Local BA not enabled for "+ service +" Service.");
				entity.setRejectReason(6);
				send1401Confirm(entity, primitive, message, null);
			} else if (code.equals(I18nService.ERR_INVALID_INTERFACE.name())) {
				commonRequestRejected(entity);
				entity.setRejectReason(7);
				entity.setStatusInfo("Interface type not correct");
				send1401Confirm(entity, primitive, message, null);
			} else if (code.equals(I18nCommon.ERR_VALIDATION_EXCEPTION.name())) {
				commonRequestRejected(entity);
				entity.setRejectReason(9);
				entity.setStatusInfo(message);
				send1401Confirm(entity, primitive, message, null);
			} else if (code.equals(I18nCommon.ERR_VALIDATION_SECURITY_EXCEPTION.name())) {
				commonRequestRejected(entity);
				entity.setRejectReason(9);
				entity.setStatusInfo(message);
			} else if (code.equals(I18nService.ERR_CONVERTING_FILE.name())) {
				commonRequestError(entity);
				entity.setRejectReason(73);
				entity.setStatusInfo("Failed to convert file");
			} else if(code.equals(I18nCommon.ERR_FILE_VALIDATION_EXCEPTION.name())) {
				commonRequestRejected(entity);
				entity.setPrimitiveError("1402");
				entity.setRejectReason(40);
				entity.setStatusInfo(message);			
			} else if(code.equals(I18nCommon.ERR_FILE_SIZE_VALIDATION_EXCEPTION.name())) {
				commonRequestRejected(entity);
				entity.setPrimitiveError("1402");
				entity.setRejectReason(43);
				entity.setStatusInfo(message);
			} else if(code.equals(I18nCommon.ERR_UNABLE_TO_DOWNLOAD_FILE.name())) {
				entity.setPrimitiveError("1402");
				commonRequestRejected(entity);
				entity.setRejectReason(35);
				entity.setStatusInfo(message);
//			} else if(code.equals(I18nService.ERR_INOKING_CRYPTO_HUB.name())) {
//				entity.setRejectReason(72);
//				entity.setStatusInfo(message);
//			} else if(code.equals(I18nService.ERR_SENDING_TO_CCR.name())) {
//				entity.setRejectReason(71);
//				entity.setStatusInfo(message);
			} else if(code.equals(I18nCommon.ERR_GROUP_ID_NOT_EXIST.name())) {
				entity.setPrimitiveError("1402");
				commonRequestRejected(entity);
				entity.setRejectReason(37);
				entity.setStatusInfo(message);
			} else if(code.equals(I18nService.ERR_ENCRYPT.name())) {
				entity.setPrimitiveError("1402");
				commonRequestError(entity);
				entity.setRejectReason(74);
				entity.setStatusInfo(message);
			}

		}
	
	public MQ1401SecSendFilecnf build1401ErrorWithPrimitive(@NonNull MQ1400SecSendFilereq primitiveDto, String message, String exceptionCode) {

		MQ1401SecSendFilecnf confirm = new MQ1401SecSendFilecnf();
		confirm.setBaLoc(primitiveDto.getBaLoc());
		confirm.setBaRem(primitiveDto.getBaRem());
		confirm.setSendType(primitiveDto.getSendType());
		confirm.setSyncFlag(primitiveDto.getSyncFlag());
		confirm.setCorrId(primitiveDto.getCorrId());
		confirm.setBaFileSize(primitiveDto.getBaFileSize());
		confirm.setRecType(primitiveDto.getRecType());
		confirm.setCharType(primitiveDto.getCharType());
		confirm.setMsgType(primitiveDto.getMsgType());
		confirm.setSndBaFileDigestAlg(primitiveDto.getSndBaFileDigestAlg());
		confirm.setSndBaFileDigestLen(primitiveDto.getSndBaFileDigestLen());
		confirm.setSndBaFileDigest(primitiveDto.getSndBaFileDigest());
		confirm.setMabDigestAlg(primitiveDto.getMabDigestAlg());
		confirm.setMabDigestLen(primitiveDto.getMabDigestLen());
		confirm.setMabDigest(primitiveDto.getMabDigest());
		confirm.setVfn(StringUtils.isEmpty(primitiveDto.getVfn()) ? createVfn(primitiveDto) : primitiveDto.getVfn());
		confirm.setUdr(primitiveDto.getUdr());
		confirm.setUdrLen(primitiveDto.getUdrLen());
		confirm.setCorrId(primitiveDto.getCorrId());
		confirm.setQueueFileName(primitiveDto.getQueueFileName());
		confirm.setGroupId(primitiveDto.getGroupId());
		confirm.setTur(primitiveDto.getTur());
		confirm.setCatAppl(primitiveDto.getCatAppl());
		confirm.setLocalBaData(primitiveDto.getLocalBaData());

		if (StringUtils.isEmpty(message)) {
			confirm.setResult(0);
			confirm.setAcceptTms(DateUtils.fromLocalDateTimeToZonedDateTime(LocalDateTime.now()));
		} else {
			confirm.setResult(1);

			if (exceptionCode.equals(I18nService.ERR_CHECKING_DUPLICATES.name()))
				confirm.setRejReason(34);
			
			if (exceptionCode.equals(I18nService.ERR_DUPLICATE_UDR_VFN.name()))
				confirm.setRejReason(36);

		}
		CswLog.debug(log, "Sending 1401 ..");
//		log.info("Sending 1401: {}", JSON.toJson(confirm));
		return confirm;
	}

	protected <C extends ConfigurationFMSFTSMQ> void updateEntityOnConfiguration(MQ1400SecSendFilereq dto, C conf, E entity) {
		if(dto.getCharType().equals(0)) {
			entity.setCharType(conf.getSndCodePage());
		} else {
			entity.setCharType(CodePage.getEnumByValueMQ(dto.getCharType()));
		}
		
		if(dto.getLineSeparator().equals(0)) {
			entity.setLineSeparator(conf.getSndLineSeparator());
		} else {
			entity.setLineSeparator(LineSeparator.getEnumByLabelMq(dto.getLineSeparator()));
		}
		
		if(dto.getRecType().equals(0)) {
			entity.setRecordFormat(conf.getSndRecordFormat());
		} else {
			entity.setRecordFormat(RecordFormat.getEnumByValueMQ(dto.getRecType()));
		}
		
		if(dto.getMaxRecLen() == 0) {
			entity.setMaxRecLen(conf.getSndMaxRecLength());
		} else {
			entity.setMaxRecLen(dto.getMaxRecLen());
		}
	}
	

	protected  File convertFileToHubFormatMQ(E entity) throws ChcException {
		
		File fileAsciiOrBinary = convertMQFileifEBCDIC(entity);
		File filetoSend;
		
		if(entity.getCharType()!=CodePage.BINARY) {
			filetoSend = convertLineSeparatorToCRLFforMQ(fileAsciiOrBinary, entity);
		}else {
			 filetoSend = fileAsciiOrBinary;
		}
		return filetoSend;
	}
	
	protected <C extends ConfigurationFMSFTSCommon> File convertMQFileifEBCDIC(E entity) throws ChcException {

		if (!(entity instanceof CswEntityOutWithFile))
			return null;

		if (!canConvertMQFileToEBCDIC(entity))
			return new File(((CswEntityOutWithFile) entity).getFileName());

		CswEntityOutWithFile entityWithFile = (CswEntityOutWithFile) entity;
		File ebcdic = new File(entityWithFile.getFileName());
		File ascii = new File(localConversionFolder + File.separator + "converted-to-ascii-" + UUID.randomUUID().toString());
		CswLog.info(log, String.format("Converting EBCDIC (%s) file to ASCII (%s) before sending...", entityWithFile.getFileName(), ascii.getAbsolutePath()));

		try {
			FileConversionUtils.convertFileFromEBCDICtoASCII(ebcdic, ascii);
			return ascii;
			//NON MODIFICARE ENTITA
//			entityWithFile.setFileName(ascii.getAbsolutePath());
		} catch (ChcException e) {
			CswLog.error(log, e.getLocalizedMessage());
			throw new ChcException(I18nService.ERR_CONVERTING_FILE, e.getLocalizedMessage());
		}
	}
	
	protected <C extends ConfigurationFMSFTSCommon> File convertLineSeparatorToCRLFforMQ(File file, E entity)
			throws ChcException {

		if (!(entity instanceof HasFile))
			return null;


		if (entity.getLineSeparator() == null)
			throw new ChcException(I18nService.ERR_CONVERTING_FILE, "LineSeparator not found for conversion");

		if (!entity.getLineSeparator().equals(LineSeparator.LF_0X0A) && !entity.getLineSeparator().equals(LineSeparator.NONE)) {
			return file;
		}

		File fileConvertedLineSeparator = new File(localConversionFolder + File.separator + "converted-lineSeparator-" + UUID.randomUUID().toString());
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file));
				PrintWriter writer = new PrintWriter(
						new BufferedOutputStream(new FileOutputStream(fileConvertedLineSeparator)));) {

			if (entity.getRecordFormat().equals(RecordFormat.FIXED) & entity.getLineSeparator().equals(LineSeparator.NONE)) {
				int recordLenght = entity.getMaxRecLen();
				char[] buffer = new char[recordLenght];
				while ((reader.read(buffer)) > 0 ) {
					writer.print(String.format("%s%s",new String(buffer), LineSeparator.CRLF_0X0D0A.getFullHexCode()));
				}

			} else {
				String str;
				while ((str = reader.readLine()) != null) {
					writer.print(String.format("%s%s", str, LineSeparator.CRLF_0X0D0A.getFullHexCode()));
				}
			}
			writer.flush();
			return fileConvertedLineSeparator;
		} catch (IOException e) {
			throw new ChcException(I18nService.ERR_CONVERTING_FILE, e.getLocalizedMessage());
		}
	}

}
