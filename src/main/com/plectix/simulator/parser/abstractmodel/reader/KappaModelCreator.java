package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.*;

import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.*;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;

public class KappaModelCreator {

	private final SimulationArguments myArguments;
	
	public KappaModelCreator(SimulationArguments arguments) {
		myArguments = arguments;
	}
	
	//TODO stateg?
	public KappaModel createModel(KappaFile file) throws ParseErrorException {
		KappaModel model = new KappaModel();
		
//		simulationData.addInfo(InfoType.INFO,"--Computing initial state");
		
		if (myArguments.getSimulationType() != SimulationType.GENERATE_MAP) {
			AbstractSolution solution = (new SolutionParagraphReader(model, myArguments)).
						addComponent(file.getSolution());
			model.setSolution(solution);
		}
		
		Collection<AbstractRule> rules = (new RulesParagraphReader(model, myArguments)).
						addComponent(file.getRules());
		model.setRules(rules);
		
		if ((model.getStories() == null)
				&& (myArguments.getSimulationType() == SimulationType.STORIFY)) {
			AbstractStories stories = (new StoriesParagraphReader(model, myArguments)).
						addComponent(file.getStories()); 
			model.setStories(stories);
		} else {
			AbstractObservables observables = (new ObservablesParagraphReader(model, myArguments)).
						addComponent(file.getObservables());
			model.setObservables(observables);
		}
		List<AbstractPerturbation> perturbations = 
			(new PerturbationsParagraphReader(model, myArguments)).addComponent(file.getPerturbations());
		model.setPerturbations(perturbations);
		
		return model;
	}
}
