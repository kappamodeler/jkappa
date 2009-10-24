package com.plectix.simulator.parser.builders;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.interfaces.PerturbationExpressionInterface;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelPerturbation;
import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpressionMonome;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.SpeciesCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.TimeCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModelRateModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModificationType;
import com.plectix.simulator.simulationclasses.perturbations.Perturbation;
import com.plectix.simulator.simulationclasses.perturbations.PerturbationRule;
import com.plectix.simulator.simulationclasses.perturbations.RateExpression;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Rule;

public final class PerturbationsBuilder {
	private final SubstanceBuilder substanceBuilder;
	private final SimulationArguments simulationArguments;
	private final KappaSystem kappaSystem;

	public PerturbationsBuilder(SimulationData simulationData) {
		this.kappaSystem = simulationData.getKappaSystem();
		this.simulationArguments = simulationData.getSimulationArguments();
		this.substanceBuilder = new SubstanceBuilder(kappaSystem);
	}

	public final List<Perturbation> build(List<ModelPerturbation> perturbations)
			throws ParseErrorException, DocumentFormatException {
		List<Perturbation> result = new ArrayList<Perturbation>();
		for (ModelPerturbation perturbation : perturbations) {
			List<Perturbation> res = convert(perturbation);
			if (res != null) {
				result.addAll(res);
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

	private final List<PerturbationExpressionInterface> createRateExpression (
			ModelRateModification modification) throws DocumentFormatException {
		List<PerturbationExpressionInterface> result = new ArrayList<PerturbationExpressionInterface>();
		for (LinearExpressionMonome monome : modification.getExpression()
				.getPolynome()) {
			Rule foundedRule = findRule(monome.getObsName());
			double multiplier = monome.getMultiplier();
			result.add(new RateExpression(foundedRule, multiplier));
		}
		return result;
	}

	private final List<Perturbation> convert(ModelPerturbation abstractPerturbation)
			throws ParseErrorException, DocumentFormatException {
		ModificationType modificationType = abstractPerturbation.getModification().getType();
		List<Perturbation> result = new ArrayList<Perturbation>();
		boolean rateModification = (modificationType == ModificationType.RATE);
		boolean addOnceModification = (modificationType == ModificationType.ADDONCE);

		// TODO worry about type conversion?
		switch (abstractPerturbation.getCondition().getType()) {
		case TIME: {
			TimeCondition condition = (TimeCondition) abstractPerturbation.getCondition();
			double timeBound = condition.getBounds();

			if (rateModification) {
				ModelRateModification modification = (ModelRateModification) abstractPerturbation
						.getModification();
				result.add(new Perturbation(timeBound,
						findRule(modification
								.getArgument()),
						createRateExpression(modification)));
				return result;
			} else {
				AbstractOnceModification modification = (AbstractOnceModification) abstractPerturbation
						.getModification();
				List<Agent> agentList = substanceBuilder
						.buildAgents(modification.getSubstanceAgents());
				List<ConnectedComponentInterface> ccList = SimulationUtils
						.buildConnectedComponents(agentList);
				
				for (ConnectedComponentInterface cc : ccList) {
					List<ConnectedComponentInterface> ccL = new ArrayList<ConnectedComponentInterface>();
					ccL.add(cc);
					PerturbationRule rule;
					if (addOnceModification) {
						rule = new PerturbationRule(null, ccL, "", 0,
								(int) kappaSystem.generateNextRuleId(), simulationArguments.isStorify());
					} else {
						rule = new PerturbationRule(ccL, null, "", 0,
								(int) kappaSystem.generateNextRuleId(), simulationArguments.isStorify());
					}
					rule.setCount(modification.getQuantity());
					kappaSystem.addRule(rule);
					result.add(new Perturbation(timeBound,
							rule));
				}
				return result;
			}
		}
		case SPECIES: {
			SpeciesCondition condition = (SpeciesCondition) abstractPerturbation
					.getCondition();
			if (rateModification) {
				ModelRateModification modification = (ModelRateModification) abstractPerturbation
						.getModification();

				// old code "conversion"
				List<ObservableInterface> obsID = new ArrayList<ObservableInterface>();
				List<Double> parameters = new ArrayList<Double>();
				double freeTerm = 0;
				boolean freeTermNotZero = false;
				for (LinearExpressionMonome monome : condition.getExpression()
						.getPolynome()) {
					if (monome.getObsName() != null) {
						obsID.add(checkObservableForExistance(monome.getObsName()));
						parameters.add(monome.getMultiplier());
					} else {
						freeTerm += monome.getMultiplier();
						freeTermNotZero = true;
					}
				}

				if (freeTermNotZero) {
					parameters.add(freeTerm);
				}
				Rule rule = findRule(modification.getArgument());
				ObservableInterface component = checkObservableForExistance(condition
						.getArgument());
				result.add(new Perturbation(obsID, parameters, component
						.getId(), 
						rule, condition.inequalitySign(), createRateExpression(modification), 
						kappaSystem.getObservables()));
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
		for (ObservableInterface cc : kappaSystem.getObservables().getComponentList()) {
			if ((cc.getName() != null) && (cc.getName().equals(observableName))) {
				return cc;
			}
		}
		throw new DocumentFormatException("'" + observableName + "' must be in observables!");
	}
}
