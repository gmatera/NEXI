package com.cbi.ccr.csw.mq.inbound;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.inbound.common.ConfigCswInboundCommon;
import com.cbi.ccr.csw.mq.common.ConfigCswMQCommon;
import com.cbi.ccr.csw.mq.domain.ConfigCswDomainMQ;
import com.cbi.ccr.csw.service.common.ConfigCommonService;

@Profile("MQ")
@Configuration
@Import(value = {ConfigCswDomain.class, ConfigCswDomainMQ.class, ConfigCommonService.class, ConfigCswInboundCommon.class, ConfigCswMQCommon.class })
@ComponentScan(basePackageClasses = { ConfigCswInboundMQ.class})
public class ConfigCswInboundMQ{


}
