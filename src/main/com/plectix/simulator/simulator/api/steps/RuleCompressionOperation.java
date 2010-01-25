package com.plectix.simulator.simulator.api.steps;

import java.util.List;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;

public class RuleCompressionOperation extends AbstractOperation<List<Rule>> {
	private final KappaSystem kappaSystem;
	private final RuleCompressionType type;
	
	public RuleCompressionOperation(KappaSystem kappaSystem, RuleCompressionType type) {
		super(kappaSystem.getSimulationData(), OperationType.RULE_COMPRESSION);
		this.kappaSystem = kappaSystem;
		this.type = type;
	}
	
	protected List<Rule> performDry() {
		return kappaSystem.compressRules(type, kappaSystem.getRules());
	}

}
