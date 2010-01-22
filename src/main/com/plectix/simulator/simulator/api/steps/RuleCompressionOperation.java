package com.plectix.simulator.simulator.api.steps;

import java.util.List;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;

public class RuleCompressionOperation extends AbstractOperation {

	public RuleCompressionOperation() {
		super(OperationType.RULE_COMPRESSION);
	}
	
	public List<Rule> perform(KappaSystem kappaSystem, RuleCompressionType type) {
		return kappaSystem.compressRules(type, kappaSystem.getRules());
	}

}
