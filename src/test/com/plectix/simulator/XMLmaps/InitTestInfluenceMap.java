package com.plectix.simulator.XMLmaps;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.junit.*;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.SimulationMain;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;


@RunWith(value=Suite.class)
@SuiteClasses(value = {
		TestInfluenceMap.class
	})
public class InitTestInfluenceMap {
	private static final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	private static Process process;
	private static Scanner scanner;
	private static PrintWriter writer;

	private static Simulator mySimulator;
	private static SimulationArguments myArguments;
	private static boolean isMergeMaps = true;
	
	private static double time = 10;


	
	@BeforeClass
	public static void setup() {

		String filePath = "large_systems-sysepi.ka";
		String prefix = "C:\\Documents and Settings\\lopatkinat\\workspace\\simulator\\";
		String patch = "plectix\\windows\\simplx.exe" + 
						" --generate-map " +  "plectix\\windows\\" +
						filePath + 
						" --time 10 --seed 1 --merge-maps";
		patch = "plectix\\windows\\simplx.exe  --generate-map plectix\\windows\\Example.ka";

		patch = "plectix\\windows\\simplx.exe";
//		gererateXML(patch);
		setup("plectix\\windows\\" + filePath, time );	
	
	}
	

	private static void gererateXML(String patch) {
		Runtime runtime = Runtime.getRuntime();
		List<String> command = new ArrayList<String>();
		command.add("plectix\\windows\\simplx.exe");
		command.add(" --generate-map ");
		command.add("plectix\\windows\\Example.ka ");
		command.add(" --merge-maps ");
		
		String prefix = "\"C:\\Documents and Settings\\lopatkinat\\workspace\\simulator\\";
		
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder = processBuilder.command(command);
		try {
			process = runtime.exec(prefix + 
					"plectix\\windows\\simplx.exe\" " +
					"--generate-map " + prefix +
					"plectix\\windows\\Example.ka\"" +
					" --merge-maps");

		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream inputStream2 = process.getInputStream();
		scanner = new Scanner(inputStream2);
		writer = new PrintWriter(process.getOutputStream());
	}
	
	public static void setup(String fileName, double time) {
		String fullTestFilePath = fileName;
		init(fullTestFilePath);
		mySimulator.getSimulationData().setTimeLength(time);
		mySimulator.getSimulationData().getSimulationArguments().setXmlSessionName("simplexTest.xml");
		try {
			mySimulator.run(0);
		} catch (Exception e) {
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	
	private static String[] prepareTestArgs(String filePath) {
		String[] args;
		if (!isMergeMaps) {
			args = new String[8];
		} else {
			args = new String[9];
			args[8] = "--merge_maps";
		}
		args[0] = "--generate_map";
		args[1] = filePath;
		args[2] = "--time";
		args[3] = "5";
		args[4] = "--seed";
		args[5] = "1";
		args[6] = "--ocaml_style_obs_name";
		args[7] = "--no_save_all";
		return args;
	}
	
	
	public static void init(String filePath) {
			PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
			mySimulator = new Simulator();
			String[] testArgs = prepareTestArgs(filePath);

			SimulationData simulationData = mySimulator.getSimulationData();

			SimulatorCommandLine commandLine = null;
			try {
				commandLine = new SimulatorCommandLine(testArgs);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
			simulationData.setSimulationArguments(InfoType.OUTPUT,commandLine.getSimulationArguments());
			simulationData.readSimulatonFile(InfoType.OUTPUT);
			simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
			
//			mySimulator.getSimulationData().readSimulatonFile(mySimulator);
//			mySimulator.init(myArguments);
		}
}
