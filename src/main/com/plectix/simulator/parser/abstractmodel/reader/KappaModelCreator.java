package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.ModelPerturbation;
import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.parser.abstractmodel.ModelSolution;
import com.plectix.simulator.parser.abstractmodel.ModelStories;
import com.plectix.simulator.parser.abstractmodel.observables.ModelObservables;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;

public final class KappaModelCreator {
	private final SimulationArguments simulationArguments;
	private final AgentFactory defaultAgentFactory = new AgentFactory(true);
	private final AgentFactory solutionAgentFactory;

	public KappaModelCreator(SimulationArguments simulationArguments) {
		this.simulationArguments = simulationArguments;
		solutionAgentFactory = new AgentFactory(simulationArguments.incompletesAllowed());
	}

	public final KappaModel createModel(KappaFile kappaFile) throws SimulationDataFormatException {
		KappaModel model = new KappaModel();

		if (simulationArguments.getSimulationType() != SimulationType.GENERATE_MAP) {
			ModelSolution solution = (new SolutionParagraphReader(
					simulationArguments, solutionAgentFactory)).readComponent(kappaFile
					.getSolution());
			model.setSolution(solution);
		}

		Collection<ModelRule> rules = (new RulesParagraphReader(
				simulationArguments, defaultAgentFactory)).readComponent(kappaFile.getRules());
		model.setRules(rules);

		ModelStories stories = (new StoriesParagraphReader(
				simulationArguments, defaultAgentFactory)).readComponent(kappaFile.getStories());
		model.setStories(stories);

		ModelObservables observables = (new ObservablesParagraphReader(
				simulationArguments, defaultAgentFactory)).readComponent(kappaFile.getObservables());
		model.setObservables(observables);

		List<ModelPerturbation> perturbations = (new PerturbationsParagraphReader(
				simulationArguments, defaultAgentFactory)).readComponent(kappaFile
				.getPerturbations());
		model.setPerturbations(perturbations);

		return model;
	}
}
