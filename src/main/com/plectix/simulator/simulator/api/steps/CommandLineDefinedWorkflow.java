package com.plectix.simulator.simulator.api.steps;

import java.io.File;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorMessage;
import com.plectix.simulator.simulator.SimulatorStatus;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.MemoryUtil.PeakMemoryUsage;

public class CommandLineDefinedWorkflow extends AbstractOperation<File> {
	private final Simulator simulator; 
	private final SimulatorInputData simulatorInputData;
	
	public CommandLineDefinedWorkflow(Simulator simulator, SimulatorInputData simulatorInputData) {
		super(simulator.getSimulationData(), OperationType.STANDARD_WORKFLOW);
		this.simulator = simulator;
		this.simulatorInputData = simulatorInputData;
	}

	@Override
	protected File performDry() throws Exception {
		SimulationData simulationData = simulator.getSimulationData();
		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		SimulatorStatus simulatorStatus = simulator.getLatestFreezedStatus();
		 
		OperationManager manager = simulationData.getKappaSystem().getOperationManager();

		// feeding simulator with simulation arguments
		manager.performSequentially(new SimulatorInitializationOperation(simulator, simulatorInputData));
		
		// reading and compiling kappa file
		this.readAndCompileKappaInput();
		
		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_INITIALIZING);
		manager.performSequentially(new SolutionInitializationOperation(simulationData)); 

		// compiled kappa system object output 
		if (simulationData.getSimulationArguments().isCompile()) {
			consoleOutputManager.outputData();
			return null;
		}

		if (!this.hasNoNeedToRunAnything(simulationData.getSimulationArguments())) {
			if (simulationData.getSimulationArguments().storiesModeIsOn()) {
				simulator.runStories();
			} else {
				simulator.runSimulation();
			}
		}

		// Let's see if we monitor peak memory usage
		this.checkAndOutputMemory();

		// Output XML data:
		String destination = simulationData.getSimulationArguments().getXmlOutputDestination();
		simulationData.getKappaSystem().getOperationManager().performSequentially(new XMLOutputOperation(simulationData, destination));
		File xmlFile = new File(destination);

		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_IDLE);
		return xmlFile;
	}

	//TODO another operation for this?
	private final void readAndCompileKappaInput() throws Exception {
		SimulationData simulationData = simulator.getSimulationData();
		SimulatorStatus status = simulator.getLatestFreezedStatus();
		
		PlxTimer readingKappaTimer = new PlxTimer();
		readingKappaTimer.startTimer();

		status.setStatusMessage(SimulatorMessage.STATUS_READING_KAPPA);
		simulationData.getConsoleOutputManager().addAdditionalInfo(InfoType.INFO,
				"--Computing initial state");
		
		new SimulationDataReader(simulationData).readAndCompile();

		SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, readingKappaTimer,
				"-Reading Kappa input:");
	}
	
	private final void checkAndOutputMemory() {
		SimulationData simulationData = simulator.getSimulationData();
		PeakMemoryUsage peakMemoryUsage = MemoryUtil.getPeakMemoryUsage();
		if (peakMemoryUsage != null) {
			simulationData.getConsoleOutputManager().addAdditionalInfo(InfoType.INFO,
					"-Peak Memory Usage (in bytes): "
							+ peakMemoryUsage
							+ " [period= "
							+ simulationData.getSimulationArguments()
									.getMonitorPeakMemory() + " milliseconds]");
		}
	}

	private final boolean hasNoNeedToRunAnything(
			SimulationArguments simulationArguments) {
		return simulationArguments.isGenereteMap()
				|| (simulationArguments.getSimulationType() == SimulationType.CONTACT_MAP)
				|| simulationArguments.debugModeIsOn();
	}
	
	@Override
	protected boolean noNeedToPerform() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected File retrievePreparedResult() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
