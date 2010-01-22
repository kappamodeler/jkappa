package com.plectix.simulator.smoke;

import junit.framework.Assert;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.util.Info.InfoType;

public class TestENG345 extends SmokeTest {
	private static final String inputFile = "eng345" + DEFAULT_EXTENSION_FILE;

	@Override
	protected String[] prepareTestArgs() {
		String[] args = new String[2];

		args[0] = "--compile";
		args[1] = inputDirectory + inputFile;
		return args;
	}

	@Override
	public void test() throws Exception {
		long timeStamp = System.currentTimeMillis();
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
		(new SimulationDataReader(simulationData)).readAndCompile();
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);

//		simulationData.getClock().setClockStamp(System.currentTimeMillis());

		if (simulationData.getSimulationArguments().isCompile()) {
			timeStamp  = System.currentTimeMillis() - timeStamp;
			Assert.assertTrue("Too long", timeStamp < 3000);
			return;
		}

		try {
			if (!simulationData.getSimulationArguments().debugModeIsOn()) {
				if (simulationData.getSimulationArguments().isGenereteMap()
						|| simulationData.getSimulationArguments()
								.getSimulationType() == SimulationType.CONTACT_MAP) {
				} else if (simulationData.getSimulationArguments().storiesModeIsOn()) {
					mySimulator.runStories();
				} else {
					mySimulator.runSimulation();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		timeStamp  = System.currentTimeMillis() - timeStamp;
		Assert.assertTrue("Too long", timeStamp < 3000);
	}

}
