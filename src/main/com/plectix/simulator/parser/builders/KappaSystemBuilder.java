package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.parser.abstractmodel.AbstractRule;
import com.plectix.simulator.parser.abstractmodel.AbstractSolution;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;

public class KappaSystemBuilder {
	// public KappaSystem build(KappaModel model) {
	// KappaSystem system = new KappaSystem();
	//		
	// system
	//		
	//		
	// return system;
	// }

	public void build(KappaModel model, SimulationData data) {
		SimulationArguments arguments = data.getSimulationArguments();
		IdGenerator generator = data.getIdGenerator();
		
		// solution
		if (arguments.getSimulationType() != SimulationArguments.SimulationType.GENERATE_MAP) { 
			AbstractSolution solution = model.getSolution();
			data.setSolution((new SolutionBuilder(data)).build(solution));
		}

//		System.out.println("GENERATOR - " + generator.generateNextAgentId());
		// rules
		List<IRule> rules = new ArrayList<IRule>();
		for (AbstractRule abstractRule : model.getRules()) {
			IRule rule = (new RuleBuilder(data)).build(abstractRule);
			rules.add(rule);
		}
		data.setRules(rules);

//		data.outputData();
		
		if ((data.getStories() == null)
				&& (arguments.getSimulationType() == SimulationArguments.SimulationType.STORIFY)) {
			// stories
			data.setStories(new CStories(data));
			for (String storifiedName : (new StoriesBuilder()).build(model
					.getStories())) {
				data.addStories(storifiedName);
			}
		} else {
			// observables
			IObservables observables = (new ObservablesBuilder(data, arguments)).build(model
					.getObservables(), rules);
			data.setObservables(observables);
		}

		data.setIdGenerator(generator);
		
//		data.setPerturbations(new ArrayList<CPerturbation>());
	}
}
