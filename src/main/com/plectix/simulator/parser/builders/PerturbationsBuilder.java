package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CPerturbationType;
import com.plectix.simulator.components.CRulePerturbation;
import com.plectix.simulator.components.RateExpression;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.AbstractPerturbation;
import com.plectix.simulator.parser.abstractmodel.AbstractRule;
import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpressionMonome;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.AbstractCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.AbstractSpeciesCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.AbstractTimeCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractRateModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModificationType;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.Info.InfoType;

public class PerturbationsBuilder {
	private final SubstanceBuilder mySubstanceBuilder;
	private final SimulationData myData;

	public PerturbationsBuilder(SimulationData data) {
		mySubstanceBuilder = new SubstanceBuilder(data);
		myData = data;
	}

	public List<CPerturbation> build(List<AbstractPerturbation> arg)
			throws ParseErrorException {
		List<CPerturbation> result = new ArrayList<CPerturbation>();
		for (AbstractPerturbation perturbation : arg) {
			List<CPerturbation> res = convert(perturbation);
			if (res != null) {
				result.addAll(res);
			}
		}
		return result;
	}

	// TODO throw document format exception
	private IRule findRule(String name) {
		if (name == null) {
			return null;
		}

		for (IRule rule : myData.getRules()) {
			if (name.equals(rule.getName())) {
				return rule;
			}
		}
		return null;
	}

	private List<IPerturbationExpression> createRateExpression(
			AbstractRateModification modification) {
		List<IPerturbationExpression> result = new ArrayList<IPerturbationExpression>();
		for (LinearExpressionMonome monome : modification.getExpression()
				.getPolynome()) {
			IRule foundedRule = findRule(monome.getObsName());
			double multiplier = monome.getMultiplier();
			result.add(new RateExpression(foundedRule, multiplier));
		}
		return result;
	}

	private List<CPerturbation> convert(AbstractPerturbation arg)
			throws ParseErrorException {
		ModificationType modificationType = arg.getModification().getType();
		List<CPerturbation> result = new ArrayList<CPerturbation>();
		boolean rateModification = (modificationType == ModificationType.RATE);
		boolean deleteOnceModification = (modificationType == ModificationType.DELETEONCE);
		boolean addOnceModification = (modificationType == ModificationType.ADDONCE);

		int id = arg.getId();
		// TODO worry about type conversion?
		switch (arg.getCondition().getType()) {
		case TIME: {
			AbstractTimeCondition condition = (AbstractTimeCondition) arg
					.getCondition();
			double timeBound = condition.getBounds();

			if (rateModification) {
				AbstractRateModification modification = (AbstractRateModification) arg
						.getModification();
				result.add(new CPerturbation(id, timeBound,
						// TODO -1, we don't need it
						CPerturbationType.TIME, -1, findRule(modification
								.getArgument()), true,
						createRateExpression(modification)));
				return result;
			} else {
				AbstractOnceModification modification = (AbstractOnceModification) arg
						.getModification();
				List<IAgent> agentList = mySubstanceBuilder
						.buildAgents(modification.getSubstance());
				List<IConnectedComponent> ccList = SimulationUtils
						.buildConnectedComponents(agentList);
				
				for (IConnectedComponent cc : ccList) {
					List<IConnectedComponent> ccL = new ArrayList<IConnectedComponent>();
					ccL.add(cc);
					CRulePerturbation rule;
					if (addOnceModification) {
						//TODO
//						if (countToFile == Double.MAX_VALUE)
//							throw new ParseErrorException(perturbationStr,
//									"$ADDONCE has not used with $INF");
						rule = new CRulePerturbation(null, ccL, "", 0,
								(int) myData.generateNextRuleId(), myData
										.getSimulationArguments().isStorify());
					} else {
						rule = new CRulePerturbation(ccL, null, "", 0,
								(int) myData.generateNextRuleId(), myData
										.getSimulationArguments().isStorify());
					}
					rule.setCount(modification.getQuantity());
					myData.addRule(rule);
					result.add(new CPerturbation(id, timeBound, CPerturbationType.ONCE,
							rule, true));
				}
				return result;
			}
		}
		case SPECIES: {
			AbstractSpeciesCondition condition = (AbstractSpeciesCondition) arg
					.getCondition();
			if (rateModification) {
				AbstractRateModification modification = (AbstractRateModification) arg
						.getModification();

				// old code "conversion"
				List<IObservablesComponent> obsID = new ArrayList<IObservablesComponent>();
				List<Double> parameters = new ArrayList<Double>();
				double freeTerm = 0;
				boolean freeTermNotZero = false;
				for (LinearExpressionMonome monome : condition.getExpression()
						.getPolynome()) {
					if (monome.getObsName() != null) {
						obsID.add(checkInObservables(monome.getObsName()));
						parameters.add(monome.getMultiplier());
					} else {
						freeTerm += monome.getMultiplier();
						freeTermNotZero = true;
					}
				}

				if (freeTermNotZero) {
					parameters.add(freeTerm);
				}
				IRule rule = findRule(modification.getArgument());
				IObservablesComponent component = checkInObservables(condition
						.getArgument());
				result.add(new CPerturbation(id, obsID, parameters, component
						.getNameID(), CPerturbationType.NUMBER, -1., 
						rule, condition.isGreater(), createRateExpression(modification), 
						myData.getObservables()));
				return result;
			} else {
				myData.addInfo(InfoType.OUTPUT, InfoType.WARNING, 
						"WARNING - We cannot use species condition with 'ONCE' modification");
//				throw new ParseErrorException("We cannot use species condition with 'ONCE' modification");
				// TODO we've not implemented this feature :-(
			}
		}
		}
		return null;
	}

	private final IObservablesComponent checkInObservables(String obsName)
			throws ParseErrorException {
		IObservablesComponent obsId = null;
		for (IObservablesComponent cc : myData.getObservables()
				.getComponentList()) {
			if ((cc.getName() != null) && (cc.getName().equals(obsName))) {
				return cc;
			}
		}
		throw new ParseErrorException("'" + obsName
				+ "' must be in observables!");
	}
}
