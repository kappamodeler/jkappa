package com.plectix.simulator.simulationclasses.perturbations;

import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ConditionType;
import com.plectix.simulator.util.InequalitySign;

public interface ConditionInterface {
	public InequalitySign inequalitySign();
	public boolean check(double currentTime);
	public ConditionType getType();
}
