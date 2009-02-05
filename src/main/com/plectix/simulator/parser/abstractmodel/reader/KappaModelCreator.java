package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.*;

import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.*;
import com.plectix.simulator.parser.abstractmodel.observables.AbstractObservables;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;

public class KappaModelCreator {

	private final SimulationArguments myArguments;
	private AgentFactory myAgentFactory = new AgentFactory();

	public KappaModelCreator(SimulationArguments arguments) {
		myArguments = arguments;
	}

	// TODO stateg?
	public KappaModel createModel(KappaFile file) throws ParseErrorException {
		KappaModel model = new KappaModel();

		// simulationData.addInfo(InfoType.INFO,"--Computing initial state");

		if (myArguments.getSimulationType() != SimulationType.GENERATE_MAP) {
			AbstractSolution solution = (new SolutionParagraphReader(model,
					myArguments, myAgentFactory)).addComponent(file
					.getSolution());
			model.setSolution(solution);
		}

		// System.out.println(myAgentFactory.getCurrentId());
		Collection<AbstractRule> rules = (new RulesParagraphReader(model,
				myArguments, myAgentFactory)).addComponent(file.getRules());
		model.setRules(rules);

		AbstractStories stories = (new StoriesParagraphReader(model,
				myArguments, myAgentFactory)).addComponent(file.getStories());
		model.setStories(stories);

		AbstractObservables observables = (new ObservablesParagraphReader(
				model, myArguments, myAgentFactory)).addComponent(file
				.getObservables());
		model.setObservables(observables);

		List<AbstractPerturbation> perturbations = (new PerturbationsParagraphReader(
				model, myArguments, myAgentFactory)).addComponent(file
				.getPerturbations());
		model.setPerturbations(perturbations);

		return model;
	}
}
