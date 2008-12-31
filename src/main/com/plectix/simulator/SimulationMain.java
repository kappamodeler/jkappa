package com.plectix.simulator;

import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.controller.SimulationService;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.DefaultSimulatorFactory;

public class SimulationMain  {
	private static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	private static final Logger LOGGER = Logger.getLogger(SimulationMain.class);

	private static final PrintStream DEFAULT_OUTPUT_STREAM = System.err;
	
	public static final String COMMAND_LINE_SYNTAX = "use --sim [file] [options]";
	
	public static void main(String[] args) {
		initializeLogging();
		
		SimulationService service = new SimulationService(new DefaultSimulatorFactory());
		service.submit(new SimulatorInputData(args, DEFAULT_OUTPUT_STREAM), null);
		service.shutdown();
	}

	public static final void initializeLogging() {
		// Initialize log4j
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		
		// Dump important info:
		LOGGER.info("Build Date: " + BuildConstants.BUILD_DATE);
		LOGGER.info("SVN Revision: " + BuildConstants.BUILD_SVN_REVISION);
		LOGGER.info("Build OS: " + BuildConstants.BUILD_OS_NAME);
		LOGGER.info("Build Java Version: " + BuildConstants.JAVA_VERSION);
		LOGGER.info("Ant Java Version: " + BuildConstants.ANT_JAVA_VERSION);

		LOGGER.info("OS: " 
				+ System.getProperties().get("os.name") + " "
				+ System.getProperties().get("os.version") + ", "
				+ System.getProperties().get("os.arch"));
		
		LOGGER.info("Java Version: " 
				+ System.getProperties().get("java.version") + ", "
				+ System.getProperties().get("java.vendor"));
		
		LOGGER.info("Java Runtime: " 
				+ System.getProperties().get("java.runtime.name") + ", "
				+ System.getProperties().get("java.runtime.version"));
		
		LOGGER.info("Java VM: " 
				+ System.getProperties().get("java.vm.name") + ", "
				+ System.getProperties().get("java.vm.version") + ", "
				+ System.getProperties().get("java.vm.vendor") + ", "
				+ System.getProperties().get("java.vm.info"));
		
		LOGGER.info("Java Specifications: " +
				System.getProperties().get("java.specification.name") + ", "
				+ System.getProperties().get("java.specification.version") + ", "
				+ System.getProperties().get("java.specification.vendor"));
		
		LOGGER.info("Timezone: " + System.getProperties().get("user.timezone"));
	}

}
