package com.plectix.simulator.simulator.api.steps;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorMessage;
import com.plectix.simulator.simulator.SimulatorStatus;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.MemoryUtil.PeakMemoryUsage;

public class CommandLineDefinedWorkflow extends AbstractOperation<File> {
	private final Simulator simulator; 
	private final SimulatorInputData simulatorInputData;
	private boolean xmlOutputIsTurnedOn = true; 
	
	public CommandLineDefinedWorkflow(Simulator simulator, SimulatorInputData simulatorInputData) {
		super(simulator.getSimulationData(), OperationType.STANDARD_WORKFLOW);
		this.simulator = simulator;
		this.simulatorInputData = simulatorInputData;
	}
	
	public final void turnOffXMLOutput() {
		xmlOutputIsTurnedOn = false;
	}

	/**
	 * Very important method which defines the set of operations
	 * simulator is supposed to perform when current SimulationArguments set
	 */
	private LinkedList<AbstractOperation<?>> prepareOperations(SimulationArguments arguments) {
		SimulationData simulationData = simulator.getSimulationData();
		
		LinkedList<AbstractOperation<?>> operations = new LinkedList<AbstractOperation<?>>();
		
		if (arguments.needToDumpHelp()) {
			operations.add(new DumpHelpOperation(simulationData));
		}
		if (arguments.needToDumpVersion()) {
			operations.add(new DumpVersionOperation(simulationData));
		}
		if (arguments.needToCreateLocalViews()) {
			operations.add(new LocalViewsComputationOperation(simulationData));
		}
		if (arguments.needToCreateSubViews()) {
			operations.add(new SubviewsComputationOperation(simulationData.getKappaSystem()));
		}
		if (arguments.needToOutputDebugInformation()) {
			// TODO do nothing?
		}
		if (arguments.needToStorify()) {
			operations.add(new StoriesComputationOperation(simulator));
		}
		if (arguments.needToSimulate()) {
			operations.add(new SimulationOperation(simulator));
		}
		if (arguments.needToCompile()) {
			operations.add(new OutputCompiledFileOperation(simulationData));
		}
		if (arguments.needToBuildContactMap()) {
			operations.add(new ContactMapComputationOperation(simulationData));
		}
		
		if (arguments.needToBuildInfluenceMap()) {
			operations.add(new InfluenceMapComputationOperation(simulationData));
		} else {
			if (arguments.needToBuildActivationMap()) {
				operations.add(new ActivationMapComputationOperation(simulationData));
			}
			if (arguments.needToBuildInhibitionMap()) {
				operations.add(new InhibitionMapComputationOperation(simulationData));
			}
		}
		if (arguments.needToRunQualitativeCompression()) {
			operations.add(new RuleCompressionOperation(simulationData.getKappaSystem(), 
					RuleCompressionType.QUALITATIVE));
		}
		if (arguments.needToRunQuantitativeCompression()) {
			operations.add(new RuleCompressionOperation(simulationData.getKappaSystem(), 
					RuleCompressionType.QUANTITATIVE));
		}
		if (arguments.needToEnumerationOfSpecies()) {
			operations.add(new SpeciesEnumerationOperation(simulationData.getKappaSystem()));
		}
		if (arguments.needToFindDeadRules()) {
			operations.add(new DeadRuleDetectionOperation(simulationData.getKappaSystem()));
		}
		
		// Output XML data:
		String destination = simulationData.getSimulationArguments().getXmlOutputDestination();
		if (xmlOutputIsTurnedOn) {
			operations.add(new XMLOutputOperation(simulationData, destination));
		}
		return operations;
	}
	
	
	@Override
	protected File performDry() throws Exception {
		SimulationData simulationData = simulator.getSimulationData();
//		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		SimulatorStatus simulatorStatus = simulator.getLatestFreezedStatus();
		 
		OperationManager manager = simulationData.getKappaSystem().getOperationManager();

		// feeding simulator with simulation arguments
		manager.perform(new SimulatorInitializationOperation(simulator, simulatorInputData));
		
		// reading and compiling kappa file
//		this.readAndCompileKappaInput();
		
//		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_INITIALIZING);
//		manager.performSequentially(new SolutionInitializationOperation(simulationData)); 

		// compiled kappa system object output 
//		if (simulationData.getSimulationArguments().needToCompile()) {
//			consoleOutputManager.outputData();
//			return null;
//		}

		for (AbstractOperation<?> operation : this.prepareOperations(simulationData.getSimulationArguments())) {
			simulationData.getKappaSystem().getOperationManager().perform(operation);
		}
//		
//		if (!this.hasNoNeedToRunAnything(simulationData.getSimulationArguments())) {
//			if (simulationData.getSimulationArguments().needToStorify()) {
//				simulator.runStories();
//			} else {
//				simulator.runSimulation();
//			}
//		}

		// Let's see if we monitor peak memory usage
		this.checkAndOutputMemory();
	
		String destination = simulationData.getSimulationArguments().getXmlOutputDestination();
		File xmlFile = new File(destination);

		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_IDLE);
		return xmlFile;
	}

	public List<AbstractOperation<?>> checkoutOperationsSet() throws Exception {
		SimulationData simulationData = simulator.getSimulationData();
		OperationManager manager = simulationData.getKappaSystem().getOperationManager();

		manager.perform(new SimulatorInitializationOperation(simulator, simulatorInputData));
		
		return this.prepareOperations(simulationData.getSimulationArguments());
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
				|| simulationArguments.needToOutputDebugInformation();
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
