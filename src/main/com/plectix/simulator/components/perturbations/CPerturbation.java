package com.plectix.simulator.components.perturbations;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.*;

/**
 * This class implements Perturbation entity.
 * @author avokhmin
 * @see CPerturbationType
 */
@SuppressWarnings("serial")
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
	private CRule rule;
	private int perturbationID;
	private boolean greater = true;
	private boolean isDO = false;
	private List<IPerturbationExpression> parametersRHS;

	/**
	 * Constructor of perturbation for "TIME condition".
	 * @param perturbationID given unique id
	 * @param time given time condition
	 * @param rule given rule for modify
	 * @param rateParameters given rate expression
	 * @see CPerturbationType
	 */
	public CPerturbation(int perturbationID, double time,
			CRule rule,
			List<IPerturbationExpression> rateParameters) {
		this.perturbationID = perturbationID;
		this.timeCondition = time;
		this.type = CPerturbationType.TIME;
		this.perturbationRate = -1;
		this.rule = rule;
		this.ruleRate = rule.getRate();
		this.greater = true;
		this.parametersRHS = rateParameters;
	}

	/**
	 * Constructor of perturbation for "Once modification".
	 * @param perturbationID given unique id
	 * @param time given time condition
	 * @param rule given specially rule
	 * @see CPerturbationType
	 */
	public CPerturbation(int perturbationID, double time,
			CRulePerturbation rule) {
		this.perturbationID = perturbationID;
		this.timeCondition = time;
		this.type = CPerturbationType.ONCE;
		this.rule = rule;
		this.ruleRate = rule.getRate();
		this.greater = true;
	}

	/**
	 * Constructor of perturbation for "NUMBER modification".
	 * @param perturbationID given unique id
	 * @param obsComponents given list of observable components for checks to apply.
	 * @param parameters given list of correction factor for <b>obsComponents</b>
	 * @param obsNameID given id of watch observable.
	 * @param rule given rule for modify
	 * @param greater <tt>true</tt> if at line we have 
	 * <code>"%mod: ['a'] <b>></b> 100.0 ..."</code>,<br>
	 * else if  <code>"%mod: ['a'] <b><</b> 100.0 ..."</code>, <tt>false</tt>
	 * @param rateParameters given list of rate parameters for right handSide.
	 * @param observables observables storage. 
	 */
	public CPerturbation(int perturbationID, List<IObservablesComponent> obsComponents,
			List<Double> parameters, int obsNameID,
			CRule rule, boolean greater,
			List<IPerturbationExpression> rateParameters, CObservables observables) {
		this.perturbationID = perturbationID;
		this.obsNameID = obsNameID;
		CObservables obs = observables;
		fillParameters(obsComponents, parameters, obs);
		this.type = CPerturbationType.NUMBER;
		this.perturbationRate = -1.;
		this.rule = rule;
		this.ruleRate = rule.getRate();
		this.greater = greater;
		this.parametersRHS = rateParameters;
	}
	
	/**
	 * This method returns unique id of this.
	 * @return unique id of this.
	 */
	public final int getObsNameID() {
		return this.obsNameID;
	}

	/**
	 * This method returns perturbation expressions from left handSide.
	 * @return perturbation expressions from left handSide.
	 */
	public final List<IPerturbationExpression> getLHSParametersList() {
		return this.parametersLHS;
	}

	/**
	 * This method returns rule for modify
	 * @return rule for modify
	 */
	public final CRule getPerturbationRule() {
		return this.rule;
	}

	/**
	 * This method returns greater from this.
	 * @return greater
	 */
	public final boolean getGreater() {
		return this.greater;
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
	public final List<IPerturbationExpression> getRHSParametersList() {
		return Collections.unmodifiableList(parametersRHS);
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
	 * @see CPerturbationType
	 */
	public final void checkConditionOnce(double currentTime) {
		if (currentTime > this.timeCondition) {
			rule.setInfinityRate(true);
			isDO = true;
			rule.setRuleRate(1.0);
		}
	}

	/**
	 * Util method. Initialization left handSide perturbation expression.
	 * @param obsID given list of observable components for checks to apply.
	 * @param parameters given list of correction factor for <b>obsID</b>
	 * @param obs observables storage. 
	 */
	private final void fillParameters(List<IObservablesComponent> obsID,
			List<Double> parameters, CObservables obs) {
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

	/**
	 * This method checks a need to apply "TIME modification". 
	 * @param currentTime given time for checks
	 * @see CPerturbationType
	 */
	public final boolean checkCondition(double currentTime) {
		if (currentTime > this.timeCondition) {
			checkAndSetRuleRate();
//			fillPerturbationRate();
//			if(perturbationRate == Double.POSITIVE_INFINITY){
//				this.rule.setRuleRate(1);
//				this.rule.setInfinityRate(true);
//			}else				
//				this.rule.setRuleRate(this.perturbationRate);
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
			this.rule.setInfinityRate(true);
		}else				
			this.rule.setRuleRate(this.perturbationRate);
	}

	/**
	 * Util method. Calculates new perturbation rate.
	 */
	private final void fillPerturbationRate() {
		this.perturbationRate = 0;
		for (IPerturbationExpression re : this.parametersRHS) {
			this.perturbationRate += re.getMultiplication(null);
		}
	}

	/**
	 * This method checks a need to apply "NUMBER modification". 
	 * @param observables observables storage. 
	 * @see CPerturbationType
	 */
	public final boolean checkCondition(CObservables observables) {
		double obsSize = observables.getComponentList().get(obsNameID)
				.getCurrentState(observables);

		if ((greater && (obsSize > calculateSum(observables)))
				|| (!(greater) && (obsSize < calculateSum(observables)))) {
			checkAndSetRuleRate();
//			fillPerturbationRate();
//			if(perturbationRate == Double.POSITIVE_INFINITY){
//				this.rule.setRuleRate(1);
//				this.rule.setInfinityRate(true);
//			}else				
//				this.rule.setRuleRate(this.perturbationRate);
//			this.rule.setRuleRate(this.perturbationRate);
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

	/**
	 * Util method. Calculates left handSide expressions.
	 * @param observables observables storage.
	 * @return amount of left handSide expressions.
	 */
	private final double calculateSum(CObservables observables) {
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
