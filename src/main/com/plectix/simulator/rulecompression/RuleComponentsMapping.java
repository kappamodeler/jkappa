package com.plectix.simulator.rulecompression;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.plectix.simulator.components.CAgent;

/**
 * This class describes some kind of map.
 * Each connected component in rooted rule has root.
 * Each action has one or two roots and amount of different sites, 
 * which should be described in terms of roots and paths.
 * <br><br>
 * In order to gather information about all sites mentioned in actions,
 * we group it by connected components, so as by its' roots.
 * @author evlasov
 *
 */
/*package*/ class RuleComponentsMapping {
	private Map<CAgent, CommonPartFinder> allSubstancesInfo = new LinkedHashMap<CAgent, CommonPartFinder>();
	
	public CommonPartFinder getStorage(CAgent root) {
		return allSubstancesInfo.get(root);
	}
	
	public void addEntry(CAgent root, CommonPartFinder finder) {
		allSubstancesInfo.put(root, finder);
	}

	public void join(RuleComponentsMapping otherMapping) {
		allSubstancesInfo.putAll(otherMapping.allSubstancesInfo);
	}
	
	public Collection<CAgent> gatherAgents() {
		Set<CAgent> allAgents = new LinkedHashSet<CAgent>();
		for (CommonPartFinder cpf : allSubstancesInfo.values()) {
			allAgents.addAll(cpf.getAgents());
		}
		return allAgents;
	}
	
	public String toString() {
		return allSubstancesInfo + "";
	}
}
