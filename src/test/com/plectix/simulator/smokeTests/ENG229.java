package com.plectix.simulator.smokeTests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.MemoryUtil.PeakMemoryUsage;

public class ENG229 {
	private static final String separator = File.separator;
	private static final String outputDirectory = "test.data"+ separator +"smoke_test" + separator + "results" + separator;
	private static final String inputDirectory = "test.data" + separator + "smoke_test" + separator + "source" + separator;
	private static final String inputFile = "eng229" + RunAllTests.FILENAME_EXTENSION;
	private static final String outputFile = "eng229" + RunAllTests.RESULTS_EXTENSION;

	public ENG229() {
		
	}
	
	@Test
	public void test() {
		PropertyConfigurator.configure(RunAllTests.LOG4J_PROPERTIES_FILENAME);
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
		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine.getSimulationArguments());
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
		try {
			mySimulator.run(0);
		} catch (Exception e) {
			junit.framework.Assert.fail(e.getMessage());
		}
		
		// Let's see if we monitor peak memory usage
		PeakMemoryUsage peakMemoryUsage = MemoryUtil.getPeakMemoryUsage();
		if (peakMemoryUsage != null) {
			simulationData.addInfo(InfoType.OUTPUT, InfoType.INFO, "-Peak Memory Usage (in bytes): " + peakMemoryUsage 
					+ " [period= " + simulationData.getSimulationArguments().getMonitorPeakMemory() + " milliseconds]");
		}
		
		try {
			simulationData.outputData(0);
		}catch (NullPointerException e) {
			e.printStackTrace();
			fail("Fail!");
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (StoryStorageException e) {
			e.printStackTrace();
		}
	}

	private static String[] prepareTestArgs() {
		/**
		 * --sim data/calcium2.ka --time 0.5 --operation-mode 1 --agents-limit
		 * 100 --xml-session-name Session.xml --seed 1 --live-data-interval 1000
		 * --live-data-points 500
		 */
		String[] args = new String[16];

		args[0] = "--sim";
		args[1] = inputDirectory + inputFile;
		args[2] = "--event";
		args[3] = "1";
		args[4] = "--operation-mode";
		args[5] = "1";
		args[6] = "--agents-limit";
		args[7] = "100";
		args[8] = "--xml-session-name";
		args[9] = outputDirectory + outputFile;
		args[10] = "--seed";
		args[11] = "1";
		args[12] = "--live-data-interval";
		args[13] = "1000";
		args[14] = "--live-data-points";
		args[15] = "500";
		return args;
	}
}