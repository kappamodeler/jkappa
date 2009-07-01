package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.components.bologna.Reaction;
import com.plectix.simulator.components.bologna.ReactionClass;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.SolutionUtils;
import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

public class RuleApplicator {
	private final SimulationData simulationData;
	private IRandom random = ThreadLocalData.getRandom();
	
	public RuleApplicator(SimulationData data) {
		this.simulationData = data;
	}
	
	public final List<CInjection> applyRule(CRule rule, List<CInjection> injections, SimulationData data) {
		if (!rule.isUnusualBinary()) {
			rule.applyRule(injections, data);
			return injections;
//			return true;
		} else {
			// as rule is binary 'injections' contains exactly 2 elements
			CInjection firstInjection = injections.get(0);
			CInjection secondInjection = injections.get(1);
			long ruleInjectionsWeight 
						= rule.getLeftHandSide().get(0).getInjectionsWeight()
							+ rule.getLeftHandSide().get(1).getInjectionsWeight();
			Reaction currentReaction = new Reaction(rule, firstInjection, secondInjection);
			// rate
			double k1 = rule.getRate();
			// additional rate
			double k2 = rule.getAdditionalRate();
			double temp = k1 / ruleInjectionsWeight;
			double k2prime = Math.max(temp, k2);
			
			double pIntra = temp / k2prime; 
//			System.out.println(pIntra);
			double pInter = k2 / k2prime;
//			System.out.println(pInter);
//			System.out.println("--------------------------");
//			for (CInjection inj : injections) {
//				System.out.println(inj.getImageAgent() + " from " 
//						+ SolutionUtils.getConnectedComponent(inj.getImageAgent()));
//			}
//			System.out.println(currentReaction.getType());
			if (currentReaction.isUnary()) {
				// see documentation
				if (performReactionWithProba(currentReaction, pIntra)){
					return injections;
				}
			} else if (currentReaction.isSimpleBinary()) {
				if (performReactionWithProba(currentReaction, pInter)) {
					return injections;
				}
			} else if (currentReaction.isPolymerizing()) {
//				System.out.println(currentReaction.getSwappedReactions());
				for (Reaction swap : currentReaction.getSwappedReactions()) {
					if (performReactionWithProba(swap, pIntra)) {
						return swap.getInjectionsList(); 
					}
				}
			}
			return null;
		}
	}
	
	private final boolean performReactionWithProba(Reaction reaction, double probability) {
		if (random.getDouble() < probability) {
//			System.out.println("APPLICATION");
//			System.out.println("--------------------------");
//			for (CInjection inj : reaction.getInjectionsList()) {
//				System.out.println(inj.getImageAgent() + " from " 
//						+ SolutionUtils.getConnectedComponent(inj.getImageAgent()));
//			}
//			System.out.println(reaction.getType());
			reaction.getRule().applyRule(reaction.getInjectionsList(), simulationData);
			return true;
		} else {
//			System.out.println("NO");
			return false;
		}
	}
}
