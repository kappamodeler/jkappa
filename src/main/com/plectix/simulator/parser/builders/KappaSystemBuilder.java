package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.parser.abstractmodel.AbstractSolution;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;

public class KappaSystemBuilder {

	private final SimulationData myData;
	
	public KappaSystemBuilder(SimulationData data) {
		myData = data;
	}
	
	public void build() throws ParseErrorException, DocumentFormatException {
		KappaModel model = myData.getInitialModel();
		SimulationArguments arguments = myData.getSimulationArguments();
		KappaSystem kappaSystem = myData.getKappaSystem();
		
		// solution
		if (arguments.isSolutionRead()) { 
			AbstractSolution solution = model.getSolution();
			kappaSystem.setSolution((new SolutionBuilder(myData)).build(solution));
		}

		// rules
		List<IRule> rules = (new RuleBuilder(kappaSystem)).build(model.getRules());
		kappaSystem.setRules(rules);

		if ((kappaSystem.getStories() == null)
				&& (arguments.getSimulationType() == SimulationArguments.SimulationType.STORIFY)) {
			// stories
			kappaSystem.setStories(new CStories(myData));
			for (String storifiedName : (new StoriesBuilder()).build(model
					.getStories())) {
				kappaSystem.addStories(storifiedName);
			}
		} else {
			// observables
			IObservables observables = (new ObservablesBuilder(myData)).build(model
					.getObservables(), rules);
			kappaSystem.setObservables(observables);
		}

		// perturbations
		List<CPerturbation> perturbations = 
			(new PerturbationsBuilder(myData)).build(model.getPerturbations());
		kappaSystem.setPerturbations(perturbations);
	}
}
