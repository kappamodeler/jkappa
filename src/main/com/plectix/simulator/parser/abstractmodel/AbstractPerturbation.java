package com.plectix.simulator.parser.abstractmodel;

import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.AbstractCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractModification;

public class AbstractPerturbation {
	private final AbstractCondition myCondition;
	private final AbstractModification myModification;
	private final int myId;
	
	public AbstractPerturbation(int id, AbstractCondition condition, AbstractModification modification) {
		myId = id;
		myCondition = condition;
		myModification = modification;
	}
	
	public AbstractCondition getCondition() {
		return myCondition;
	}
	
	public AbstractModification getModification() {
		return myModification;
	}
	
	public int getId() {
		return myId;
	}
	
	public String toString() {
		return "%mod: " + myCondition + " do " + myModification;
	}
}
