package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;

public class CSpanningTree {

	private List<Integer>[] vertexes;

	private int rootIndex;

	private boolean[] newVertex;

	@SuppressWarnings("unchecked")
	public CSpanningTree(int N, IAgent agent) {
		this.newVertex = new boolean[N];
		this.vertexes = new ArrayList[N];
		rootIndex = agent.getIdInConnectedComponent();
		for (int i = 0; i < N; i++) {
			vertexes[i] = new ArrayList<Integer>();
			newVertex[i] = true;
		}
		if (agent != null)
			depthFirstSearch(agent);

	}

	public final void resetNewVertex() {
		for (int i = 0; i < newVertex.length; i++) {
			newVertex[i] = false;
		}
	}

	public final boolean getNewVertexElement(int index) {
		return newVertex[index];
	}

	public final int getRootIndex() {
		return rootIndex;
	}

	public final List<Integer>[] getVertexes() {
		return vertexes;
	}

	public final void setFalse(int index) {
		newVertex[index] = false;
	}

	public final void setTrue(int index) {
		newVertex[index] = true;
	}

	/**
	 * Depth-first search of connected component's graph. This search algorithm
	 * is used for spanning tree construction. Spanning tree is storing as a
	 * linked list. The first element of some vertexes list is always the id of
	 * this vertex (it needs in future tree usage).
	 */
	private final void depthFirstSearch(IAgent rootAgent) {
		newVertex[rootAgent.getIdInConnectedComponent()] = false;
		for (ISite site : rootAgent.getSites()) {
			ISite linkSite = (CSite) site.getLinkState().getSite();
			if (linkSite != null) {
				IAgent agent = linkSite.getAgentLink();
				Integer vertexIndex = agent.getIdInConnectedComponent();
				if (newVertex[vertexIndex]) {
					vertexes[rootAgent.getIdInConnectedComponent()]
							.add(vertexIndex);
					depthFirstSearch(agent);
				}
			}
		}
	}

}
