package com.plectix.simulator.components.constraints;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;


public class EmptyConstraint extends Constraint {

	@Override
	protected boolean acceptMatchingRule(CRule rule, IConnectedComponent cc1,
			IConnectedComponent cc2) {
		return true;
	}

	@Override
	public boolean ruleIsMatching(CRule rule) {
		return true;
	}

}
