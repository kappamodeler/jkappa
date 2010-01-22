package com.plectix.simulator.harness.contactmap;

import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.simulator.CompiledKappaFile;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.util.DefaultPropertiesForTest;
import com.plectix.simulator.util.Info.InfoType;

public class InitTestContactMap extends DefaultPropertiesForTest {

	private static final String testModel = "model";
	private static Simulator simulator;
	private static String sessionPath;
	private static String counter;
	private static String directory;

	private static SimulationArguments argSimulation;

	private static final String fileName(String suffix, String number) {
		return directory + suffix + number + DEFAULT_EXTENSION_FILE;
	}
	
	public static void init(String dir, String dirResult, String count) throws Exception {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		simulator = new Simulator();
		directory = dir;
		sessionPath = dirResult;
		counter = count;
		SimulationData simulationData = simulator.getSimulationData();
		SimulatorCommandLine commandLine = null;
		try {
			if (dir.contains(testModel)) {
				String filename = fileName("~kappa", count);
				commandLine = SimulatorTestOptions.defaultContactMapCommandLine(filename, null);
			} else {
				String filename = fileName("~kappa", count);
				SimulatorTestOptions options = SimulatorTestOptions.defaultContactMapOptions(filename, null);
				options.appendFocus(fileName("~focus", count));
				commandLine = options.toCommandLine();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		argSimulation = commandLine.getSimulationArguments();
		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine
				.getSimulationArguments());
		SimulationDataReader reader = new SimulationDataReader(simulationData);
		KappaFile file = reader.readSimulationFile();
		(new SimulationDataReader(simulationData)).readAndCompile();
	}

	public static SimulationArguments getSimulationArguments() {
		return argSimulation;
	}

	public static SolutionInterface getSolution() {
		return simulator.getSimulationData().getKappaSystem().getSolution();
	}

	public static List<Rule> getRules() {
		return simulator.getSimulationData().getKappaSystem().getRules();
	}

	public static SimulationData getSimulationData() {
		return simulator.getSimulationData();
	}
}
