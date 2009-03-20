package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ISolution;

public abstract class CommonRuleApplicationPool extends RuleApplicationPool {
	private StandardRuleApplicationPool myStandardPools; 
	private TransparentRuleApplicationPool myTransparentPools;
	
	public CommonRuleApplicationPool() {
	}
	
	public abstract void apply();
}
