package com.plectix.simulator.interfaces;

import java.util.List;

import com.plectix.simulator.component.Rule;

public interface ActivationMapInterface {
	public List<Rule> getActivateRules(Rule rule);
}
