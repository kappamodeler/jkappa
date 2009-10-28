package com.plectix.simulator.smoke;

import static org.junit.Assert.fail;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public class TestENG310 extends SmokeTest {
	private static final String inputFile = "eng310" + DEFAULT_EXTENSION_FILE;
	

	protected String[] prepareTestArgs() {
		String[] args = new String[6];

		args[0] = "--sim";
		args[1] = inputDirectory + inputFile;
		args[2] = "--event";
		args[3] = "1";
		args[4] = "--rescale";
		args[5] = "0.001";
		return args;
	}

	@Test
	public void test() {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		Simulator mySimulator = new Simulator();

		String[] testArgs = prepareTestArgs();

		SimulationData simulationData = mySimulator.getSimulationData();

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
			simulationData.readSimulatonFile(InfoType.OUTPUT);
			fail("Incomplete substance!!");
		}catch (Exception e) {

		}
	}

}
