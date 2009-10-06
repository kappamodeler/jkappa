package com.plectix.simulator.component.complex.contactmap;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.component.complex.abstracting.AbstractLinkState;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;

public final class ContactMapAbstractEdge {
	private AbstractSite sourceVertex;
	private final String targetVertexSiteName;
	private final String targetVertexAgentName;
	private final List<Integer> rules = new ArrayList<Integer>();

	public ContactMapAbstractEdge(AbstractSite sourceVertex) {
		this.sourceVertex = sourceVertex;
		AbstractLinkState ls = sourceVertex.getLinkState();
		this.targetVertexAgentName = ls.getAgentName();
		this.targetVertexSiteName = ls.getConnectedSiteName();
	}
	
	public final AbstractSite getSourceVertex() {
		return sourceVertex;
	}

	public final String getTargetVertexSiteName() {
		return targetVertexSiteName;
	}

	public final String getTargetVertexAgentName() {
		return targetVertexAgentName;
	}

	public final List<Integer> getRules() {
		return rules;
	}

	public final boolean equalz(ContactMapAbstractEdge edge){
		if (this == edge) {
			return true;
		}

		if (edge == null) {
			return false;
		}

		if (!this.sourceVertex.hasSimilarName(edge.getSourceVertex()))
			return false;

		if (!this.targetVertexAgentName.equals(edge.targetVertexAgentName))
			return false;

		if (!this.targetVertexSiteName.equals(edge.targetVertexSiteName))
			return false;

		return true;
	}
	
	public final void addRules(int ruleId){
		rules.add(ruleId);
	}
}
