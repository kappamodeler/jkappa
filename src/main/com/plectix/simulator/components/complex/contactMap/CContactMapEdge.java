package com.plectix.simulator.components.complex.contactMap;

import java.util.*;


import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;

public class CContactMapEdge {
	private CSite vertexFrom;
	public CSite getVertexFrom() {
		return vertexFrom;
	}

	public void setVertexFrom(CSite vertexFrom) {
		this.vertexFrom = vertexFrom;
	}

	public CSite getVertexTo() {
		return vertexTo;
	}

	public void setVertexTo(CSite vertexTo) {
		this.vertexTo = vertexTo;
	}

	public List<Integer> getRules() {
		return rules;
	}

	private CSite vertexTo;
	private List<Integer> rules;

	public CContactMapEdge(CSite vertexFrom, CSite vertexTo) {
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
