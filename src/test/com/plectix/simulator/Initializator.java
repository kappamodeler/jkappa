package com.plectix.simulator;

import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

public class Initializator {
	private Simulator mySimulator;
	private List<IObservablesConnectedComponent> myObsComponents;
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
			args = new String[9];
		} else {
			args = new String[11];
			args[9] = "-rescale";
			args[10] = "" + myRescale;
		}
		args[0] = "--debug";
		args[1] = "--sim";
		args[2] = filePath;
		args[3] = "--time";
		args[4] = "5";
		args[5] = "--seed";
		args[6] = "10";
		args[7] = "--operation-mode";
		args[8] = "1";
		return args;
	}
	
	public static SimulationArguments prepareDefaultArguments(String filePath) throws ParseException{
		String[] args = new String[2];
		args[0] = "--compile";
		args[1] = filePath;
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}
	public static SimulationArguments prepareDefaultSimArguments(String filePath) throws ParseException{
		String[] args = new String[2];
		args[0] = "--sim";
		args[1] = filePath;
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}
	
	public static SimulationArguments prepareInitTimeArguments(String filePath, Double initTime) throws ParseException{
		String[] args = null;
		if (initTime.equals(-1.0)){
			args = new String[4];
			args[0] = "--sim";
			args[1] = filePath;
			args[2] = "--time";
			args[3] = Double.toString(50.0);
		}
		else {
			args = new String[6];
			args[0] = "--sim";
			args[1] = filePath;
			args[2] = "--time";
			args[3] = Double.toString(initTime + 100.0);
			args[4] = "--init";
			args[5] = initTime.toString();
		}
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}
	
	
	public static SimulationArguments prepareStorifyArguments(String filePath, boolean isSlow, boolean isWeak, boolean isStrong) throws ParseException{
		String[] args = new String[10];
		if (isStrong){
			args[8] = "--compress-stories";
			args[9] = "--use-strong-compression";
		}else if (isWeak){
			args[8] = "--compress-stories";
			args[9] = "--no-use-strong-compression";
		}else{
			args[8] = "--no-compress-stories";
			args[9] = "--no-use-strong-compression";
			
		}
		args[0] = "--storify";
		args[1] = filePath;
		args[2] = "--event";
		args[3] = "1000";
		args[4] = "--iteration";
		if (isSlow){
			args[5] = "100";
		}else {
			args[5] = "10";
		}
		args[6] = "--seed";
		args[7] = "13";
		
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}
	
	public static SimulationArguments prepareEventNumberArguments(String filePath, Integer eventNumber) throws ParseException{
		String[] args = new String[4];
		args[0] = "--sim";
		args[1] = filePath;
		args[2] = "--event";
		args[3] = eventNumber.toString();
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}
	
	public static SimulationArguments prepareTimeArguments(String filePath, Integer time) throws ParseException{
		String[] args = new String[4];
		args[0] = "--sim";
		args[1] = filePath;
		args[2] = "--time";
		args[3] = time.toString();
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}
	
	public static SimulationArguments prepareSimulationArguments(String[] args) throws ParseException {
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}
	
	public void reset(String filePath) {
		try {
			mySimulator.getSimulationData().setSimulationArguments(InfoType.OUTPUT, 
					prepareSimulationArguments(prepareTestArgs(filePath)));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		mySimulator.resetSimulation(InfoType.OUTPUT);
	}
	
	public void init(String filePath) {
		String[] testArgs = prepareTestArgs(filePath);
		if (myFirstRun) {
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
			
			simulationData.setSimulationArguments(InfoType.OUTPUT,commandLine.getSimulationArguments());
			simulationData.readSimulatonFile(InfoType.OUTPUT);
			simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
			
			myFirstRun = false;
			myObsComponents = mySimulator.getSimulationData().getKappaSystem().getObservables().getConnectedComponentList();
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