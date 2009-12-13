package com.plectix.simulator.harness;

import java.io.File;
import java.io.PrintStream;

import com.plectix.simulator.SimulatorTestOptions;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.io.xml.SimulationDataXMLWriter;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public class SimulatorTest extends Thread {
	private static Simulator simulator;
	private final String currentFile;
	private final String pathForResults;
	private final String fileName;

	private static final String EXTENSION_RESULT_FILE = ".txt";

	public SimulatorTest(String _path, String _resultPath, String _fileName) {
		currentFile = _path;
		pathForResults = _resultPath;
		fileName = _fileName;
		setDaemon(true);
	}

	private void init() throws Exception {
		simulator = new Simulator();
		SimulationData simulationData = simulator.getSimulationData();
		SimulatorCommandLine commandLine = SimulatorTestOptions
				.defaultContactMapCommandLine(currentFile, null);

		printSeparator();
		printNameFile();

		simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine
				.getSimulationArguments());
		(new SimulationDataReader(simulationData)).readSimulationFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
		simulator.runSimulation();
		new SimulationDataXMLWriter(simulationData).outputXMLData();
	}

	private void printSeparator() {
		System.out
				.println("========================================================================");
		System.out
				.println("========================================================================");
	}

	private void printSeparatorText() {
		System.out
				.println("------------------------------------------------------------------------");
	}

	private void printNameFile() {
		printSeparatorText();
//		System.out.println("RUN File = " + currentFile);
		printSeparatorText();
	}

	@Override
	public void run() {
		try {
			PrintStream out = new PrintStream(new File(pathForResults + "error_"
					+ fileName + EXTENSION_RESULT_FILE));
			System.setOut(out);
			System.setErr(out);
			init();
		} catch(Exception e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}
}
