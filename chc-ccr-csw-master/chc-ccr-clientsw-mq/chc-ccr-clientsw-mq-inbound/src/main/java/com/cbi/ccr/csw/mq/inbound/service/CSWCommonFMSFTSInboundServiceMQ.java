package com.cbi.ccr.csw.mq.inbound.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.JmsException;

import com.cbi.ccr.csw.domain.csw.mq.pool.PrimitivePool;
import com.cbi.ccr.csw.dto.fms.ClientMessageDTO;
import com.cbi.ccr.csw.dto.fms.FMSMessageDTO;
import com.cbi.ccr.csw.dto.fms.FTSMessageDTO;
import com.cbi.ccr.csw.dto.fms.ServiceType;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1405SecReceiveind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1409SecReceiveFileInd;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1412SecReadFileind;
import com.cbi.ccr.csw.mq.common.service.BinderFactory;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveSenderService;
import com.cbi.ccr.csw.mq.common.service.MqPrimitiveService;
import com.cbi.ccr.csw.mq.domain.fms.FMSRecvMQ;
import com.cbi.ccr.csw.mq.domain.fts.FTSRecvMQ;
import com.cbi.ccr.csw.mq.domain.i.CswInboundMqWithFileEntity;
import com.cbi.ccr.csw.service.common.logger.CswLog;
import com.cbi.ccr.csw.service.common.logger.CswLogData.ModuleEnum;
import com.cbi.frw.common.exception.ChcException;

import chc.framework.util.parsing.input.FlowioFixedPositionBinder;
import chc.framework.util.parsing.input.FlowioOutputMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CSWCommonFMSFTSInboundServiceMQ<
	E extends CswInboundMqWithFileEntity,
	R extends JpaRepository<E, Long>,
	D extends ClientMessageDTO>
	extends CSWCommonInboundServiceMQ<E, R, D> {
	
	@Autowired
	protected MqPrimitiveService<E> mqPrimitiveService;
	
	@Autowired 
	protected MqPrimitiveSenderService mqPrimitiveSenderService;
	
	protected CSWCommonFMSFTSInboundServiceMQ(@NonNull R dbRepository, @NonNull Class<E> entityClass) {
		super(dbRepository, entityClass);
	}
	
	protected FlowioFixedPositionBinder<MQ1405SecReceiveind> binder1405;
	protected FlowioOutputMapper<MQ1405SecReceiveind> flowioMapper1405;
	
	protected FlowioFixedPositionBinder<MQ1409SecReceiveFileInd> binder1409;
	protected FlowioOutputMapper<MQ1409SecReceiveFileInd> flowioMapper1409;
	protected FlowioOutputMapper<MQ1412SecReadFileind> flowioMapper1412;
	
	@PostConstruct
	private void initBinders() {
		flowioMapper1409= new FlowioOutputMapper<>(BinderFactory.binder1409());
		flowioMapper1412 = new FlowioOutputMapper<>(BinderFactory.binder1412());
	}
	

	
	protected void send1409FMS(FMSRecvMQ fmsRecv, FMSMessageDTO dto) throws JmsException, NoSuchFieldException, ChcException {
		MQ1409SecReceiveFileInd mq1409 = mqPrimitiveService.get1409PrimitiveMqFMS(fmsRecv, dto);

		CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
		CswLog.getLogData().setLocalBaId(fmsRecv.getLocalBaId());
		CswLog.getLogData().setRemoteBaId(fmsRecv.getRemoteBaId());
		CswLog.getLogData().setUdr(fmsRecv.getUdr());
		CswLog.getLogData().setId(fmsRecv.getId().toString());
		
		byte[] primitiveByte = flowioMapper1409.writeByte(mq1409);
		
		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1409.getId());
		p.setServiceType(ServiceType.FMS);
		p.setEntityId(fmsRecv.getId());
		
		mqPrimitiveSenderService.storePrimitiveOnDB(primitiveByte, p);
	}
	
	protected void send1409FTS(FTSRecvMQ ftsRecv, FTSMessageDTO dto) throws JmsException, NoSuchFieldException, ChcException {
		MQ1409SecReceiveFileInd mq1409 = mqPrimitiveService.get1409PrimitiveMqFTS(ftsRecv, dto);

		CswLog.getLogData().setFunction("send1409FTS");
		CswLog.getLogData().setModule(ModuleEnum.INBOUND_MQ.name());
		CswLog.getLogData().setLocalBaId(ftsRecv.getLocalBaId());
		CswLog.getLogData().setRemoteBaId(ftsRecv.getRemoteBaId());
		CswLog.getLogData().setId(ftsRecv.getId().toString());

		byte[] primitiveByte = flowioMapper1409.writeByte(mq1409);
		
		PrimitivePool p = new PrimitivePool();
		p.setPrimitiveId(mq1409.getId());
		p.setServiceType(ServiceType.FTS);
		p.setEntityId(ftsRecv.getId());
		
		mqPrimitiveSenderService.storePrimitiveOnDB(primitiveByte, p);
	}
	
}
