package com.plectix.simulator.simulationclasses.perturbations;

import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModificationType;

public abstract class AbstractModification {
	private boolean performed = false;
	
	protected abstract void doItAll(); 
	
	public void perform() {
		this.doItAll();
		performed = true;
	}

	public boolean wasPerformed() {
		return performed;
	}

	// we may need this one in future
	public void reset() {
		performed = false;
	}
	
	public abstract ModificationType getType();
}
