package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * It's specific class, used for unifying connected components.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
final class CSpanningTree implements Serializable {

	private final List<Integer>[] vertexes;
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
	 * This method resets values in array, keeping info on new vertexes
	 */
	public final void resetNewVertex() {
		for (int i = 0; i < newVertex.length; i++) {
			newVertex[i] = false;
		}
	}

	/**
	 * This method returns true if there is new vertex with given number, and false if there's no vertex
	 * @param index given number
	 * @return <tt>true</tt> if there is new vertex with given number, and <tt>false</tt> if there's no vertex
	 */
	public final boolean getNewVertexElement(int index) {
		return newVertex[index];
	}

	/**
	 * This method returns root index of the current SpanningTree.
	 * @return root index of the current SpanningTree
	 */
	public final int getRootIndex() {
		return rootIndex;
	}

	/**
	 * This method returns list of vertexes for Depth-first search.
	 * @return list of vertexes for Depth-first search.
	 */
	public final List<Integer>[] getVertexes() {
		return vertexes;
	}

	/**
	 * This method sets value with index number from checks array to false
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
	 * is used for spanning tree construction. The first element of vertexes 
	 * list is always the id of this vertex (we need it in future tree usage).
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
