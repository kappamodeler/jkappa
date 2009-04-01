package com.plectix.simulator.interfaces;

import java.util.List;

import com.plectix.simulator.components.CRule;

public interface IActivationMap {

	public List<CRule> getActivateRules(CRule rule);
	
}
