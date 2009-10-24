package com.plectix.simulator.staticanalysis.subviews;

import java.util.LinkedHashSet;

import com.plectix.simulator.staticanalysis.graphs.Vertex;

public final class SubViewClass extends Vertex {
	private final String agentName;
	private final LinkedHashSet<String> sitesNames = new LinkedHashSet<String>();
	private final LinkedHashSet<Integer> rulesId = new LinkedHashSet<Integer>();

	public SubViewClass(String agentName) {
		this.agentName = agentName;
	}

	public final String getAgentType() {
		return agentName;
	}

	public final LinkedHashSet<String> getSitesNames() {
		return sitesNames;
	}

	public final LinkedHashSet<Integer> getRulesId() {
		return rulesId;
	}

	public final void addSite(String siteName) {
		sitesNames.add(siteName);
	}

	public final int hashCode() {
		return sitesNames.hashCode();
	}

	public final boolean hasSite(String siteName) {
		return sitesNames.contains(siteName);
	}

	public final void addRuleId(int rule) {
		rulesId.add(rule);
	}

	public final void addRulesId(LinkedHashSet<Integer> rulesId2) {
		rulesId.addAll(rulesId2);
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer(agentName);
		sb.append(" ");
		for (String siteName : sitesNames)
			sb.append(siteName + " ");
		sb.append(" Rules:'");
		for (Integer id : rulesId)
			sb.append(id + " ");
		sb.append("'");
		return sb.toString();
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof SubViewClass))
			return false;
		SubViewClass inClass = (SubViewClass) obj;

		if (!agentName.equals(inClass.agentName))
			return false;
		if (!sitesNames.equals(inClass.sitesNames))
			return false;
		return true;
	}
}
