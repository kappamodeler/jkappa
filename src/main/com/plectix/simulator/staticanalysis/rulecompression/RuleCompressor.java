package com.plectix.simulator.staticanalysis.rulecompression;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;

public class RuleCompressor {
	private final LocalViewsMain localViews;
	private final RuleCompressionType type;
	
	public RuleCompressor(RuleCompressionType type,LocalViewsMain lViews) {
		this.type = type;
		this.localViews = lViews;
	}
	
	public CompressionResults compress(Collection<Rule> rules) {
		Map<Rule, Rule> answer = null;
		if (type == RuleCompressionType.QUANTITATIVE) {
			answer = compressQuantity(rules); 
		} else {
			answer = compressQuality(rules);
		}
		CompressionResults results = new CompressionResults(answer, type);
		return results;
	}
	/**
	 * <b>Qualitative mode</b>
	 * method groups rule, for each group[R1,R2...] create one rule R <br>
	 * with the next property :<br>
	 * each embedding R to solution is embedding of some Ri
	 * each embedding Ri to solution is embedding of R
	 * 
	 * @param rules
	 * @return
	 */
	protected Map<Rule,Rule> compressQuality(Collection<Rule> rules){
		Map<Rule,Rule> answer = new LinkedHashMap<Rule, Rule>();
		QualitativeCompressor compressor = new QualitativeCompressor(localViews);
		compressor.buildGroups(rules);
		compressor.setLocalViews();
		compressor.compressGroups();
		for(Rule r : rules){
			answer.put(r, compressor.getCompressedRule(r));
			//answer.put(r, r);
		}
		LinkedHashSet<Rule> sets = new LinkedHashSet<Rule>(); 
		int i=1;
		
		for(Rule r : answer.values()){
			if(!sets.contains(r)){
				r.setRuleID(i);
				sets.add(r);
				i++;
			}
		}
		return answer;
	}

	/**
	 * <b>Quantitative mode</b>
	 * method decontextualize rules <br>
	 * (decrease information which rule test before applying) 
	 * @param rules
	 * @return
	 */
	protected Map<Rule, Rule> compressQuantity(Collection<Rule> rules) {
		Map<Rule, Rule> associationMap = new LinkedHashMap<Rule, Rule>();
		int id = 1;
		for (Rule rule : rules) {
			QuantitativeCompressor qc = new QuantitativeCompressor(localViews);
			qc.compress(rule);
			Rule compressedRule = qc.getCompressedRule();
			compressedRule.setRuleID(id++);
			associationMap.put(rule, compressedRule);
		}
		return associationMap;
	}
	
	
}
