package com.plectix.simulator.simulationclasses.bologna;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.injections.LiftElement;
import com.plectix.simulator.simulationclasses.solution.SolutionUtils;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;

/**
 * This class describes only those reactions, which we should mention 
 * when talking about Bologna method
 * @author evlasov
 *
 */
public final class Reaction {
	private final Injection firstInjection;
	private final Injection secondInjection;
	private final ConnectedComponentInterface firstComponent;
	private final ConnectedComponentInterface secondComponent;
	private List<Injection> list;
	private final ReactionClass type;
	private final Rule rule;
	private final Agent agentInFirstComponentToSwap;
	private final Agent agentInSecondComponentToSwap;
	
	/**
	 * Constructor.
	 * @param rule
	 * @param firstInjection
	 * @param secondInjection
	 */
	public Reaction(Rule rule, Injection firstInjection, Injection secondInjection) {
		this.rule = rule;
		this.firstInjection = firstInjection;
		this.secondInjection = secondInjection;
		this.firstComponent = SolutionUtils.getConnectedComponent(firstInjection.getImageAgent());
		this.secondComponent = SolutionUtils.getConnectedComponent(secondInjection.getImageAgent());
		Agent rulesFirstAgent = rule.getLeftHandSide().get(0).getAgents().get(0);
		Agent rulesSecondAgent = rule.getLeftHandSide().get(1).getAgents().get(0);
		
		this.agentInFirstComponentToSwap = 
				this.findSimilarAgent(firstComponent, rulesSecondAgent, 
						firstInjection.getImageAgent());
		this.agentInSecondComponentToSwap = 
				this.findSimilarAgent(secondComponent, rulesFirstAgent, 
						secondInjection.getImageAgent());
		
		if (firstComponent.getAgents().contains(secondInjection.getImageAgent())) {
			type = ReactionClass.UNARY;
		} else if (agentInFirstComponentToSwap != null) {
			if (agentInSecondComponentToSwap != null) {
				type = ReactionClass.BINARY_TWICE_POLYMERIZING;
			} else {
				type = ReactionClass.BINARY_POLYMERIZING;
			}
		} else if (agentInSecondComponentToSwap != null) {
			type = ReactionClass.BINARY_POLYMERIZING;
		} else {
			type = ReactionClass.BINARY;
		}
	}
	
	private final Agent findSimilarAgent(ConnectedComponentInterface component, 
			Agent agent, Agent exception) {
		for (Agent ccAgent : component.getAgents()) {
			if (SimulationUtils.justCompareAgents(ccAgent, agent) && ccAgent != exception) {
				return ccAgent;
			}
		}
		return null;
	}

	
	
	public final List<Injection> getInjections() {
		if (list == null) {
			list = new ArrayList<Injection>();
			list.add(this.firstInjection);
			list.add(this.secondInjection);
		}
		return list;
	}
	
	/**
	 * @return <tt>true</tt> if and only if this reaction is polymerizing (see documentation)
	 */
	public final boolean isPolymerizing() {
		return type == ReactionClass.BINARY_POLYMERIZING 
				|| type == ReactionClass.BINARY_TWICE_POLYMERIZING;
	}
	
	/**
	 * @return <tt>true</tt> if and only if this reaction is unary (see documentation)
	 */
	public final boolean isUnary() {
		return type == ReactionClass.UNARY;
	}
	
	/**
	 * @return <tt>true</tt> if and only if this reaction is simple binary (see documentation)
	 */
	public final boolean isSimpleBinary() {
		return type == ReactionClass.BINARY;
	}
	
	/**
	 * @return rule, which belongs to this reaction
	 */
	public final Rule getRule() {
		return this.rule;
	}
	
	public final List<Reaction> getSwappedReactions() {
		List<Reaction> list = new ArrayList<Reaction>();
		if (agentInFirstComponentToSwap != null) {
			for (Site site : agentInFirstComponentToSwap.getSites()) { 
				for (LiftElement liftE : site.getLift()) {
				// we can compare these for being ==
					if (liftE.getInjection().getConnectedComponent() 
								== rule.getLeftHandSide().get(1)) {
						list.add(new Reaction(rule, firstInjection, 
								liftE.getInjection()));
					}
				}
			}
		}
		if (agentInSecondComponentToSwap != null) {
			for (Site site : agentInSecondComponentToSwap.getSites()) { 
				for (LiftElement liftE : site.getLift()) {
				// we can compare these for being ==
					if (liftE.getInjection().getConnectedComponent() 
								== rule.getLeftHandSide().get(0)) {
						list.add(new Reaction(rule, 
								liftE.getInjection(),
								secondInjection));
					}
				}
			}
		}
		return list;
	}
}


