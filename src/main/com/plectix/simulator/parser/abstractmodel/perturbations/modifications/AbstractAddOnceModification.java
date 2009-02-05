package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.util.StringUtil;

public class AbstractAddOnceModification extends AbstractOnceModification {

	public AbstractAddOnceModification(List<AbstractAgent> operand, double quant) {
		super(operand, quant);
	}
	
	@Override
	protected String actionOnceSymbol() {
		return "ADD";
	}

	@Override
	public ModificationType getType() {
		return ModificationType.ADDONCE;
	}
}
