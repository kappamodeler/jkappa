package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.ModelAgent;

public final class ModelAddOnceModification extends AbstractOnceModification {
	public ModelAddOnceModification(List<ModelAgent> agents, double quantity) {
		super(agents, quantity);
	}
	
	@Override
	public final ModificationType getType() {
		return ModificationType.ADDONCE;
	}
	
	@Override
	protected final String actionOnceSymbol() {
		return "ADD";
	}
}
