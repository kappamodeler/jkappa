package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.components.bologna.Reaction;
import com.plectix.simulator.components.bologna.ReactionClass;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

public class RuleApplicator {
	private final SimulationData simulationData;
	private IRandom random = ThreadLocalData.getRandom();
	
	public RuleApplicator(SimulationData data) {
		this.simulationData = data;
	}
	
	public final boolean applyRule(CRule rule, List<CInjection> injections, SimulationData data) {
		if (!rule.isUnusualBinary()) {
			rule.applyRule(injections, data);
			return true;
		} else {
			// as rule is binary 'injections' contains exactly 2 elements
			CInjection firstInjection = injections.get(0);
			CInjection secondInjection = injections.get(1);
			long ruleInjectionsWeight 
						= rule.getLeftHandSide().get(0).getInjectionsWeight()
							+ rule.getLeftHandSide().get(1).getInjectionsWeight();
			Reaction currentReaction = new Reaction(rule, firstInjection, secondInjection);
			// k1
			double rate = rule.getRate();
			// k2
			double additionalRate = rule.getAdditionalRate();
			double temp = rate / ruleInjectionsWeight;
			double k2prime = Math.max(temp, additionalRate);
			
			double pIntra = temp * additionalRate; 
			double pInter = additionalRate / k2prime;
			if (currentReaction.isUnary()) {
				// see documentation
				return performReactionWithProba(currentReaction, pIntra);
			} else if (currentReaction.isSimpleBinary()) {
				return performReactionWithProba(currentReaction, pInter);
			} else if (currentReaction.getType() == ReactionClass.BINARY_POLYMERIZING) {
				for (Reaction swap : currentReaction.getSwappedReactions()) {
					if (performReactionWithProba(swap, pIntra)) {
						return true; 
					}
				}
			}
			return false;
		}
	}
	
	private final boolean performWithProba(Reaction reaction, double probability) {
		if (random.getDouble() < probability) {
			reaction.getRule().applyRule(reaction.getInjectionsList(), simulationData);
			System.out.println("onw");
			return true;
		} else {
			return false;
		}
	}
	
	private final boolean performReactionWithProba(Reaction reaction, double probability) {
		return this.performWithProba(reaction, probability);
	}
}
