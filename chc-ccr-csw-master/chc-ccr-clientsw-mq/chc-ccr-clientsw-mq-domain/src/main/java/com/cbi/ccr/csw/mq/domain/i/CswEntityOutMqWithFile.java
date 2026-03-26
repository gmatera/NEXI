package com.cbi.ccr.csw.mq.domain.i;

import java.time.LocalDateTime;

import com.cbi.ccr.csw.domain.csw.config.CodePage;
import com.cbi.ccr.csw.domain.csw.config.LineSeparator;
import com.cbi.ccr.csw.domain.csw.config.RecordFormat;
import com.cbi.ccr.csw.domain.i.CswEntityOutWithFile;
import com.cbi.ccr.csw.mq.domain.SendStatusMQ;

public interface CswEntityOutMqWithFile extends CswEntityMq, CswEntityOutWithFile{

	SendStatusMQ getStatus();

	void setStatus(SendStatusMQ cleanable);
	
	public void setStartCreateTimestamp(LocalDateTime startCreationTms);
	public void setEndCreateTimestamp(LocalDateTime endCreationTms);
	public void setErrorTimestamp(LocalDateTime errorTms);
	
	CodePage getCharType();
	void setCharType(CodePage codePage);
	RecordFormat getRecordFormat();
	void setRecordFormat(RecordFormat recordFormat);
	LineSeparator getLineSeparator();
	void setLineSeparator(LineSeparator lineSeparator);
	
	void setMaxRecLen(Integer maxRecLen);
	Integer getMaxRecLen();
	
	String getGroupId();
	String getQuequeFileName();
}
