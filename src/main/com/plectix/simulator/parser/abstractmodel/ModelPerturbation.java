package com.plectix.simulator.parser.abstractmodel;

import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.PerturbationCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.PerturbationModification;

public final class ModelPerturbation {
	private final PerturbationCondition condition;
	private final PerturbationModification modification;
	private final int id;
	
	public ModelPerturbation(int id, PerturbationCondition condition, PerturbationModification modification) {
		this.id = id;
		this.condition = condition;
		this.modification = modification;
	}
	
	public final PerturbationCondition getCondition() {
		return condition;
	}
	
	public final PerturbationModification getModification() {
		return modification;
	}
	
	public final int getId() {
		return id;
	}
	
	@Override
	public final String toString() {
		return "%mod: " + condition + " do " + modification;
	}
}
