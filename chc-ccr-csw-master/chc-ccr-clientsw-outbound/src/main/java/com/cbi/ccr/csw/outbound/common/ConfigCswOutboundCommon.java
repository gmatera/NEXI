package com.cbi.ccr.csw.outbound.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.cbi.ccr.csw.domain.ConfigCswDomain;
import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.service.common.LivenessService.ServiceRoles;
import com.cbi.frw.common.ConfigCommon;
import com.cbi.frw.encryption.HubEncryptionUtil;
import com.cbi.frw.http.ConfigHttp;

@Configuration
@Import(value = {ConfigCommon.class, ConfigCswDomain.class, ConfigHttp.class})
@ComponentScan(basePackageClasses = {ConfigCswOutboundCommon.class, HubEncryptionUtil.class})
public class ConfigCswOutboundCommon {
}
