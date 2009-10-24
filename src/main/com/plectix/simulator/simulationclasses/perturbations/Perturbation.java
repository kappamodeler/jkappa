package com.plectix.simulator.simulationclasses.perturbations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.interfaces.PerturbationExpressionInterface;
import com.plectix.simulator.staticanalysis.ObservableConnectedComponent;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.util.InequalitySign;

/**
 * This class implements Perturbation entity.
 * @author avokhmin
 * @see PerturbationType
 */
@SuppressWarnings("serial")
public final class Perturbation implements Serializable{

	private final PerturbationType type;
	private final double timeCondition;
	private final int observableId;
	private double perturbationRate;
	private final double ruleRate;
	private final Rule rule;
	private final InequalitySign inequalitySign;
	// TODO get rid of this
	private boolean isDO = false;
	private List<PerturbationExpressionInterface> parametersLHS;
	private List<PerturbationExpressionInterface> parametersRHS;

	/**
	 * Constructor of perturbation for "TIME condition".
	 * @param perturbationID given unique id
	 * @param time given time condition
	 * @param rule given rule for modify
	 * @param rateParameters given rate expression
	 * @see PerturbationType
	 */
	public Perturbation(double time, Rule rule,
			List<PerturbationExpressionInterface> rateParameters) {
		this.timeCondition = time;
		this.type = PerturbationType.TIME;
		this.perturbationRate = -1;
		this.rule = rule;
		this.ruleRate = rule.getRate();
		this.inequalitySign = InequalitySign.GREATER;
		this.parametersRHS = rateParameters;
		this.observableId = -1;
	}

	/**
	 * Constructor of perturbation for "Once modification".
	 * @param perturbationID given unique id
	 * @param time given time condition
	 * @param rule given specially rule
	 * @see PerturbationType
	 */
	public Perturbation(double time, PerturbationRule rule) {
		this.timeCondition = time;
		this.type = PerturbationType.ONCE;
		this.rule = rule;
		this.ruleRate = rule.getRate();
		this.inequalitySign = InequalitySign.GREATER;
		this.observableId = -1;
	}

	/**
	 * Constructor of perturbation for "NUMBER modification".
	 * @param perturbationID given unique id
	 * @param obsComponents given list of observable components for checks to apply.
	 * @param parameters given list of correction factor for <b>obsComponents</b>
	 * @param observableId given id of watch observable.
	 * @param rule given rule for modify
	 * @param inequalitySign sign of inequality used in this perturbation
	 * @param rateParameters given list of rate parameters for right handSide.
	 * @param observables observables storage. 
	 */
	public Perturbation(List<ObservableInterface> obsComponents,
			List<Double> parameters, int observableId,
			Rule rule, InequalitySign inequalitySign,
			List<PerturbationExpressionInterface> rateParameters, Observables observables) {
		this.observableId = observableId;
		Observables obs = observables;
		fillParameters(obsComponents, parameters, obs);
		this.type = PerturbationType.NUMBER;
		this.perturbationRate = -1.;
		this.rule = rule;
		this.ruleRate = rule.getRate();
		this.inequalitySign = inequalitySign;
		this.parametersRHS = rateParameters;
		this.timeCondition = Double.NaN;
	}
	
	public final PerturbationType getType() {
		return type;
	}
	
	/**
	 * This method returns unique id of this.
	 * @return unique id of this.
	 */
	public final int getObservableName() {
		return this.observableId;
	}

	/**
	 * This method returns perturbation expressions from left handSide.
	 * @return perturbation expressions from left handSide.
	 */
	public final List<PerturbationExpressionInterface> getLHSParametersList() {
		return this.parametersLHS;
	}

	/**
	 * This method returns rule for modify
	 * @return rule for modify
	 */
	public final Rule getPerturbationRule() {
		return this.rule;
	}

	/**
	 * This method returns time condition
	 * @return time condition
	 */
	public final double getTimeCondition() {
		return this.timeCondition;
	}

