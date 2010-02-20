package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;

public class SolutionInitializationOperation extends AbstractOperation<KappaSystem> {
	private final SimulationData simulationData;
	
	public SolutionInitializationOperation(SimulationData simulationData) {
		super(simulationData, OperationType.INITIALIZATION);
		this.simulationData = simulationData;
	}
	
	protected KappaSystem performDry() throws Exception {
		SimulationArguments args = simulationData.getSimulationArguments();

		KappaSystem kappaSystem = simulationData.getKappaSystem();
		OperationManager manager = kappaSystem.getOperationManager();
		
		Observables observables = kappaSystem.getObservables();
		SolutionInterface solution = kappaSystem.getSolution();
		
		observables.init(args.getTimeLimit(), args.getInitialTime(), args
				.getMaxNumberOfEvents(), args.getPoints(), args.isTime());
		observables.checkAutomorphisms();

		// !!!!!!!!INJECTIONS!!!!!!!!!
		if (args.solutionNeedsToBeRead()) {
			manager.perform(new InjectionBuildingOperation(kappaSystem));
		}

		if (solution.getSuperStorage() != null) {
			solution.getSuperStorage().setAgentsLimit(args.getAgentsLimit());
		}

		if (args.createSubViews()) {
			manager.perform(new SubviewsComputationOperation(kappaSystem));
		}
			
		if (args.needToFindDeadRules()) {
			manager.perform(new DeadRuleDetectionOperation(kappaSystem));
		}
		
		if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			manager.perform(new ContactMapComputationOperation(simulationData));
		}

		if (args.needToBuildActivationMap() || args.needToBuildInhibitionMap()) {
			kappaSystem.setInfluenceMap(manager.perform(new InfluenceMapComputationOperation(simulationData)));
		}

		if (args.createLocalViews()) {
			kappaSystem.setLocalViews(manager.perform(new LocalViewsComputationOperation(simulationData)));
		}

		if (args.needToEnumerationOfSpecies()) {
			manager.perform(new SpeciesEnumerationOperation(kappaSystem));
		}

		if (args.needToRunQualitativeCompression()) {
			manager.perform(new RuleCompressionOperation(kappaSystem, RuleCompressionType.QUALITATIVE));
		}

		if (args.needToRunQuantitativeCompression()) {
			manager.perform(new RuleCompressionOperation(kappaSystem, RuleCompressionType.QUANTITATIVE));
		}
		
		return kappaSystem;
	}

	@Override
	protected boolean noNeedToPerform() {
		return simulationData.getKappaSystem().getState().kappaSystemIsInitialized();
//		return false;
	}

	@Override
	protected KappaSystem retrievePreparedResult() {
		return simulationData.getKappaSystem();
	}

}
