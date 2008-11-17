package com.plectix.simulator;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.simulator.Model;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorManager;

public class Initializator {
	private SimulatorManager myManager;
	private Model myModel;
	private Simulator mySimulator;
	private List<ObservablesConnectedComponent> myObsComponents;
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
		args[4] = "15";
		args[5] = "--seed";
		args[6] = "10";
		instance.parseArguments(args);
	}

	public void reset(String filePath) {
		parseArgs(filePath);
		mySimulator.resetSimulation();
	}
	
	public void init(String filePath) {
		if (myFirstRun) {
			PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
			new SimulationMain();
			instance = SimulationMain.getInstance();

			parseArgs(filePath);

			instance.readSimulatonFile();
			instance.initialize();

			myManager = SimulationMain.getSimulationManager();

			myModel = new Model(myManager.getSimulationData());

			mySimulator = new Simulator(myModel);
			myFirstRun = false;
			myObsComponents = myManager.getSimulationData().getObservables()
				.getConnectedComponentList();
	
		} else {
			reset(filePath);
		}
	}
	
	public Model getModel() {
		return myModel;
	}
	
	public SimulatorManager getManager() {
		return myManager;
	}
	
	public Simulator getSimulator() { 
		return mySimulator;
	}
	
	public List<ObservablesConnectedComponent> getObservables() {
		return Collections.unmodifiableList(myObsComponents);
	}

	
}