package com.cbi.ccr.csw.domain.i;

public interface CswOutWithMessage extends CswOutboundEntity, HasMessage{

	String getMsgDigest();

}
