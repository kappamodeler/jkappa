package com.plectix.simulator.components.constraints;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;

public class NoHelixConstraint extends Constraint {

	@Override
	protected boolean acceptMatchingRule(IRule rule, IConnectedComponent cc1,
			IConnectedComponent cc2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ruleIsMatching(IRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

}
