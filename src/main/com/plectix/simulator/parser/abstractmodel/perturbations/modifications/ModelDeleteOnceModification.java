package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.ModelAgent;

public final class ModelDeleteOnceModification extends AbstractOnceModification {
	public ModelDeleteOnceModification(List<ModelAgent> agents, double quantity) {
		super(agents, quantity);
	}

	@Override
	public final ModificationType getType() {
		return ModificationType.DELETEONCE;
	}
	
	@Override
	protected final String actionOnceSymbol() {
		return "DELETE";
	}
}
