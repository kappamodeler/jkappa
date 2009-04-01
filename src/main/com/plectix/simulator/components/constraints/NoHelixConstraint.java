package com.plectix.simulator.components.constraints;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;


public class NoHelixConstraint extends Constraint {

	@Override
	protected boolean acceptMatchingRule(CRule rule, IConnectedComponent cc1,
			IConnectedComponent cc2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ruleIsMatching(CRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

}
