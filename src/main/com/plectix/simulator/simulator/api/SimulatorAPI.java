package com.plectix.simulator.simulator.api;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.steps.ContactMapComputationOperation;
import com.plectix.simulator.simulator.api.steps.DeadRuleDetectionOperation;
import com.plectix.simulator.simulator.api.steps.InfluenceMapComputationOperation;
import com.plectix.simulator.simulator.api.steps.InjectionBuildingOperation;
import com.plectix.simulator.simulator.api.steps.KappaFileCompilationOperation;
import com.plectix.simulator.simulator.api.steps.KappaFileLoadingOperation;
import com.plectix.simulator.simulator.api.steps.LocalViewsComputationOperation;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.simulator.api.steps.ReportErrorOperation;
import com.plectix.simulator.simulator.api.steps.RuleCompressionOperation;
import com.plectix.simulator.simulator.api.steps.SimulationOperation;
import com.plectix.simulator.simulator.api.steps.SimulatorInitializationOperation;
import com.plectix.simulator.simulator.api.steps.SolutionInitializationOperation;
import com.plectix.simulator.simulator.api.steps.SpeciesEnumerationOperation;
import com.plectix.simulator.simulator.api.steps.StoriesComputationOperation;
import com.plectix.simulator.simulator.api.steps.SubviewsComputationOperation;
import com.plectix.simulator.simulator.api.steps.XMLOutputOperation;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;
import com.plectix.simulator.util.Info.InfoType;

public class SimulatorAPI implements SimulatorAPIInterface {
	@Override
	public void initiateSimulator(Simulator simulator, SimulatorInputData inputData) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new SimulatorInitializationOperation(simulator, inputData));
	}
	
	@Override
	public void buildInjections(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new InjectionBuildingOperation(simulator.getSimulationData().getKappaSystem()));
	}

	@Override
	public void compileKappaFile(Simulator simulator, KappaFile kappaInput) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new KappaFileCompilationOperation(simulator.getSimulationData(), kappaInput, InfoType.OUTPUT));
	}

	@Override
	public void compressRulesQuality(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new RuleCompressionOperation(simulator.getSimulationData().getKappaSystem(), RuleCompressionType.QUALITATIVE));
	}

	@Override
	public void compressRulesQuantity(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new RuleCompressionOperation(simulator.getSimulationData().getKappaSystem(), RuleCompressionType.QUANTITATIVE));
	}

	@Override
	public void computeContactMap(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new ContactMapComputationOperation(simulator.getSimulationData()));
	}

	@Override
	public void computeInfluenceMap(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new InfluenceMapComputationOperation(simulator.getSimulationData()));
	}

	@Override
	public void computeLocalViews(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new LocalViewsComputationOperation(simulator.getSimulationData()));
	}

	@Override
	public void computeSubviews(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new SubviewsComputationOperation(simulator.getSimulationData().getKappaSystem()));
	}

	@Override
	public void computeStories(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new StoriesComputationOperation(simulator));
	}

	@Override
	public void enumerateSpecies(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new SpeciesEnumerationOperation(simulator.getSimulationData().getKappaSystem()));
	}

	@Override
	public void initializeSolution(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new SolutionInitializationOperation(simulator.getSimulationData()));
	}

	@Override
	public void loadKappaFile(Simulator simulator, String kappaInputId) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new KappaFileLoadingOperation(simulator.getSimulationData(), kappaInputId));
	}

	@Override
	public void locateDeadRules(Simulator simulator) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new DeadRuleDetectionOperation(simulator.getSimulationData().getKappaSystem()));
	}

	@Override
	public void outputToXML(Simulator simulator, String xmlDestinationPath) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new XMLOutputOperation(simulator.getSimulationData(), xmlDestinationPath));

	}

	@Override
	public void returnError(Simulator simulator, String message) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new ReportErrorOperation(message));
	}
	
	@Override
	public void returnError(Simulator simulator, Exception exception) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new ReportErrorOperation(exception));
	}

	@Override
	public void simulateByTime(Simulator simulator, double time) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		simulator.getSimulationData().getSimulationArguments().setTimeLimit(time);
		manager.perform(new SimulationOperation(simulator));
	}

	@Override
	public void simulateByEvents(Simulator simulator, long events) throws Exception {
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		simulator.getSimulationData().getSimulationArguments().setMaxNumberOfEvents(events);
		manager.perform(new SimulationOperation(simulator));
	}
}
