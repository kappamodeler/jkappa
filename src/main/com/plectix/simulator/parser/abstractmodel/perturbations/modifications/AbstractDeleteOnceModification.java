package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.AbstractAgent;

public class AbstractDeleteOnceModification extends AbstractOnceModification {

	public AbstractDeleteOnceModification(List<AbstractAgent> operand, double quant) {
		super(operand, quant);
	}

	@Override
	protected String actionOnceSymbol() {
		return "DELETE";
	}

	@Override
	public ModificationType getType() {
		return ModificationType.DELETEONCE;
	}
	
}
