package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.util.ParserUtil;

public abstract class AbstractOnceModification implements PerturbationModification {
	private final List<ModelAgent> substanceAgents;
	// TODO $INF == -1
	private final int quantity;
	
	AbstractOnceModification(List<ModelAgent> agents, int quantity) {
		this.substanceAgents = agents;
		this.quantity = quantity;
	}
	
	public final int getQuantity() {
		return quantity;
	}
	
	public final List<ModelAgent> getSubstanceAgents() {
		return substanceAgents;
	}
	
	@Override
	public final String toString() {
		return "$" + this.getType() + "ONCE " + getQuantity() + " * " + ParserUtil.listToString(getSubstanceAgents());
	}
}
