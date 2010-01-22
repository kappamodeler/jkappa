package com.plectix.simulator.smoke;

import static org.junit.Assert.fail;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public class TestENG325 extends SmokeTest{
	private static final String inputFile = "eng325"
		+ DEFAULT_EXTENSION_FILE;

	@Override
	protected String[] prepareTestArgs() {
		String[] args = new String[4];
		args[0] = "--sim";
		args[1] = inputDirectory + inputFile;
		args[2] = "--event";
		args[3] = "1";
		return args;
	}

	@Override
	public void test() {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		Simulator mySimulator = new Simulator();

		String[] testArgs = prepareTestArgs();

		SimulationData simulationData = mySimulator.getSimulationData();
//		simulationData.setPrintStream(System.out);

		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(testArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine
				.getSimulationArguments());
		try{
			(new SimulationDataReader(simulationData)).readAndCompile();
			simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
		} catch (Exception e) {
			if(!e.getMessage().contains("line 5 : ['rule1']")){
				fail("Another Bug!!!");
			}
		}
	}

}
