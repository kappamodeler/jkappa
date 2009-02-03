package com.plectix.simulator.parser.abstractmodel.perturbations;

import java.util.*;

import com.plectix.simulator.parser.abstractmodel.AbstractAgent;

public class AbstractOnceModification implements AbstractModification {
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
}
