package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;

public class CContactMapAbstractEdge {
	private CContactMapAbstractSite vertexFrom;
	private int vertexToSiteNameID;
	private int vertexToAgentNameID;
	private List<Integer> rules;

	public CContactMapAbstractSite getVertexFrom() {
		return vertexFrom;
	}

	public void setVertexFrom(CContactMapAbstractSite vertexFrom) {
		this.vertexFrom = vertexFrom;
	}

	public int getVertexToSiteNameID() {
		return vertexToSiteNameID;
	}

	public int getVertexToAgentNameID() {
		return vertexToAgentNameID;
	}

	public List<Integer> getRules() {
		return rules;
	}

	public CContactMapAbstractEdge(CContactMapAbstractSite vertexFrom) {
		this.vertexFrom = vertexFrom;
		CContactMapLinkState ls = vertexFrom.getLinkState();
		this.vertexToAgentNameID = ls.getAgentNameID();
		this.vertexToSiteNameID = ls.getLinkSiteNameID();
		rules = new ArrayList<Integer>();
	}

	public void clearRules() {
		rules.clear();
	}

	public final boolean equalz(CContactMapAbstractEdge edge){
		if (this == edge) {
			return true;
		}

		if (edge == null) {
			return false;
		}

		if(this.vertexFrom.getNameId()!=edge.getVertexFrom().getNameId())
			return false;

		if(this.vertexToAgentNameID !=edge.getVertexToAgentNameID())
			return false;

		if(this.vertexToSiteNameID!=edge.getVertexToSiteNameID())
			return false;

		return true;
	}
	
	
	public void addRules(CContactMapAbstractRule rule) {
		if (rule != null) {
			int value = rule.getRule().getRuleID();
			if (!rules.contains(value))
				rules.add(value);
		}
	}
}
