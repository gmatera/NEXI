package com.cbi.ccr.orchestrator;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.frw.micros.MainApp;
import com.cbi.frw.rabbitmq.ConfigRabbit;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = OrchestratorSimulatorApp.class)
@Import(value = {ConfigRabbit.class})
public class OrchestratorSimulatorApp extends MainApp{
	
	public static void main(String[] args) {
		MainApp.main(OrchestratorSimulatorApp.class);
	}
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
}
