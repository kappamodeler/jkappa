package com.plectix.simulator.parser.abstractmodel.perturbations;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.AbstractAgent;

public class AbstractAddOnceModification extends AbstractOnceModification {

	public AbstractAddOnceModification(List<AbstractAgent> operand, double quant) {
		super(operand, quant);
	}
	
	public String toString() {
		return "$ADDONCE " + getQuantity() + " * " + getSubstance();
	}
}
