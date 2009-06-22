package com.plectix.simulator.components.complex.subviews.mock;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;

class MockEdge {
	private CAbstractSite vertexFrom;
	private int vertexToSiteNameID;
	private int vertexToAgentNameID;
	private List<Integer> rules;

	public CAbstractSite getVertexFrom() {
		return vertexFrom;
	}

	public void setVertexFrom(CAbstractSite vertexFrom) {
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

	public MockEdge(CAbstractSite vertexFrom) {
		this.vertexFrom = vertexFrom;
		CAbstractLinkState ls = vertexFrom.getLinkState();
		this.vertexToAgentNameID = ls.getAgentNameID();
		this.vertexToSiteNameID = ls.getLinkSiteNameID();
		rules = new ArrayList<Integer>();
	}

	public void clearRules() {
		rules.clear();
	}

	public final boolean equalz(MockEdge edge){
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
	
	
	public void addRules(MockRule rule) {
		if (rule != null) {
			int value = rule.getRule().getRuleID();
			if (!rules.contains(value))
				rules.add(value);
		}
	}
}
