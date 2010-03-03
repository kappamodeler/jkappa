package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.staticanalysis.Rule;

/**
 * TODO sooner or later we will use this class, so then we should put it in util package 
 * or something like that 
 * @author evlasov
 *
 */
public class RulePattern implements Pattern<Rule> {
	private final String ruleStringRepresentation;
	
	public RulePattern(String template) {
		this.ruleStringRepresentation = template;
	}
	
	public RulePattern(Rule rule) {
		if (rule == null) {
			this.ruleStringRepresentation = "";
		} else {
			this.ruleStringRepresentation = rule.getCanonicalRuleString();
		}
	}
	
	public boolean matches(Rule rule) {
		if (rule == null) {
			return false;
		}
		return ruleStringRepresentation.equals(rule.getCanonicalRuleString());
	}
	
	public boolean matches(String componentRepresentation) {
		return ruleStringRepresentation.equals(componentRepresentation);
	}

	public boolean matches(ModelRule modelRule) {
		if (modelRule == null) {
			return false;
		} else {
			return modelRule.toString().endsWith(ruleStringRepresentation);
		}
	}
	
	@Override
	public String toString() {
		return ruleStringRepresentation;
	}
}
