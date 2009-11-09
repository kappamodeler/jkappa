package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.ModelAgent;

public final class ModelAddOnceModification extends AbstractOnceModification {
	public ModelAddOnceModification(List<ModelAgent> agents, int quantity) {
		super(agents, quantity);
	}
	
	@Override
	public final ModificationType getType() {
		return ModificationType.ADDONCE;
	}
}
