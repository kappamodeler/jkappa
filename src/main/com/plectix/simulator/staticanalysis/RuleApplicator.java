package com.plectix.simulator.staticanalysis;

import java.util.List;

import com.plectix.simulator.interfaces.RandomInterface;
import com.plectix.simulator.simulationclasses.bologna.Reaction;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;

public final class RuleApplicator {
	private final SimulationData simulationData;
	private final RandomInterface random = ThreadLocalData.getRandom();
	
	public RuleApplicator(SimulationData data) {
		this.simulationData = data;
	}
	
	public final List<Injection> applyRule(Rule rule, List<Injection> injections, SimulationData data) throws StoryStorageException {
		if (!rule.bolognaWanted()) {
			rule.applyRule(injections, data);
			return injections;
		} else {
			// as rule is binary 'injections' contains exactly 2 elements
			Injection firstInjection = injections.get(0);
			Injection secondInjection = injections.get(1);
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
			double pInter = k2 / k2prime;

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
				for (Reaction swap : currentReaction.getSwappedReactions()) {
					if (performReactionWithProba(swap, pIntra)) {
						return swap.getInjections(); 
					}
				}
			}
			return null;
		}
	}
	
	private final boolean performReactionWithProba(Reaction reaction, double probability) throws StoryStorageException {
		if (random.getDouble() < probability) {
			reaction.getRule().applyRule(reaction.getInjections(), simulationData);
			return true;
		} else {
			return false;
		}
	}
}
