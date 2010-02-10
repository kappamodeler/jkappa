package com.plectix.simulator;

import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;

import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.simulator.api.steps.SolutionInitializationOperation;
import com.plectix.simulator.util.DefaultPropertiesForTest;
import com.plectix.simulator.util.SimulatorRenewer;
import com.plectix.simulator.util.Info.InfoType;

public class Initializator extends DefaultPropertiesForTest {
	private Simulator simulator;
	private List<ObservableConnectedComponentInterface> myObsComponents;
	private Double myRescale = null;

	private static boolean myFirstRun = true;

	public void setRescale(double rescale) {
		myRescale = rescale;
	}

	private String[] prepareTestArgs(String filePath, Integer opMode) {
		boolean rescale = (myRescale != null);
		String[] args;
		if (!rescale) {
			args = new String[10];
			args[9] = "--allow-incomplete-substance";
		} else {
			args = new String[12];
			args[9] = "-rescale";
			args[10] = "" + myRescale;
			args[11] = "--allow-incomplete-substance";
		}
		args[0] = "--debug";
		args[1] = "--sim";
		args[2] = filePath;
		args[3] = "--time";
		args[4] = "5";
		args[5] = "--seed";
		args[6] = "10";
		args[7] = "--operation-mode";
		if (opMode != null)
			args[8] = opMode.toString();
		else
			args[8] = "1";
			
		return args;
	}

	public static SimulationArguments prepareDefaultArguments(String filePath, Integer opMode)
			throws ParseException {
		String[] args = new String[5];
		args[0] = "--compile";
		args[1] = filePath;
		args[2] = "--allow-incomplete-substance";
		args[3] = "--operation-mode";
		if (opMode == null)
			args[4] = "1";
		else
			args[4] = opMode.toString();
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}

	public static SimulationArguments prepareInitTimeArguments(String filePath,
			Double initTime, Integer opMode) throws ParseException {
		String[] args = null;
		if (initTime.equals(-1.0)) {
			args = new String[7];
			args[0] = "--sim";
			args[1] = filePath;
			args[2] = "--time";
			args[3] = Double.toString(5.0);
			args[4] = "--allow-incomplete-substance";
			args[5] = "--operation-mode";
			if (opMode == null)
				args[6] = "1";
			else
				args[6] = opMode.toString();
		} else {
			args = new String[9];
			args[0] = "--sim";
			args[1] = filePath;
			args[2] = "--time";
			args[3] = Double.toString(initTime + 5.0);
			args[4] = "--init";
			args[5] = initTime.toString();
			args[6] = "--allow-incomplete-substance";
			args[7] = "--operation-mode";
			if (opMode == null)
				args[8] = "1";
			else
				args[8] = opMode.toString();
		}
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}

	public static SimulationArguments prepareStorifyArguments(String filePath,
			boolean isSlow, boolean isWeak, boolean isStrong, boolean isEvent,
			Long numberOfEventOrTime, Integer seed, Integer opMode) throws ParseException {
		String[] args = new String[13];
		args[10] = "--allow-incomplete-substance";
		if (isStrong) {
			args[8] = "--compress-stories";
			args[9] = "--use-strong-compression";
		} else if (isWeak) {
			args[8] = "--compress-stories";
			args[9] = "--no-use-strong-compression";
		} else {
			args[8] = "--no-compress-stories";
			args[9] = "--no-use-strong-compression";

		}
		args[0] = "--storify";
		args[1] = filePath;
		if (isEvent) {
			args[2] = "--event";
			args[3] = numberOfEventOrTime.toString();
		} else {
			args[2] = "--time";
			args[3] = numberOfEventOrTime.toString();
		}
		args[4] = "--iteration";
		if (isSlow) {
			args[5] = "100";
		} else {
			args[5] = "10";
		}
		args[6] = "--seed";
		args[7] = seed.toString();
		
		args[11] = "--operation-mode";
		if (opMode == null)
			args[12] = "1";
		else
			args[12] = opMode.toString();

		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}


	public static SimulationArguments prepareEventNumberArguments(
			String filePath, Integer eventNumber, Integer opMode) throws ParseException {
		String[] args = new String[7];
		args[0] = "--sim";
		args[1] = filePath;
		args[2] = "--event";
		args[3] = eventNumber.toString();
		args[4] = "--allow-incomplete-substance";
		args[5] = "--operation-mode";
		if (opMode == null)
			args[6] = "1";
		else 
			args[6] = opMode.toString();
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}

	public static SimulationArguments prepareTimeArguments(String filePath,
			Integer time, Integer opMode) throws ParseException {
		String[] args = new String[7];
		args[0] = "--sim";
		args[1] = filePath;
		args[2] = "--time";
		args[3] = time.toString();
		args[4] = "--allow-incomplete-substance";
		args[5] = "--operation-mode";
		if (opMode == null)
			args[6] = "1";
		else
			args[6] = opMode.toString();
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}

	public static SimulationArguments prepareSimulationArguments(String[] args)
			throws ParseException {
		SimulatorCommandLine commandLine = null;
		commandLine = new SimulatorCommandLine(args);
		return commandLine.getSimulationArguments();
	}

	public void reset(String filePath, Integer opMode) throws Exception {
		try {
			simulator.getSimulationData().setSimulationArguments(
					InfoType.OUTPUT,
					prepareSimulationArguments(prepareTestArgs(filePath, opMode)));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		SimulatorRenewer.renew(simulator);
	}

	public void init(String filePath, Integer opMode) throws Exception {
		String[] testArgs = prepareTestArgs(filePath, opMode);
		if (myFirstRun) {
			PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);

			simulator = new Simulator();

			SimulationData simulationData = simulator.getSimulationData();

			SimulatorCommandLine commandLine = null;
			try {
				commandLine = new SimulatorCommandLine(testArgs);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}

			simulationData.setSimulationArguments(InfoType.OUTPUT, commandLine.getSimulationArguments());
			
//			(new SimulationDataReader(simulationData)).readAndCompile();
//			simulationData.getKappaSystem().initialize();
			
			OperationManager manager = simulationData.getKappaSystem().getOperationManager();
			manager.perform(new SolutionInitializationOperation(simulationData));

			myFirstRun = false;
			myObsComponents = simulator.getSimulationData().getKappaSystem().getObservables().getConnectedComponentList();
		} else {
			reset(filePath, opMode);
		}

	}
	public Simulator getSimulator() {
		return simulator;
	}

	public List<ObservableConnectedComponentInterface> getObservables() {
		return myObsComponents;
	}

}