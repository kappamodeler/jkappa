package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.component.Observables;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.perturbations.Perturbation;
import com.plectix.simulator.component.stories.Stories;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.ModelSolution;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;

public final class KappaSystemBuilder {
	private final SimulationData simulationData;
	
	public KappaSystemBuilder(SimulationData simulationData) {
		this.simulationData = simulationData;
	}
	
	public final void build() throws ParseErrorException, DocumentFormatException {
		KappaModel model = simulationData.getInitialModel();
		SimulationArguments arguments = simulationData.getSimulationArguments();
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		
		// solution
		if (arguments.isSolutionRead()) { 
			ModelSolution solution = model.getSolution();
			kappaSystem.setSolution((new SolutionBuilder(simulationData)).build(solution));
		}

		// rules
		List<Rule> rules = (new RuleBuilder(kappaSystem)).build(model.getRules());
		kappaSystem.setRules(rules);

		if ((kappaSystem.getStories() == null)
				&& (arguments.getSimulationType() == SimulationArguments.SimulationType.STORIFY)) {
			// stories
			kappaSystem.setStories(new Stories(simulationData));
			for (String storifiedName : (new StoriesBuilder()).build(model
					.getStories())) {
				kappaSystem.addStories(storifiedName);
			}
		} else {
			// observables
			Observables observables = (new ObservablesBuilder(simulationData)).build(model
					.getObservables(), rules);
			kappaSystem.setObservables(observables);
		}

		// perturbations
		List<Perturbation> perturbations = 
			(new PerturbationsBuilder(simulationData)).build(model.getPerturbations());
		kappaSystem.setPerturbations(perturbations);
	}
}
