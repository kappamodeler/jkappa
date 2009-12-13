package com.plectix.simulator.parser.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelPerturbation;
import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpressionMonome;
import com.plectix.simulator.parser.abstractmodel.perturbations.ModelLinearExpression;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ModelConjuctionCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ModelSpeciesCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ModelTimeCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.PerturbationCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModelRateModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModificationType;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.PerturbationModification;
import com.plectix.simulator.simulationclasses.perturbations.AbstractModification;
import com.plectix.simulator.simulationclasses.perturbations.AddOnceModification;
import com.plectix.simulator.simulationclasses.perturbations.ComplexCondition;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulationclasses.perturbations.ConditionInterface;
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
	
	private ConditionInterface convert(PerturbationCondition modelCondition) throws DocumentFormatException {
		switch (modelCondition.getType()) {
		case COMPLEX: {
			ModelConjuctionCondition modelConjuctionCondition = (ModelConjuctionCondition) modelCondition;
			Collection<ConditionInterface> conditions = new LinkedHashSet<ConditionInterface>();
			for (PerturbationCondition simpleCondition : modelConjuctionCondition.getConditions()) {
				conditions.add(this.convert(simpleCondition));
			}
			return new ComplexCondition(conditions);
		}
		case TIME: {
			ModelTimeCondition modelTimeCondition = (ModelTimeCondition) modelCondition;
			return new TimeCondition(modelTimeCondition.getBounds());
		}
		case SPECIES : {
			ModelSpeciesCondition modelSpeciesCondition = (ModelSpeciesCondition) modelCondition;
			ObservableInterface component = checkObservableForExistance(modelSpeciesCondition
					.getPickedObservableName());
			LinearExpression<VectorObservable> speciesExpression 
					= createSpeciesExpression(modelSpeciesCondition.getExpression());
			return new SpeciesCondition(component, speciesExpression, 
					modelSpeciesCondition.inequalitySign(), kappaSystem.getObservables());
		}
		}
		return null;
	}
	
	private AbstractModification convert(PerturbationModification modelModification) throws DocumentFormatException {
		switch (modelModification.getType()) {
		case RATE: {
			return this.convert((ModelRateModification)modelModification);
		}
		default: {
			AbstractOnceModification modelOnceModification = (AbstractOnceModification) modelModification;
			List<Agent> agentList = substanceBuilder.buildAgents(modelOnceModification.getSubstanceAgents());
			List<ConnectedComponentInterface> ccList = SpeciesManager.formConnectedComponents(agentList);
	
			for (ConnectedComponentInterface cc : ccList) {
				List<ConnectedComponentInterface> ccL = new ArrayList<ConnectedComponentInterface>();
				ccL.add(cc);
				int quantity = modelOnceModification.getQuantity();
				if (modelOnceModification.getType() == ModificationType.ADDONCE) {
					PerturbationRule rule = new PerturbationRule(null, ccL, "", 0,
							(int) kappaSystem.generateNextRuleId(), simulationArguments.storiesModeIsOn());
					kappaSystem.addRule(rule);
					
					return new AddOnceModification(rule, quantity);
				} else {
					PerturbationRule rule = new PerturbationRule(ccL, null, "", 0,
							(int) kappaSystem.generateNextRuleId(), simulationArguments.storiesModeIsOn());
					kappaSystem.addRule(rule);	
					
					return new DeleteOnceModification(rule, quantity);
				}
			}
		}
		}
		return null;
	}
	
	private final <C extends ConditionInterface, M extends AbstractModification> ComplexPerturbation<?, ?> construct(C condition, M modification) {
		// tricky one, yeah ;)
		return new ComplexPerturbation<C, M>(condition, modification);
	}
		
	private final ComplexPerturbation<?, ?> convert(ModelPerturbation abstractPerturbation)
			throws ParseErrorException, DocumentFormatException {

		ConditionInterface condition = this.convert(abstractPerturbation.getCondition());
		AbstractModification modification = this.convert(abstractPerturbation.getModification());
		return this.construct(condition, modification);
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
