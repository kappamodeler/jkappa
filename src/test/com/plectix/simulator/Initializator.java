package com.plectix.simulator;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;

public class Initializator {
	private Simulator mySimulator;
	private List<IObservablesConnectedComponent> myObsComponents;
	private SimulationMain instance;
	private Double myRescale = null;
	
	private static boolean myFirstRun = true;
	
	private final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";

	public void setRescale(Double rescale) {
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
		instance.cmdLineArgs = SimulationMain.parseArguments(mySimulator.getSimulationData(), args, SimulationMain.cmdLineOptions);
	}

	public void reset(String filePath) {
		parseArgs(filePath);
		mySimulator.resetSimulation();
	}
	
	public void init(String filePath) {
		if (myFirstRun) {
			PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
			
			instance = new SimulationMain();
			mySimulator = new Simulator();

			parseArgs(filePath);

			SimulationMain.readSimulatonFile(mySimulator, instance.cmdLineArgs);
			mySimulator.init(instance.cmdLineArgs);

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