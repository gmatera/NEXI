package com.cbi.ccr.csw.mq.outbound.bean;

import java.util.List;

import com.cbi.ccr.csw.mq.common.dto.PrimitiveMQ;
import com.cbi.ccr.csw.mq.domain.i.CswEntityMq;
import com.cbi.frw.http.AbstractPart;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class EntityToSend<E extends CswEntityMq, P extends PrimitiveMQ> {
	@NonNull
	private E entity;
	@NonNull
	private List<AbstractPart> parts;
	
	@NonNull
	private P primitiveDto;
}
