package com.plectix.simulator.components.constraints;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.IConnectedComponent;


public class NoPolyConstraint extends Constraint {

	@Override
	protected boolean acceptMatchingRule(CRule rule, IConnectedComponent cc1,
			IConnectedComponent cc2) {
		return ConstraintsUtil.agentsNamesAreNotIntersected(cc1, cc2);
	}

	@Override
	public boolean ruleIsMatching(CRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

}
