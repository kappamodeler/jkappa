package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.components.bologna.Reaction;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.simulator.SimulationData;

public class RuleApplicator {
	private void applyWithProba(CRule rule, List<CInjection> injectionList, 
			SimulationData simulationData, double probability) {
		if (new Random().nextDouble() < probability) {
			rule.applyRule(injectionList, simulationData);
		}
	}
	
	private void performReactionWithProba(CRule rule, Reaction reaction, 
			SimulationData simulationData, double probability) {
		this.applyWithProba(rule, reaction.getInjectionsList(), simulationData, probability);
	}
}
