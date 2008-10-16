package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;

public class CPerturbation {

	public final static byte TYPE_TIME = 0;
	public final static byte TYPE_NUMBER = 1;

	private byte type;

	public byte getType() {
		return type;
	}

	private double timeCondition;
	private double numberCondition;
	private List<SumParameters> sumParameters;
	private double perturbationRate;
	private double ruleRate;
	private int ruleID;
	private boolean greater = true;

	private class SumParameters {
		private int observableID;
		private double parameter;

		public SumParameters(int observableID, double parameter) {
			this.observableID = observableID;
			this.parameter = parameter;
		}

		public double getMultiply(CObservables observables) {
			return observables.getConnectedComponentList().get(
					this.observableID).getInjectionsList().size()
					* this.parameter;
		}

		@Override
		public final boolean equals(Object obj) {
			if (!(obj instanceof SumParameters))
				return false;
			SumParameters sp = (SumParameters) obj;
			if (observableID != sp.observableID)
				return false;
			return true;
		}
	}

	public CPerturbation(double time, List<Integer> obsID,
			List<Double> parameters, double number, byte type, double ruleRate,
			double perturbationRate, int roolID, boolean greater,
			CObservables obs) {
		this.timeCondition = time;
		this.numberCondition = number;
		fillParameters(obsID, parameters, obs);
		this.type = type;
		this.perturbationRate = perturbationRate;
		this.ruleRate = ruleRate;
		this.ruleID = roolID;
		this.greater = greater;
	}

	private void fillParameters(List<Integer> obsID, List<Double> parameters,
			CObservables obs) {
		this.sumParameters = new ArrayList<SumParameters>();
		for (int i = 0; i < obsID.size(); i++) {
			int mainAmNumber = obs.getConnectedComponentList()
					.get(obsID.get(i)).getMainAutomorphismNumber();

			if (mainAmNumber == ObservablesConnectedComponent.NO_INDEX)
				this.sumParameters.add(new SumParameters(obsID.get(i),
						parameters.get(i)));
			else {
				SumParameters sp = new SumParameters(mainAmNumber, parameters
						.get(i));
				int index = this.sumParameters.indexOf(sp);
				if (index == -1)
					this.sumParameters.add(sp);
				else {
					this.sumParameters.get(index).parameter += sp.parameter;
				}
			}
		}
	}

	public boolean checkCondition(double currentTime, List<CRule> rules) {
		if (currentTime > this.timeCondition) {
			rules.get(this.ruleID).setRuleRate(this.perturbationRate);
			return true;
		}
		return false;
	}

	public boolean checkCondition(CObservables observables, List<CRule> rules) {
		if ((greater && (calculateSum(observables) > this.numberCondition))
				|| (!(greater) && (calculateSum(observables) < this.numberCondition))) {
			rules.get(this.ruleID).setRuleRate(this.perturbationRate);
			return true;
		}
		if ((greater && (calculateSum(observables) < this.numberCondition))
				|| (!(greater) && (calculateSum(observables) > this.numberCondition))) {
			rules.get(this.ruleID).setRuleRate(this.ruleRate);
			return true;
		}
		return false;
	}

	private double calculateSum(CObservables observables) {
		double sum = 0.0;
		for (int i = 0; i < this.sumParameters.size() - 1; i++) {
			sum += this.sumParameters.get(i).getMultiply(observables);
		}
		sum += this.sumParameters.get(this.sumParameters.size() - 1).parameter;
		return sum;
	}
}
