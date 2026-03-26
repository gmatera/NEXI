package com.cbi.ccr.csw.dashboard;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.ccr.csw.domain.csw.ServiceRole;
import com.cbi.ccr.csw.service.common.CcrAbstratctApp;
import com.cbi.frw.micros.MainApp;

@Configuration
@Import(value = {ConfigCswDashboardApi.class})
public class CswDashboardApp extends CcrAbstratctApp{
	
	public static void main(String[] args) {
		
		MainApp.main(CswDashboardApp.class, ServiceRole.DASHBOARD.name());
	}
	
	@Override
	public void run(String... args) throws Exception {
		// not used
		
	}

	@Override
	protected void shutdown() {
		// not used
		
	}
	
}
