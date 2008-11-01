package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;

public class CPerturbation {

	public final static byte TYPE_TIME = 0;
	public final static byte TYPE_NUMBER = 1;

	private byte type;

	public byte getType() {
		return type;
	}

	private double timeCondition;
	private int obsNameID;
	private List<SumParameters> sumParameters;
	private double perturbationRate;
	private double ruleRate;
	private CRule rule;
	private int perturbationID;
	private boolean greater = true;
	private boolean isDO = false;

	public final boolean isDo() {
		return this.isDO;
	}

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

	public CPerturbation(int perturbationID, double time, byte type,
			double perturbationRate, CRule rule, boolean greater) {
		switch (type) {
		case TYPE_TIME: {
			this.perturbationID = perturbationID;
			this.timeCondition = time;
			this.type = type;
			this.perturbationRate = perturbationRate;
			this.rule = rule;
			this.ruleRate = rule.getRuleRate();
			this.greater = greater;
		}
		}
	}

	public CPerturbation(int perturbationID, List<Integer> obsID,
			List<Double> parameters, int obsNameID, byte type,
			double perturbationRate, CRule rule, boolean greater) {
		switch (type) {
		case TYPE_NUMBER: {
			this.perturbationID = perturbationID;
			this.obsNameID = obsNameID;
			CObservables obs = SimulationMain.getSimulationManager()
					.getSimulationData().getObservables();
			fillParameters(obsID, parameters, obs);

			this.type = type;
			this.perturbationRate = perturbationRate;
			this.rule = rule;
			this.ruleRate = rule.getRuleRate();
			this.greater = greater;
		}
		}

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

		if (obsID.size() != parameters.size()) {
			this.sumParameters.add(new SumParameters(-1, parameters
					.get(parameters.size() - 1)));
		}
	}

	public boolean checkCondition(double currentTime) {
		if (currentTime > this.timeCondition) {
			this.rule.setRuleRate(this.perturbationRate);
			this.isDO = true;
			return true;
		}
		return false;
	}

	public boolean checkCondition(CObservables observables) {
		int obsSize = observables.getConnectedComponentList().get(
				this.obsNameID).getInjectionsList().size();

		if ((greater && (obsSize > calculateSum(observables)))
				|| (!(greater) && (obsSize < calculateSum(observables)))) {
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

	private double calculateSum(CObservables observables) {
		double sum = 0.0;
		for (int i = 0; i < this.sumParameters.size() - 1; i++) {
			sum += this.sumParameters.get(i).getMultiply(observables);
		}
		sum += this.sumParameters.get(this.sumParameters.size() - 1).parameter;
		return sum;
	}
}
