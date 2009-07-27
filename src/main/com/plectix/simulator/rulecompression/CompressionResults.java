package com.plectix.simulator.rulecompression;

import java.util.*;

import com.plectix.simulator.components.CRule;

public class CompressionResults {
	private final Map<CRule, CRule> associations;
	private final RuleCompressionType type;
	
	public CompressionResults(Map<CRule, CRule> associations, RuleCompressionType type) {
		this.associations = associations;
		this.type = type;
	}

	public Set<Map.Entry<CRule, CRule>> getAssociations() {
		return associations.entrySet();
	}
	
	public Collection<CRule> getCompressedRules() {
		return associations.values();
	}
	
	public RuleCompressionType getCompressionType() {
		return type;
	}
}
