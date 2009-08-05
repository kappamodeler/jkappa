package com.plectix.simulator.harness.startAll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public class SimulatorTest  extends Thread {

	private static Simulator mySimulator;
	private static String currentFile;
	private static SimulationArguments argSimulation;
	private final String pathForResults;
	private final String fileName;
	
	private static final String EXTENSION_RESULT_FILE = ".txt";
	
	
	
	public SimulatorTest(String _path, String _resultPath, String _fileName) {
		currentFile = _path;
		pathForResults = _resultPath;
		fileName = _fileName;
		setDaemon(true);
	}


	private String[] prepareTestModelArgs(String path) {
		
		String[] args = new String[9];
		
		args[0] = "--short-console-output";
		args[1] = "--contact-map";
		args[2] = path;
		args[3] = "--no-dump-iteration-number";
		args[4] = "--no-dump-rule-iteration";
		args[5] = "--no-build-influence-map";
		args[6] = "--no-compute-quantitative-compression";
		args[7] = "--no-compute-qualitative-compression";
		args[8] = "--no-enumerate-complexes";
		return args;
	}
		
	private void init() {

		try {
					
			mySimulator = new Simulator();
		
			String[] testArgs = prepareTestModelArgs(currentFile);
		
			SimulationData simulationData = mySimulator.getSimulationData();

			SimulatorCommandLine commandLine = null;

			commandLine = new SimulatorCommandLine(testArgs);
			
			argSimulation = commandLine.getSimulationArguments();
		
			printSeparator();
			printNameFile();
			printCommandLine(testArgs);
		
			simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine.getSimulationArguments());
			simulationData.readSimulatonFile(InfoType.OUTPUT);
			simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
			mySimulator.run(0);
			simulationData.outputData(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void printSeparator() {
		System.out.println("========================================================================");
		System.out.println("========================================================================");
	}

	private void printCommandLine(String[] testArgs) {
		StringBuffer commandLineParaments = new StringBuffer();

		for (String arg : testArgs) {
		
			commandLineParaments.append(arg + " ");
		}

		printSeparatorText();
		System.out.println("CommandLine = " + commandLineParaments.toString());
		printSeparatorText();
	}

	private void printSeparatorText() {
		System.out.println("------------------------------------------------------------------------");
	}

	private void printNameFile() {
		printSeparatorText();
		System.out.println("RUN File = " + currentFile);
		printSeparatorText();
	}
	
	@Override
	public void run() {

		try {
			PrintStream out = new PrintStream(new File(pathForResults + "error_" + fileName + EXTENSION_RESULT_FILE));
			System.setOut(out);
			System.setErr(out);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		init();

	}
	
}
