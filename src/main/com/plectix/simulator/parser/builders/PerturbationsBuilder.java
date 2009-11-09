package com.plectix.simulator.parser.builders;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelPerturbation;
import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpressionMonome;
import com.plectix.simulator.parser.abstractmodel.perturbations.ModelLinearExpression;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ModelSpeciesCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ModelTimeCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModelRateModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModificationType;
import com.plectix.simulator.simulationclasses.perturbations.AddOnceModification;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulationclasses.perturbations.DeleteOnceModification;
import com.plectix.simulator.simulationclasses.perturbations.PerturbationRule;
import com.plectix.simulator.simulationclasses.perturbations.RateModification;
import com.plectix.simulator.simulationclasses.perturbations.SpeciesCondition;
import com.plectix.simulator.simulationclasses.perturbations.TimeCondition;
import com.plectix.simulator.simulationclasses.perturbations.util.LinearExpression;
import com.plectix.simulator.simulationclasses.perturbations.util.VectorObservable;
import com.plectix.simulator.simulationclasses.perturbations.util.VectorRule;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.util.SpeciesManager;

public final class PerturbationsBuilder {
	private final SubstanceBuilder substanceBuilder;
	private final SimulationArguments simulationArguments;
	private final KappaSystem kappaSystem;

	public PerturbationsBuilder(SimulationData simulationData) {
		this.kappaSystem = simulationData.getKappaSystem();
		this.simulationArguments = simulationData.getSimulationArguments();
		this.substanceBuilder = new SubstanceBuilder(kappaSystem);
	}

	public final List<ComplexPerturbation<?,?>> build(List<ModelPerturbation> perturbations, 
			MasterSolutionModel masterSolutionModel)
			throws ParseErrorException, DocumentFormatException {
		List<ComplexPerturbation<?,?>> result = new ArrayList<ComplexPerturbation<?,?>>();
		for (ModelPerturbation modelPerturbation : perturbations) {
			ComplexPerturbation<?, ?> realPerturbation = convert(modelPerturbation);
			if (masterSolutionModel != null)
				masterSolutionModel.checkCorrect(realPerturbation, modelPerturbation);
			if (realPerturbation != null) {
				result.add(realPerturbation);
			}
		}
		return result;
	}

	private final Rule findRule(String ruleName) throws DocumentFormatException {
		if (ruleName == null) {
			// TODO throw document format exception
			return null;
		}

		for (Rule rule : kappaSystem.getRules()) {
			if (ruleName.equals(rule.getName())) {
				return rule;
			}
		}
		return null;
	}

//	private final List<MonomeInterface> createRateExpressionOld (
//			ModelLinearExpression expression) throws DocumentFormatException {
//		List<MonomeInterface> result = new ArrayList<MonomeInterface>();
//		for (LinearExpressionMonome monome : expression.getPolynome()) {
//			Rule foundedRule = findRule(monome.getEntityName());
//			double multiplier = monome.getMultiplier();
//			result.add(new RateExpressionMonome(foundedRule, multiplier));
//		}
//		return result;
//	}
	
	private final LinearExpression<VectorRule> createRateExpression (
			ModelLinearExpression expression) throws DocumentFormatException {
		LinearExpression<VectorRule> result = new LinearExpression<VectorRule>();
		for (LinearExpressionMonome monome : expression.getPolynome()) {
			Rule foundRule = findRule(monome.getEntityName());
			double multiplier = monome.getMultiplier();
			if (foundRule != null) {
				result.addMonome(new VectorRule(foundRule), multiplier);	
			} else {
				result.addMonome(multiplier);
			}
		}
		return result;
	}
	
	private final LinearExpression<VectorObservable> createSpeciesExpression (
			ModelLinearExpression expression) throws DocumentFormatException {
		LinearExpression<VectorObservable> result = new LinearExpression<VectorObservable>();
		for (LinearExpressionMonome monome : expression.getPolynome()) {
			ObservableInterface observable = this.checkObservableForExistance(monome.getEntityName());
			double coeffient = monome.getMultiplier();
			if (observable != null) {
				result.addMonome(new VectorObservable(observable, kappaSystem.getObservables()), coeffient);	
			} else {
				result.addMonome(coeffient);
			}
		}
		return result;
	}
	
