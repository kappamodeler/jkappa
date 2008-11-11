package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IObservablesComponent;

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
	private List<RateExpression> rateParameters;

	public List<RateExpression> getRateParameters() {
		return rateParameters;
	}

	public void setRateParameters(List<RateExpression> rateParameters) {
		this.rateParameters = rateParameters;
	}

	public final boolean isDo() {
		return this.isDO;
	}

	public CPerturbation(int perturbationID, double time, byte type,
			double perturbationRate, CRule rule, boolean greater,
			List<RateExpression> rateParameters) {
		this.perturbationID = perturbationID;
		this.timeCondition = time;
		this.type = type;
		this.perturbationRate = perturbationRate;
		this.rule = rule;
		this.ruleRate = rule.getRuleRate();
		this.greater = greater;
		this.rateParameters = rateParameters;
	}

	public CPerturbation(int perturbationID, List<IObservablesComponent> obsID,
			List<Double> parameters, int obsNameID, byte type,
			double perturbationRate, CRule rule, boolean greater,
			List<RateExpression> rateParameters) {
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
		this.rateParameters = rateParameters;
	}

	private void fillParameters(List<IObservablesComponent> obsID,
			List<Double> parameters, CObservables obs) {
		this.sumParameters = new ArrayList<SumParameters>();
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
			this.sumParameters.add(new SumParameters(null, parameters
					.get(parameters.size() - 1)));
		}
	}

	public boolean checkCondition(double currentTime) {
		if (currentTime > this.timeCondition) {
			fillPerturbationRate();
			this.rule.setRuleRate(this.perturbationRate);
			this.isDO = true;
			return true;
		}
		return false;
	}

	private final void fillPerturbationRate(){
		this.perturbationRate=0; 
		for (RateExpression re : this.rateParameters){
			this.perturbationRate+=re.getMultiplication();
		}
	}
	
	public boolean checkCondition(CObservables observables) {
		double obsSize = observables.getComponentList().get(obsNameID)
				.getSize();

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

	private double calculateSum(CObservables observables) {
		double sum = 0.0;
		for (int i = 0; i < this.sumParameters.size() - 1; i++) {
			sum += this.sumParameters.get(i).getMultiply(observables);
		}
		sum += this.sumParameters.get(this.sumParameters.size() - 1).parameter;
		return sum;
	}
}
