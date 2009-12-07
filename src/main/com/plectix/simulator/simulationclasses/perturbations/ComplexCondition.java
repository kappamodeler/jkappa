package com.plectix.simulator.simulationclasses.perturbations;

import java.util.Collection;

import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ConditionType;
import com.plectix.simulator.util.InequalitySign;

public class ComplexCondition implements ConditionInterface {
	private final Collection<ConditionInterface> conditions;
	
	public ComplexCondition(Collection<ConditionInterface> conditions) {
		this.conditions = conditions;
	}
		
	@Override
	public boolean check(double currentTime) {
		for (ConditionInterface condition : conditions) {
			if (!condition.check(currentTime)) {
				return false;		
			}
		}
		return true;
	}

	@Override
	public ConditionType getType() {
		return ConditionType.COMPLEX;
	}

	@Override
	public InequalitySign inequalitySign() {
		throw new UnsupportedOperationException("This wasn't expected");
	}
}
