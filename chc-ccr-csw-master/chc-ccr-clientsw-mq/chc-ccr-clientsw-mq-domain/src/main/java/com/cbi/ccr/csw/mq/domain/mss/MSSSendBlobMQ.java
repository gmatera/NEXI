package com.cbi.ccr.csw.mq.domain.mss;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.cbi.ccr.csw.domain.MSSSendBlobAbstract;

@Entity
@Table(name = "MSS_SEND_MQI")
public class MSSSendBlobMQ extends MSSSendBlobAbstract {

}
