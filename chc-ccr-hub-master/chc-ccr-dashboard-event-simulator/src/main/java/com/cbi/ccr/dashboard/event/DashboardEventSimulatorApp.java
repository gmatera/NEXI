package com.cbi.ccr.dashboard.event;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cbi.frw.micros.MainApp;
import com.cbi.frw.rabbitmq.ConfigRabbit;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = DashboardEventSimulatorApp.class)
@Import(value = {ConfigRabbit.class})
public class DashboardEventSimulatorApp extends MainApp {

	public static void main(String[] args) {
		MainApp.main(DashboardEventSimulatorApp.class);
	}
	
	@Override
	public void run(String... args) throws Exception {}

	@Override
	protected void run() {}

	@Override
	protected void shutdown() {}

}
