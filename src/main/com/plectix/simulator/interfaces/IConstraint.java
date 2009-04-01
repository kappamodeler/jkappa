package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CRule;

public interface IConstraint {
	/**
	 * 
	 * @param rule x
	 * @return true, if this constraint is applicable for the rule x
	 * and false, otherwise 
	 */
	public boolean ruleIsMatching(CRule rule);
	
	/**
	 * 
	 * @param rule x
	 * @param cc1
	 * @param cc2
	 * @return true, if rule x can be applied to cc1 and cc2, according to this constraint
	 */
	public boolean acceptRule(CRule rule, IConnectedComponent cc1, IConnectedComponent cc2);
	
}
