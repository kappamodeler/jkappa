package com.plectix.simulator.parser.builders;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.perturbations.CPerturbationType;
import com.plectix.simulator.components.perturbations.CRulePerturbation;
import com.plectix.simulator.components.perturbations.RateExpression;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.parser.abstractmodel.AbstractPerturbation;
import com.plectix.simulator.parser.abstractmodel.perturbations.*;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.*;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.*;
import com.plectix.simulator.parser.exceptions.*;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.*;

public class PerturbationsBuilder {
	private final SubstanceBuilder mySubstanceBuilder;
	private final SimulationArguments myArguments;
	private final KappaSystem myData;

	public PerturbationsBuilder(SimulationData data) {
		myData = data.getKappaSystem();
		myArguments = data.getSimulationArguments();
		mySubstanceBuilder = new SubstanceBuilder(myData);
	}

	public List<CPerturbation> build(List<AbstractPerturbation> arg)
			throws ParseErrorException, DocumentFormatException {
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
	private CRule findRule(String name) {
		if (name == null) {
			return null;
		}

		for (CRule rule : myData.getRules()) {
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
			CRule foundedRule = findRule(monome.getObsName());
			double multiplier = monome.getMultiplier();
			result.add(new RateExpression(foundedRule, multiplier));
		}
		return result;
	}

	private List<CPerturbation> convert(AbstractPerturbation arg)
			throws ParseErrorException, DocumentFormatException {
		ModificationType modificationType = arg.getModification().getType();
		List<CPerturbation> result = new ArrayList<CPerturbation>();
		boolean rateModification = (modificationType == ModificationType.RATE);
//		boolean deleteOnceModification = (modificationType == ModificationType.DELETEONCE);
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
						findRule(modification
								.getArgument()),
						createRateExpression(modification)));
				return result;
			} else {
				AbstractOnceModification modification = (AbstractOnceModification) arg
						.getModification();
				List<CAgent> agentList = mySubstanceBuilder
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
								(int) myData.generateNextRuleId(), myArguments.isStorify());
					} else {
						rule = new CRulePerturbation(ccL, null, "", 0,
								(int) myData.generateNextRuleId(), myArguments.isStorify());
					}
					rule.setCount(modification.getQuantity());
					myData.addRule(rule);
					result.add(new CPerturbation(id, timeBound,
							rule));
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
				CRule rule = findRule(modification.getArgument());
				IObservablesComponent component = checkInObservables(condition
						.getArgument());
				result.add(new CPerturbation(id, obsID, parameters, component
						.getId(), 
						rule, condition.isGreater(), createRateExpression(modification), 
						myData.getObservables()));
				return result;
			} else {
//				myData.addInfo(InfoType.OUTPUT, InfoType.WARNING, 
//						"WARNING - We cannot use species condition with 'ONCE' modification");
//				throw new ParseErrorException("We cannot use species condition with 'ONCE' modification");
				// TODO we've not implemented this feature :-(
			}
		}
		}
		return null;
	}

	private final IObservablesComponent checkInObservables(String obsName)
			throws DocumentFormatException {
		for (IObservablesComponent cc : myData.getObservables()
				.getComponentList()) {
			if ((cc.getName() != null) && (cc.getName().equals(obsName))) {
				return cc;
			}
		}
		throw new DocumentFormatException("'" + obsName
				+ "' must be in observables!");
	}
}
