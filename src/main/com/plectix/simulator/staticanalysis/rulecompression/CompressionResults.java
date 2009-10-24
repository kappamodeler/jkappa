package com.plectix.simulator.staticanalysis.rulecompression;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.plectix.simulator.staticanalysis.Rule;

public class CompressionResults {
	private final Map<Rule, Rule> associations;
	private final RuleCompressionType type;
	
	public CompressionResults(Map<Rule, Rule> associations, RuleCompressionType type) {
		this.associations = associations;
		this.type = type;
	}

	public Set<Map.Entry<Rule, Rule>> getAssociations() {
		return associations.entrySet();
	}
	
	public List<Rule> getCompressedRules() {
		List<Rule> list = new LinkedList<Rule>();
		for(Rule r : associations.values()){
			if(!list.contains(r)){
				list.add(r);
			}
		}
		return list;
	}
	
	public RuleCompressionType getCompressionType() {
		return type;
	}
}
