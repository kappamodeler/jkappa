package com.plectix.simulator.simulator.api.steps;

import java.util.LinkedList;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorMessage;
import com.plectix.simulator.simulator.SimulatorStatus;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;

public class ExperimentWorkflow extends AbstractOperation<Object> {
	private final Simulator simulator; 
	
	public ExperimentWorkflow(Simulator simulator) {
		super(simulator.getSimulationData(), OperationType.STANDARD_WORKFLOW);
		this.simulator = simulator;
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
		
		return operations;
	}
	
	
	@Override
	protected Object performDry() throws Exception {
		SimulationData simulationData = simulator.getSimulationData();
		SimulatorStatus simulatorStatus = simulator.getLatestFreezedStatus();
		 
		// performing all set operations
		for (AbstractOperation<?> operation : this.prepareOperations(simulationData.getSimulationArguments())) {
			simulationData.getKappaSystem().getOperationManager().perform(operation);
		}

		simulatorStatus.setStatusMessage(SimulatorMessage.STATUS_IDLE);
		return null;
//		return new ExperimentOutput(simulationData);
	}

	@Override
	protected boolean noNeedToPerform() {
		return false;
	}

	@Override
	protected Object retrievePreparedResult() throws Exception {
		return null;
	}

}
