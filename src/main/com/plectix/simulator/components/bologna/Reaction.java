package com.plectix.simulator.components.bologna;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.injections.CLiftElement;
import com.plectix.simulator.components.solution.SolutionUtils;
import com.plectix.simulator.interfaces.IConnectedComponent;

/**
 * This class describes only those reactions, which we should mention 
 * when talking about Bologna method
 * @author evlasov
 *
 */
public class Reaction {
	private final CInjection firstInjection;
	private final CInjection secondInjection;
	private final IConnectedComponent firstComponent;
	private final IConnectedComponent secondComponent;
	private List<CInjection> list;
	private final ReactionClass type;
	private final CRule rule;
	private final CAgent agentInFirstComponentToSwap;
	private final CAgent agentInSecondComponentToSwap;
	
	/**
	 * Constructor.
	 * @param rule
	 * @param firstInjection
	 * @param secondInjection
	 */
	public Reaction(CRule rule, CInjection firstInjection, CInjection secondInjection) {
		this.rule = rule;
		this.firstInjection = firstInjection;
		this.secondInjection = secondInjection;
		this.firstComponent = SolutionUtils.getConnectedComponent(firstInjection.getImageAgent());
		this.secondComponent = SolutionUtils.getConnectedComponent(secondInjection.getImageAgent());
		CAgent rulesFirstAgent = rule.getLeftHandSide().get(0).getAgents().get(0);
		CAgent rulesSecondAgent = rule.getLeftHandSide().get(1).getAgents().get(0);
		
		this.agentInFirstComponentToSwap = 
				firstComponent.findSimilarAgent(rulesSecondAgent, firstInjection.getImageAgent());
		this.agentInSecondComponentToSwap = 
				secondComponent.findSimilarAgent(rulesFirstAgent, secondInjection.getImageAgent());
		
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
	
	public List<CInjection> getInjectionsList() {
		if (list == null) {
			list = new ArrayList<CInjection>();
			list.add(this.firstInjection);
			list.add(this.secondInjection);
		}
		return list;
	}
	
	public final ReactionClass getType() {
		return type;
	}
	
	/**
	 * @return <tt>true</tt> if and only if this reaction is polymerizing (see documentation)
	 */
	public boolean isPolymerizing() {
		return type == ReactionClass.BINARY_POLYMERIZING 
				|| type == ReactionClass.BINARY_TWICE_POLYMERIZING;
	}
	
	/**
	 * @return <tt>true</tt> if and only if this reaction is unary (see documentation)
	 */
	public boolean isUnary() {
		return type == ReactionClass.UNARY;
	}
	
	/**
	 * @return <tt>true</tt> if and only if this reaction is simple binary (see documentation)
	 */
	public boolean isSimpleBinary() {
		return type == ReactionClass.BINARY;
	}
	
	/**
	 * @return rule, which belongs to this reaction
	 */
	public CRule getRule() {
		return this.rule;
	}
	
	public List<Reaction> getSwappedReactions() {
		List<Reaction> list = new ArrayList<Reaction>();
		if (agentInFirstComponentToSwap != null) {
			for (CSite site : agentInFirstComponentToSwap.getSites()) { 
				for (CLiftElement liftE : site.getLift()) {
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
			for (CSite site : agentInSecondComponentToSwap.getSites()) { 
				for (CLiftElement liftE : site.getLift()) {
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


