package com.plectix.simulator.components.constraints;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IConstraint;
import com.plectix.simulator.interfaces.IRule;

/*package*/ abstract class Constraint implements IConstraint {

	@Override
	public boolean acceptRule(IRule rule, IConnectedComponent cc1, IConnectedComponent cc2) {
		if (!ruleIsMatching(rule)) {
			return false;
		} else {
			return acceptMatchingRule(rule, cc1, cc2);
		}
	}

	@Override
	public abstract boolean ruleIsMatching(IRule rule);

	protected abstract boolean acceptMatchingRule(IRule rule, IConnectedComponent cc1, IConnectedComponent cc2);
}
