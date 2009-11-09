package com.plectix.simulator.simulationclasses.perturbations;

import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ConditionType;
import com.plectix.simulator.util.InequalitySign;

public class TimeCondition implements ConditionInterface {
	private final double timeLimit;
	
	public TimeCondition(double timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	@Override
	public boolean check(double currentTime) {
		return currentTime > this.timeLimit;
	}

	@Override
	public ConditionType getType() {
		return ConditionType.TIME;
	}

	@Override
	public InequalitySign inequalitySign() {
		return InequalitySign.GREATER;
	}

	public double getTimeLimit() {
		return timeLimit;
	}
	
	@Override
	public String toString() {
		return "$T > " + timeLimit;
	}
}
