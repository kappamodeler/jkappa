package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.ModelSolution;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.observables.Observables;
import com.plectix.simulator.staticanalysis.stories.Stories;

public final class KappaSystemBuilder {
	private final SimulationData simulationData;
	
	public KappaSystemBuilder(SimulationData simulationData) {
		this.simulationData = simulationData;
	}
	
	public final KappaSystem build(KappaModel model) throws ParseErrorException, DocumentFormatException {
		SimulationArguments arguments = simulationData.getSimulationArguments();
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		
		MasterSolutionModel masterSolutionModel = null;
		if(!simulationData.getSimulationArguments().incompletesAllowed())
			masterSolutionModel= new MasterSolutionModel();

		// rules
		List<Rule> rules = (new RuleBuilder(kappaSystem)).build(model.getRules(), masterSolutionModel);
		kappaSystem.setRules(rules);

		if ((kappaSystem.getStories() == null) 
				&& (arguments.getSimulationType() == SimulationArguments.SimulationType.STORIFY)) {
			// stories
			kappaSystem.setStories(new Stories(simulationData));
			for (String storifiedName : (new StoriesBuilder()).build(model.getStories())) {
				kappaSystem.addStories(storifiedName);
			}
		} else {
			// observables
			Observables observables = (new ObservablesBuilder(simulationData)).build(model
					.getObservables(), rules);
			kappaSystem.setObservables(observables);
		}

		// perturbations
		List<ComplexPerturbation<?,?>> perturbations = 
			(new PerturbationsBuilder(simulationData)).build(model.getPerturbations(), masterSolutionModel);
		kappaSystem.setPerturbations(perturbations);

		// solution
		if (arguments.solutionNeedsToBeRead()) { 
			ModelSolution solution = model.getSolution();
			kappaSystem.setSolution((new SolutionBuilder(simulationData)).build(solution, masterSolutionModel));
		}
		return kappaSystem;
	}
}
