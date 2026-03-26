package com.cbi.ccr.csw.mq.domain.fms;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.cbi.ccr.csw.domain.FMSSendBlobAbstract;

@Entity
@Table(name = "FMS_SEND_MQI")
public class FMSSendBlobMQ extends FMSSendBlobAbstract {

}
