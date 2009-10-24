package com.plectix.simulator.staticanalysis.contactmap;

import java.util.LinkedHashSet;
import java.util.Set;

import com.plectix.simulator.staticanalysis.abstracting.AbstractLinkState;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;

public final class ContactMapAbstractEdge {
	private AbstractSite sourceVertex;
	private final String targetVertexSiteName;
	private final String targetVertexAgentName;
	private final Set<Integer> rules;

	public ContactMapAbstractEdge(AbstractSite sourceVertex) {
		this.sourceVertex = sourceVertex;
		AbstractLinkState ls = sourceVertex.getLinkState();
		this.targetVertexAgentName = ls.getAgentName();
		this.targetVertexSiteName = ls.getConnectedSiteName();
		this.rules = new LinkedHashSet<Integer>();
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

	public final Set<Integer> getRules() {
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
