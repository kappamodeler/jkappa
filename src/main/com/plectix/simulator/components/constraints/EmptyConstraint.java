package com.plectix.simulator.components.constraints;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;

public class EmptyConstraint extends Constraint {

	@Override
	protected boolean acceptMatchingRule(IRule rule, IConnectedComponent cc1,
			IConnectedComponent cc2) {
		return true;
	}

	@Override
	public boolean ruleIsMatching(IRule rule) {
		return true;
	}

}
