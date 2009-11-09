package com.plectix.simulator.simulationclasses.perturbations;

import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModificationType;

public class DeleteOnceModification extends OnceModification {

	public DeleteOnceModification(PerturbationRule rule, int quantity) {
		super(rule, quantity);
	}

	@Override
	public ModificationType getType() {
		return ModificationType.DELETEONCE;
	}
}
