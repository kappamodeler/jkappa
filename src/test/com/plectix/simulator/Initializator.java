package com.plectix.simulator;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.options.SimulatorArguments;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;

public class Initializator {
	private Simulator mySimulator;
	private List<IObservablesConnectedComponent> myObsComponents;
	private Double myRescale = null;
	
	private SimulatorArguments myArguments;
	private static boolean myFirstRun = true;
	
	private final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	public void setRescale(double rescale) {
		myRescale = rescale;
	}
	
	private void parseArgs(String filePath) {
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
		myArguments = SimulationUtils.parseArguments(
				mySimulator.getSimulationData(), args);
	}

	public void reset(String filePath) {
		parseArgs(filePath);
		mySimulator.resetSimulation();
	}
	
	public void init(String filePath) {
		if (myFirstRun) {
			PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
			
			mySimulator = new Simulator();

			parseArgs(filePath);

			SimulationUtils.readSimulatonFile(mySimulator, myArguments);
			mySimulator.init(myArguments);

			myFirstRun = false;
			myObsComponents = mySimulator.getSimulationData().getObservables()
				.getConnectedComponentList();
	
		} else {
			reset(filePath);
		}
	}
	
	public Simulator getSimulator() { 
		return mySimulator;
	}
	
	public List<IObservablesConnectedComponent> getObservables() {
		return Collections.unmodifiableList(myObsComponents);
	}

	
}