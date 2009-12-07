package com.plectix.simulator.simulationclasses.perturbations;

import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModificationType;

public abstract class OnceModification extends AbstractModification {
	private final PerturbationRule rule;
	private final int quantity;
	
	public OnceModification(PerturbationRule rule, int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException(
					"Failed on attempt of setting negative argument to " + this.getType() + "ONCE modification");
		}
		this.rule = rule;
		this.quantity = quantity;
		rule.setCount(quantity);
	}
	
	// TODO move to StringUtil!!!
	private static StringBuffer allComponentsToStringBuffer(List<ConnectedComponentInterface> components) {
		String comma = "";
		StringBuffer sb = new StringBuffer();
		for (ConnectedComponentInterface cc : components) {
			sb.append(comma + cc);
			comma = ", ";
		}
		return sb;
	}
	
	@Override
	public final String toString() {
		ModificationType type = this.getType();
		StringBuffer sb = new StringBuffer("$" + this.getType() + "ONCE " + quantity + " * ");
		switch (type) {
		case ADDONCE : {
			sb.append(allComponentsToStringBuffer(rule.getRightHandSide()));
			break;
		}
		case DELETEONCE : {
			sb.append(allComponentsToStringBuffer(rule.getLeftHandSide()));
			break;
		}
		}
		return "perturbation has wrong type, this was not supposed to happen =(";
	}

	@Override
	protected void doItAll() {
		rule.setInfinityRateFlag(true);
		rule.setRuleRate(1.0);
	}
	
	public final PerturbationRule getPerturbationRule() {
		return rule;
	}
}

