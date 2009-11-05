package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.util.ParserUtil;

public abstract class AbstractOnceModification implements PerturbationModification {
	private final List<ModelAgent> substanceAgents;
	// TODO $INF == -1
	private final double quantity;
	
	public AbstractOnceModification(List<ModelAgent> agents, double quantity) {
		this.substanceAgents = agents;
		this.quantity = quantity;
	}
	
	public final double getQuantity() {
		return quantity;
	}
	
	public final List<ModelAgent> getSubstanceAgents() {
		return substanceAgents;
	}
	
	@Override
	public final String toString() {
		return "$" + actionOnceSymbol() + "ONCE " + getQuantity() + " * " + ParserUtil.listToString(getSubstanceAgents());
	}
	
	protected abstract String actionOnceSymbol();
}
