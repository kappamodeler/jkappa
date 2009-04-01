package com.plectix.simulator.components.contactMap;

import java.util.*;


import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.ISite;

public class CContactMapEdge {
	private ISite vertexFrom;
	public ISite getVertexFrom() {
		return vertexFrom;
	}

	public void setVertexFrom(ISite vertexFrom) {
		this.vertexFrom = vertexFrom;
	}

	public ISite getVertexTo() {
		return vertexTo;
	}

	public void setVertexTo(ISite vertexTo) {
		this.vertexTo = vertexTo;
	}

	public List<Integer> getRules() {
		return rules;
	}

	private ISite vertexTo;
	private List<Integer> rules;

	public CContactMapEdge(ISite vertexFrom, ISite vertexTo) {
		this.vertexFrom = vertexFrom;
		this.vertexTo = vertexTo;
		rules = new ArrayList<Integer>();
	}

	public void clearRules(){
		rules.clear();
	}
	
	public void addRules(CRule rule) {
		if (rule != null) {
			int value = rule.getRuleID();
			if (!rules.contains(value))
				rules.add(value);
		}
	}
}