	private RateModification convert(ModelRateModification modelModification) throws DocumentFormatException {
		LinearExpression<VectorRule> expression = createRateExpression(modelModification.getExpression());
		Rule rule = findRule(modelModification.getArgument());
		return new RateModification(rule, expression);
	}
	
	private final ComplexPerturbation<?, ?> convert(ModelPerturbation abstractPerturbation)
			throws ParseErrorException, DocumentFormatException {
		ModificationType modificationType = abstractPerturbation.getModification().getType();
		ComplexPerturbation<?, ?> result = null;
		boolean rateModification = (modificationType == ModificationType.RATE);
		boolean addOnceModification = (modificationType == ModificationType.ADDONCE);

		// TODO worry about type conversion?
		switch (abstractPerturbation.getCondition().getType()) {
		case TIME: {
			// TODO type cast
			ModelTimeCondition modelCondition = (ModelTimeCondition) abstractPerturbation.getCondition();
			TimeCondition condition = new TimeCondition(modelCondition.getBounds());
			
			if (rateModification) {
				
				// TODO type cast
				ModelRateModification modelModification 
						= (ModelRateModification) abstractPerturbation.getModification();
				RateModification modification = this.convert(modelModification);
				
				result = new ComplexPerturbation<TimeCondition, RateModification>(condition, modification);
			} else {
				
				// TODO type cast
				AbstractOnceModification modelModification = (AbstractOnceModification) abstractPerturbation
						.getModification();
				List<Agent> agentList = substanceBuilder
						.buildAgents(modelModification.getSubstanceAgents());
				List<ConnectedComponentInterface> ccList = SpeciesManager
						.formConnectedComponents(agentList);
				
				for (ConnectedComponentInterface cc : ccList) {
					List<ConnectedComponentInterface> ccL = new ArrayList<ConnectedComponentInterface>();
					ccL.add(cc);
					PerturbationRule rule;
					int quantity = modelModification.getQuantity();
					if (addOnceModification) {
						rule = new PerturbationRule(null, ccL, "", 0,
								(int) kappaSystem.generateNextRuleId(), simulationArguments.isStorify());
						
						AddOnceModification modification = new AddOnceModification(rule, quantity);
						result = new ComplexPerturbation<TimeCondition, AddOnceModification>(condition, modification);
					} else {
						rule = new PerturbationRule(ccL, null, "", 0,
								(int) kappaSystem.generateNextRuleId(), simulationArguments.isStorify());
						
						DeleteOnceModification modification = new DeleteOnceModification(rule, quantity);
						result = new ComplexPerturbation<TimeCondition, DeleteOnceModification>(condition, modification);
					}
					
					kappaSystem.addRule(rule);
				}
			}
			return result;
		}
		case SPECIES: {
			Observables observables = kappaSystem.getObservables();
			
			// TODO type cast
			ModelSpeciesCondition modelCondition = (ModelSpeciesCondition) abstractPerturbation
					.getCondition();
			if (rateModification) {
				
				// TODO type cast
				ModelRateModification modelModification = (ModelRateModification) abstractPerturbation
						.getModification();

				ObservableInterface component = checkObservableForExistance(modelCondition
						.getPickedObservableName());
				LinearExpression<VectorObservable> speciesExpression 
						= createSpeciesExpression(modelCondition.getExpression());
				SpeciesCondition condition = new SpeciesCondition(component, speciesExpression, 
						modelCondition.inequalitySign(), observables);
				
				LinearExpression<VectorRule> expression 
						= createRateExpression(modelModification.getExpression());
				Rule rule = findRule(modelModification.getArgument());
				RateModification modification = new RateModification(rule, expression);
				
				result = new ComplexPerturbation<SpeciesCondition, RateModification>(condition, modification);
				
//				result = new Perturbation(obsID, parameters, component
//						.getId(), rule, condition.inequalitySign(), 
//						createRateExpression(modification.getExpression()), 
//						kappaSystem.getObservables());
				return result;
			} else {
				// TODO we've not implemented this feature :-(
			}
		}
		}
		return null;
	}

	private final ObservableInterface checkObservableForExistance(String observableName)
			throws DocumentFormatException {
		if (observableName == null) {
			return null;
		}
		for (ObservableInterface cc : kappaSystem.getObservables().getComponentList()) {
			if ((cc.getName() != null) && (cc.getName().equals(observableName))) {
				return cc;
			}
		}
		throw new DocumentFormatException("'" + observableName + "' must be in observables!");
	}
}
