package com.plectix.simulator.smoke;

import static org.junit.Assert.fail;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.StringBufferWriter;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.MemoryUtil.PeakMemoryUsage;

public class TestENG229 extends SmokeTest {
	private static final String inputFile = "eng229"
		+ DEFAULT_EXTENSION_FILE;

	public TestENG229() {

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
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
		try {
			mySimulator.run(0);
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}

		// Let's see if we monitor peak memory usage
		PeakMemoryUsage peakMemoryUsage = MemoryUtil.getPeakMemoryUsage();
		if (peakMemoryUsage != null) {
			simulationData.addInfo(InfoType.OUTPUT, InfoType.INFO,
					"-Peak Memory Usage (in bytes): "
							+ peakMemoryUsage
							+ " [period= "
							+ simulationData.getSimulationArguments()
									.getMonitorPeakMemory() + " milliseconds]");
		}

		try {
			simulationData.outputXMLData(new StringBufferWriter());
		} catch (NullPointerException e) {
			e.printStackTrace();
			fail("Fail!");
		} catch (Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	protected String[] prepareTestArgs() {
		/**
		 * --sim data/calcium2.ka --time 0.5 --operation-mode 1 --agents-limit
		 * 100 --xml-session-name Session.xml --seed 1 --live-data-interval 1000
		 * --live-data-points 500
		 */
		String[] args = new String[14];

		args[0] = "--sim";
		args[1] = inputDirectory + inputFile;
		args[2] = "--event";
		args[3] = "1";
		args[4] = "--operation-mode";
		args[5] = "1";
		args[6] = "--agents-limit";
		args[7] = "100";
		args[8] = "--seed";
		args[9] = "1";
		args[10] = "--live-data-interval";
		args[11] = "1000";
		args[12] = "--live-data-points";
		args[13] = "500";
		return args;
	}
}