package com.plectix.simulator.rulecompression;

import java.util.*;

import com.plectix.simulator.action.*;
import com.plectix.simulator.components.*;
import com.plectix.simulator.components.solution.*;
import com.plectix.simulator.interfaces.*;

/*package*/ class RuleAnalyzer {
	private final CRule rule;
	private final Set<CAgent> modifiedAgents = new HashSet<CAgent>();
	private final Set<CAgent> untouchedComponentsAgents = new HashSet<CAgent>();

	public RuleAnalyzer(CRule rule) {
		this.rule = rule;
		analyzeActions();
	}

	private void analyzeActions() {
		for (IConnectedComponent cc : rule.getLeftHandSide()) {
			untouchedComponentsAgents.addAll(cc.getAgents());
		}
		for (CAction action : rule.getActionList()) {
			CActionType type = CActionType.getById(action.getTypeId());
			if (action.getTypeId() != CActionType.NONE.getId()) {
				if (CActionType.getById(action.getTypeId()) != CActionType.ADD) {
					CAgent modifiedAgent = null;
					if (type == CActionType.DELETE) {
						modifiedAgent = action.getAgentFrom();
					} else {
						modifiedAgent = action.getSiteFrom().getAgentLink();
					}
					modifiedAgents.add(modifiedAgent);
					untouchedComponentsAgents.removeAll(SolutionUtils
							.getConnectedComponent(modifiedAgent).getAgents());
				}
			}
		}
	}

	public boolean canBeRoot(CAgent agent) {
		return untouchedComponentsAgents.contains(agent)
				|| modifiedAgents.contains(agent);
	}
}
