package com.cbi.ccr.csw.poller.db;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class Configuration {

    List<String> outboundWorkers = new ArrayList<>();

    public List<String> getOutboundWorkers() {
        return outboundWorkers;
    }

}