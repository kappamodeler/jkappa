package com.plectix.simulator.util;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public abstract class BasicTestByModel extends DefaultPropertiesForTest {

	private Simulator simulator;
	private String mycount;
	private String directory;
	private Integer operationMode;

	public abstract SimulatorTestOptions prepareTestModelArgs();

	public void initializeSimulation(String dir, String filename, Integer opMode) throws Exception {

		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		simulator = new Simulator();
		directory = dir;
		mycount = filename;
		operationMode = opMode;

		SimulationData simulationData = simulator.getSimulationData();

		SimulatorCommandLine commandLine;

		try {
			commandLine = prepareTestModelArgs().toCommandLine();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine
				.getSimulationArguments());
		(new SimulationDataReader(simulationData)).readSimulationFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public String getMyCountInside() {
		return mycount;
	}

	public String getDirectoryInside() {
		return directory;
	}

	public String defaultSourseFileName(String path) {
		return path + DEFAULT_PREFIX_SOURSE_FILE + mycount
				+ DEFAULT_EXTENSION_FILE;
	}

	public String defaultModelFileName() {
		return directory + DEFAULT_PREFIX_MODEL_FILE + mycount
				+ DEFAULT_EXTENSION_FILE;
	}

	public String defaultExtentionAndCountFileName(String path,
			String prefixFileName) {
		return path + prefixFileName + mycount + DEFAULT_EXTENSION_FILE;
	}
	
	public Integer getOperationMode() {
		return operationMode;
	}

}
