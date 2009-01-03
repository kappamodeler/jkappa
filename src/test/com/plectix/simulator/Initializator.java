package com.plectix.simulator;

import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;

public class Initializator {
	private Simulator mySimulator;
	private Double myRescale = null;
	
	private static boolean myFirstRun = true;
	
	private final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	public void setRescale(double rescale) {
		myRescale = rescale;
	}
	
	private String[] prepareTestArgs(String filePath) {
		boolean rescale = (myRescale != null);
		String[] args;
		if (!rescale) {
			args = new String[7];
		} else {
			args = new String[9];
			args[7] = "-rescale";
			args[8] = "" + myRescale;
		}
		args[0] = "--debug";
		args[1] = "--sim";
		args[2] = filePath;
		args[3] = "--time";
		args[4] = "5";
		args[5] = "--seed";
		args[6] = "10";
		return args;
	}

	public void reset(String filePath) {
		prepareTestArgs(filePath);
		mySimulator.resetSimulation();
	}
	
	public void init(String filePath) {
		String[] args = prepareTestArgs(filePath);
		if (myFirstRun) {
			init(prepareTestArgs(filePath));
			myFirstRun = false;
		} else {
			reset(filePath);
		}

	}
	
	public void init(String[] testArgs) {
			PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
			
			mySimulator = new Simulator();

			SimulationData simulationData = mySimulator.getSimulationData();

			SimulatorCommandLine commandLine = null;
			try {
				commandLine = new SimulatorCommandLine(testArgs);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
			
			simulationData.setSimulationArguments(commandLine.getSimulationArguments());
			simulationData.readSimulatonFile();
			simulationData.initialize();
	}
	
	public Simulator getSimulator() { 
		return mySimulator;
	}
	
	public List<IObservablesConnectedComponent> getObservables() {
		return Collections.unmodifiableList(mySimulator.getSimulationData().getObservables()
				.getConnectedComponentList());
	}
	
}