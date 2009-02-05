package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.*;

public final class CPerturbation implements Serializable{

	public final static byte TYPE_TIME = 0;
	public final static byte TYPE_NUMBER = 1;
	public final static byte TYPE_ONCE = 2;

	private CPerturbationType type;

	public CPerturbationType getType() {
		return type;
	}

	private double timeCondition;
	private int obsNameID;
	private List<IPerturbationExpression> parametersLHS;
	private double perturbationRate;
	private double ruleRate;
	private IRule rule;
	private int perturbationID;
	private boolean greater = true;
	private boolean isDO = false;
	private List<IPerturbationExpression> parametersRHS;

	// time rate
	public CPerturbation(int perturbationID, double time, CPerturbationType type,
			double perturbationRate, IRule rule, boolean greater,
			List<IPerturbationExpression> rateParameters) {
		this.perturbationID = perturbationID;
		this.timeCondition = time;
		this.type = type;
		this.perturbationRate = perturbationRate;
		this.rule = rule;
		this.ruleRate = rule.getRuleRate();
		this.greater = greater;
		this.parametersRHS = rateParameters;
	}

	// time once
	// TODO remove greater
	public CPerturbation(int perturbationID, double time, CPerturbationType type,
			CRulePerturbation rule, boolean greater) {
		this.perturbationID = perturbationID;
		this.timeCondition = time;
		this.type = type;
		this.rule = rule;
		this.ruleRate = rule.getRuleRate();
		this.greater = greater;
	}

	// species rate
	public CPerturbation(int perturbationID, List<IObservablesComponent> obsID,
			List<Double> parameters, int obsNameID, CPerturbationType type,
			double perturbationRate, IRule rule, boolean greater,
			List<IPerturbationExpression> rateParameters, IObservables observables) {
		this.perturbationID = perturbationID;
		this.obsNameID = obsNameID;
		IObservables obs = observables;
		fillParameters(obsID, parameters, obs);
		this.type = type;
		this.perturbationRate = perturbationRate;
		this.rule = rule;
		this.ruleRate = rule.getRuleRate();
		this.greater = greater;
		this.parametersRHS = rateParameters;
	}
	
	public final int getObsNameID() {
		return this.obsNameID;
	}

	public final List<IPerturbationExpression> getLHSParametersList() {
		return this.parametersLHS;
	}

	public final IRule getPerturbationRule() {
		return this.rule;
	}

	public final double getPerturbationRate() {
		return this.perturbationRate;
	}

	public final boolean getGreater() {
		return this.greater;
	}

	public final double getTimeCondition() {
		return this.timeCondition;
	}

	public final List<IPerturbationExpression> getRHSParametersList() {
		return Collections.unmodifiableList(parametersRHS);
	}

	// public void setRHSParameters(List<RateExpression> rateParameters) {
	// this.RHSParameters = rateParameters;
	// }

	public final boolean isDo() {
		return this.isDO;
	}

	public final void checkConditionOnce(double currentTime) {
		if (currentTime > this.timeCondition) {
			rule.setInfinityRate(true);
			isDO = true;
			rule.setRuleRate(1.0);
		}
	}

	private final void fillParameters(List<IObservablesComponent> obsID,
			List<Double> parameters, IObservables obs) {
		this.parametersLHS = new ArrayList<IPerturbationExpression>();
		for (int i = 0; i < obsID.size(); i++) {

			IObservablesComponent mainAmNumber = null;
			if (obsID.get(i) instanceof ObservablesConnectedComponent) {
				int index = ((ObservablesConnectedComponent) obsID.get(i))
						.getMainAutomorphismNumber();
				if (index != ObservablesConnectedComponent.NO_INDEX)
					mainAmNumber = obs.getComponentList().get(
							((ObservablesConnectedComponent) obsID.get(i))
									.getMainAutomorphismNumber());
			}

			if (mainAmNumber == null)
				this.parametersLHS.add(new SumParameters(obsID.get(i),
						parameters.get(i)));
			else {
				SumParameters sp = new SumParameters(mainAmNumber, parameters
						.get(i));
				int index = this.parametersLHS.indexOf(sp);
				if (index == -1)
					this.parametersLHS.add(sp);
				else {

					this.parametersLHS.get(index).setValue(
							parametersLHS.get(index).getValue() + sp.getValue());
					// this.LHSParameters.get(index).parameter += sp.parameter;
				}
			}
		}

		if (obsID.size() != parameters.size()) {
			this.parametersLHS.add(new SumParameters(null, parameters
					.get(parameters.size() - 1)));
		}
	}

	public final boolean checkCondition(double currentTime) {
		if (currentTime > this.timeCondition) {
			fillPerturbationRate();
			this.rule.setRuleRate(this.perturbationRate);
			this.isDO = true;
			return true;
		}
		return false;
	}

	private final void fillPerturbationRate() {
		this.perturbationRate = 0;
		for (IPerturbationExpression re : this.parametersRHS) {
			this.perturbationRate += re.getMultiplication(null);
		}
	}

	public final boolean checkCondition(IObservables observables) {
		double obsSize = observables.getComponentList().get(obsNameID)
				.getSize(observables);

		if ((greater && (obsSize > calculateSum(observables)))
				|| (!(greater) && (obsSize < calculateSum(observables)))) {
			fillPerturbationRate();
			this.rule.setRuleRate(this.perturbationRate);
			return true;
		}
		if (greater) {
			if (obsSize > calculateSum(observables)) {
				this.rule.setRuleRate(this.ruleRate);
				return true;
			}
		} else {
			if (obsSize < calculateSum(observables)) {
				this.rule.setRuleRate(this.ruleRate);
				return true;
			}
		}
		return false;
	}

	private final double calculateSum(IObservables observables) {
		double sum = 0.0;
		for (int i = 0; i < this.parametersLHS.size() - 1; i++) {
			sum += this.parametersLHS.get(i).getMultiplication(observables);
			// sum += this.sumParameters.get(i).getMultiply(observables);
		}
		sum += this.parametersLHS.get(this.parametersLHS.size() - 1).getValue();
		// sum += this.LHSParameters.get(this.LHSParameters.size() -
		// 1).parameter;
		return sum;
	}
}
