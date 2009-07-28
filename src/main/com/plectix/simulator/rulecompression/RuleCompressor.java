package com.plectix.simulator.rulecompression;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.localviews.CLocalViewsMain;
import com.plectix.simulator.components.complex.subviews.CMainSubViews;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaFileReader;
import com.plectix.simulator.parser.KappaSystemParser;
import com.plectix.simulator.rulecompression.util.ExpChoiceTree;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;

public class RuleCompressor {
	private final KappaSystem ks;
	private final CLocalViewsMain localViews;
	private final RuleCompressionType type;
	
	public RuleCompressor(RuleCompressionType type, KappaSystem ks) {
		this.ks = ks;
		this.type = type;
		CLocalViewsMain lViews = ks.getLocalViews();
		if(lViews == null){
			CMainSubViews sViews = new CMainSubViews();
			sViews.build(ks.getSolution(), ks.getRules());
			lViews = new CLocalViewsMain(sViews);
			lViews.buildLocalViews();
		}
		this.localViews = lViews;
	}
	
	public CompressionResults compress(Collection<CRule> rules) {
		Map<CRule, CRule> answer = null;
		if (type == RuleCompressionType.QUANTITATIVE) {
			answer = compressQuantity(rules); 
		} else {
			answer = compressQuality(rules);
		}
		CompressionResults results = new CompressionResults(answer, type);
		return results;
	}

	/**
	 * This method divides all rules from the given collection to some groups and tries
	 * to perform compression on each group  
	 * @param rules given collection of rules
	 * @return new collection of rules
	 */
	private Map<CRule, CRule> compressQuantity(Collection<CRule> rules) {
		Map<CRule, CRule> answer = new LinkedHashMap<CRule, CRule>();
		NewRuleBuilder ruleBuilder = new NewRuleBuilder(ks);
		
		Map<CRule, RootedRulesGroup> groups = new LinkedHashMap<CRule, RootedRulesGroup>();
		for (CRule rule : rules) {
			addRuleToGroup(groups, rule);
		}
		
		
		for (RootedRulesGroup group : groups.values()) {
			CRule compressedRule = group.getCompressedCandidate(ruleBuilder);
			for (CRule rule : group.getMatchedRules()) {
				answer.put(rule, compressedRule);
			}
		}
		return answer;
	}

	/**
	 * Qualitative mode compression method
	 * @param rules
	 * @return
	 */
	private Map<CRule, CRule> compressQuality(Collection<CRule> rules) {
		// TODO Auto-generated method stub
		Map<CRule, CRule> associationMap = new LinkedHashMap<CRule, CRule>();
		int id = 1;
		for (CRule rule : rules) {
			QualitativeCompressor qc = new QualitativeCompressor(localViews);
			qc.compress(rule);
			CRule compressedRule = qc.getCompressedRule();
			compressedRule.setRuleID(id++);
			associationMap.put(rule, compressedRule);
		}
		return associationMap;
	}
	
	private RootedRulesGroup addRuleToGroup(Map<CRule, RootedRulesGroup> groups, CRule rule) {
		Set<RootedRule> rootedVersions = this.createAllPossibleRootedVersions(rule);
		RootedRulesGroup rulesGroup = null;
		for (RootedRule rr : rootedVersions) {
			if (groups.isEmpty()) {
				RootedRulesGroup firstGroup = new RootedRulesGroup(rr);
				groups.put(rule, firstGroup);
				continue;
			}
			for (RootedRulesGroup group : groups.values()) {
				rulesGroup = group.tryAdd(rr);
				// it can be already existing group
				if (groups.containsValue(rulesGroup)) {
					return rulesGroup;
				}
			}
 		}
		if (rulesGroup != null) {
			groups.put(rule, rulesGroup);	
		}
		return rulesGroup;
	}
	
	private Set<RootedRule> createAllPossibleRootedVersions(CRule rule) {
		Set<RootedRule> set = new HashSet<RootedRule>();
		RuleAnalyzer analyzer = new RuleAnalyzer(rule);
		
		ExpChoiceTree<CAgent> rootCombinationsTree = new ExpChoiceTree<CAgent>();
		List<CAgent> valueList = new ArrayList<CAgent>();
		for (IConnectedComponent component : rule.getLeftHandSide()) {
			if (!valueList.isEmpty()) {
				rootCombinationsTree.addNextFloor(valueList, false);
				valueList.clear();
			}
			for (CAgent agent : component.getAgents()) {
				if (analyzer.canBeRoot(agent)) {
					valueList.add(agent);
				}
			}
		}
		rootCombinationsTree.addNextFloor(valueList, true);
		Collection<Collection<CAgent>> allPossibleRootsCombinations = rootCombinationsTree.getAllPaths();
		for (Collection<CAgent> rootsCombination : allPossibleRootsCombinations) {
			set.add(new RootedRule(ks, rule, rootsCombination));
		}
		return set;
	}
	
	public KappaSystem getKappaSystem() {
		return ks;
	}
	
	public static void main(String[] er) {
		try {
			String f = File.separator;
			KappaFileReader kappaFileReader = new KappaFileReader("src" + f
					+ "main" + f + "com" + f + "plectix" + f + "simulator" + f + "rulecompression" 
					+ f + "test2.ka");

			KappaFile kappaFile = kappaFileReader.parse();

			SimulationData dt = new SimulationData();
			KappaSystemParser parser = new KappaSystemParser(kappaFile, dt);
			// parser.setForwarding(simulationArguments.isForwardOnly());
			parser.parse(null);
			
			new RuleCompressor(RuleCompressionType.QUANTITATIVE, dt.getKappaSystem())
					.compressQuantity(dt.getKappaSystem().getRules());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
