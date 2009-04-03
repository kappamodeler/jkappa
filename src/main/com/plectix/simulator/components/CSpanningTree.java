package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;

/**
 * It's specific class, uses for "unify" connected component to something.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
final class CSpanningTree implements Serializable {

	private final List<Integer>[] vertexes;
	
	/**
	 * Index of agent in connected component, for which creates SpanningTree.
	 */
	private final int rootIndex;
	private final boolean[] newVertex;

	@SuppressWarnings("unchecked")
	public CSpanningTree(int N, CAgent agent) {
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

	/**
	 * This method resets checks array.
	 */
	public final void resetNewVertex() {
		for (int i = 0; i < newVertex.length; i++) {
			newVertex[i] = false;
		}
	}

	/**
	 *	This method returns value from checks array by given index. 
	 * @param index given index
	 */
	public final boolean getNewVertexElement(int index) {
		return newVertex[index];
	}

	/**
	 * This method returns root index from current SpanningTree.
	 */
	public final int getRootIndex() {
		return rootIndex;
	}

	/**
	 * This method returns list of vertex for Depth-first search.
	 */
	public final List<Integer>[] getVertexes() {
		return vertexes;
	}

	/**
	 * This method sets "index" value from checks array to <code>false</code>
	 * @param index given index
	 */
	public final void setFalse(int index) {
		newVertex[index] = false;
	}

	/**
	 * This method sets "index" value from checks array to <code>true</code>
	 * @param index given index
	 */
	public final void setTrue(int index) {
		newVertex[index] = true;
	}

	/**
	 * Depth-first search of connected component's graph. This search algorithm
	 * is used for spanning tree construction. Spanning tree is storing as a
	 * linked list. The first element of some vertexes list is always the id of
	 * this vertex (it needs in future tree usage).
	 */
	private final void depthFirstSearch(CAgent rootAgent) {
		newVertex[rootAgent.getIdInConnectedComponent()] = false;
		for (CSite site : rootAgent.getSites()) {
			CSite linkSite = (CSite) site.getLinkState().getConnectedSite();
			if (linkSite != null) {
				CAgent agent = linkSite.getAgentLink();
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
