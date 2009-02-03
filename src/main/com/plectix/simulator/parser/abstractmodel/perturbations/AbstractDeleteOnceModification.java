package com.plectix.simulator.parser.abstractmodel.perturbations;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.AbstractAgent;

public class AbstractDeleteOnceModification extends AbstractOnceModification {

	public AbstractDeleteOnceModification(List<AbstractAgent> operand, double quant) {
		super(operand, quant);
	}
	
	public String toString() {
		return "$DELETEONCE " + getQuantity() + " * " + getSubstance();
	}
}
