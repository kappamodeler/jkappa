package com.plectix.simulator.simulationclasses.perturbations;

import com.plectix.simulator.simulationclasses.perturbations.util.LinearExpression;
import com.plectix.simulator.simulationclasses.perturbations.util.VectorRule;
import com.plectix.simulator.staticanalysis.Rule;

public class RateModification extends AbstractModification {
	private final Rule rule;
	private final LinearExpression<VectorRule> expression;
	
	/**
	 * The only constructor
	 * @param changingRule rule which rate we want to change 
	 * @param expression expression describing rate's modification
	 */
	public RateModification(Rule changingRule, LinearExpression<VectorRule> expression) {
		this.rule = changingRule;
		this.expression = expression;
	}

	@Override
	protected void doItAll() {
		System.out.println(expression.calculate());
		rule.setRuleRate(expression.calculate());
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("'");
		sb.append(rule.getName());
		sb.append("' := ");
		sb.append(expression);
		return sb.toString();
	}
}