	/**
	 * This method returns perturbation expressions from right handSide.
	 * @return perturbation expressions from right handSide
	 */
	public final List<PerturbationExpressionInterface> getRHSParametersList() {
		return parametersRHS;
	}

	/**
	 * This method return <tt>true</tt> if this did apply, otherwise <tt>false</tt>
	 * @return <tt>true</tt> if this did apply, otherwise <tt>false</tt>
	 */
	public final boolean isDo() {
		return this.isDO;
	}

	/**
	 * This method checks a need to apply "ONCE modification". 
	 * @param currentTime given time for checks
	 * @see PerturbationType
	 */
	public final void checkConditionOnce(double currentTime) {
		if (currentTime > this.timeCondition) {
			rule.setInfinityRateFlag(true);
			isDO = true;
			rule.setRuleRate(1.0);
		}
	}

	/**
	 * Util method. Initialization left handSide perturbation expression.
	 * @param observableId given list of observable components for checks to apply.
	 * @param parameters given list of correction factor for <b>obsID</b>
	 * @param obs observables storage. 
	 */
	private final void fillParameters(List<ObservableInterface> observableId,
			List<Double> parameters, Observables obs) {
		this.parametersLHS = new ArrayList<PerturbationExpressionInterface>();
		for (int i = 0; i < observableId.size(); i++) {

			ObservableInterface mainAmNumber = null;
			if (observableId.get(i) instanceof ObservableConnectedComponent) {
				int index = ((ObservableConnectedComponent) observableId.get(i))
						.getMainAutomorphismNumber();
				if (index != ObservableConnectedComponent.NO_INDEX)
					mainAmNumber = obs.getComponentList().get(
							((ObservableConnectedComponent) observableId.get(i))
									.getMainAutomorphismNumber());
			}

			if (mainAmNumber == null)
				this.parametersLHS.add(new SumParameters(observableId.get(i),
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

		if (observableId.size() != parameters.size()) {
			this.parametersLHS.add(new SumParameters(null, parameters
					.get(parameters.size() - 1)));
		}
	}

	/**
	 * This method checks a need to apply "TIME modification". 
	 * @param currentTime given time for checks
	 * @see PerturbationType
	 */
	public final boolean checkCondition(double currentTime) {
		if (currentTime > this.timeCondition) {
			checkAndSetRuleRate();
			this.isDO = true;
			return true;
		}
		return false;
	}

	/**
	 * Util method. Calculates and sets rule rate.
	 */
	private final void checkAndSetRuleRate(){
		fillPerturbationRate();
		if(perturbationRate == Double.POSITIVE_INFINITY){
			this.rule.setRuleRate(1);
			this.rule.setInfinityRateFlag(true);
		}else				
			this.rule.setRuleRate(this.perturbationRate);
	}

	/**
	 * Util method. Calculates new perturbation rate.
	 */
	private final void fillPerturbationRate() {
		this.perturbationRate = 0;
		for (PerturbationExpressionInterface re : this.parametersRHS) {
			this.perturbationRate += re.getMultiplication(null);
		}
	}

	/**
	 * This method checks a need to apply "NUMBER modification". 
	 * @param observables observables storage. 
	 * @see PerturbationType
	 */
	public final boolean checkCondition(Observables observables) {
		double obsSize = observables.getComponentList().get(observableId)
				.getCurrentState(observables);

		if (this.inequalitySign.satisfy(obsSize, calculateSum(observables))) {
			checkAndSetRuleRate();
			return true;
		}
		if (this.inequalitySign == InequalitySign.GREATER) {
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

	/**
	 * Util method. Calculates left handSide expressions.
	 * @param observables observables storage.
	 * @return amount of left handSide expressions.
	 */
	private final double calculateSum(Observables observables) {
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

	public final InequalitySign inequalitySign() {
		return this.inequalitySign;
	}
}
