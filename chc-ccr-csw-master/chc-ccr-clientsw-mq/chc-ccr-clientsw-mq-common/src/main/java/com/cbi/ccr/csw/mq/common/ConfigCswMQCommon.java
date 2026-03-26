package com.cbi.ccr.csw.mq.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.cbi.frw.ibmmq.ConfigIbmMq;

@Profile("MQ")
@Configuration
@ComponentScan(basePackageClasses = { ConfigCswMQCommon.class})
@Import({ConfigIbmMq.class})
public class ConfigCswMQCommon{
}
