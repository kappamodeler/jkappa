package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.util.StringUtil;

public abstract class AbstractOnceModification implements AbstractModification {
	private final List<AbstractAgent> mySubstance;
	// TODO $INF == -1
	private final double myQuantity;
	
	public AbstractOnceModification(List<AbstractAgent> operand, double quant) {
		mySubstance = operand;
		myQuantity = quant;
	}
	
	public double getQuantity() {
		return myQuantity;
	}
	
	public List<AbstractAgent> getSubstance() {
		return mySubstance;
	}
	
	public String toString() {
		return "$" + actionOnceSymbol() + "ONCE " + getQuantity() + " * " + StringUtil.listToString(getSubstance());
	}
	
	protected abstract String actionOnceSymbol();
}
