package com.plectix.simulator.parser.abstractmodel.perturbations.conditions;

import java.util.Collection;

public final class ModelConjuctionCondition implements PerturbationCondition {
	private final Collection<PerturbationCondition> conditions;
	
	public ModelConjuctionCondition(Collection<PerturbationCondition> conditions) {
		this.conditions = conditions;
	}
	
	@Override
	public final ConditionType getType() {
		return ConditionType.COMPLEX;
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		boolean flag = false;
		for (PerturbationCondition condition : conditions) {
			if (flag) {
				sb.append(" & ");	
			} else {
				flag = true;
			}
			sb.append(condition);
		}
		return sb.toString(); 
	}
	
	public final Collection<PerturbationCondition> getConditions() {
		return conditions;
	}
}
