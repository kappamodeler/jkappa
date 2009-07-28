package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.abstractmodel.AbstractPerturbation;
import com.plectix.simulator.parser.abstractmodel.AbstractRule;
import com.plectix.simulator.parser.abstractmodel.AbstractSolution;
import com.plectix.simulator.parser.abstractmodel.AbstractStories;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.observables.AbstractObservables;
import com.plectix.simulator.parser.exceptions.SimulationDataFormatException;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;

public class KappaModelCreator {

	private final SimulationArguments myArguments;
	private AgentFactory myAgentFactory = new AgentFactory(false);
	private AgentFactory mySolutionAgentFactory = myAgentFactory;

	public KappaModelCreator(SimulationArguments arguments) {
		myArguments = arguments;
		if (!myArguments.incompletesAllowed()) {
			mySolutionAgentFactory = new AgentFactory(true);
		}
	}

	public KappaModel createModel(KappaFile file) throws SimulationDataFormatException {
		KappaModel model = new KappaModel();

		// simulationData.addInfo(InfoType.INFO,"--Computing initial state");

		if (myArguments.getSimulationType() != SimulationType.GENERATE_MAP) {
			AbstractSolution solution = (new SolutionParagraphReader(model,
					myArguments, mySolutionAgentFactory)).readComponent(file
					.getSolution());
			model.setSolution(solution);
		}

		Collection<AbstractRule> rules = (new RulesParagraphReader(model,
				myArguments, myAgentFactory)).readComponent(file.getRules());
		model.setRules(rules);

		AbstractStories stories = (new StoriesParagraphReader(model,
				myArguments, myAgentFactory)).readComponent(file.getStories());
		model.setStories(stories);

		AbstractObservables observables = (new ObservablesParagraphReader(
				model, myArguments, myAgentFactory)).readComponent(file
				.getObservables());
		model.setObservables(observables);

		List<AbstractPerturbation> perturbations = (new PerturbationsParagraphReader(
				model, myArguments, myAgentFactory)).readComponent(file
				.getPerturbations());
		model.setPerturbations(perturbations);

		return model;
	}
}
