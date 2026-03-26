package com.cbi.ccr.csw.service.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.cbi.frw.micros.MainApp;

public abstract class CcrAbstratctApp extends MainApp{
	@Autowired
	private LivenessService livenessService;
	
	@Override
	protected void run() {
		livenessService.registerServices();
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
