package com.plectix.simulator.staticanalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * It's specific class, used for unifying connected components.
 * @author avokhmin
 *
 */
/*package*/ final class SpanningTree {

	private final List<Integer>[] vertices;
	private final int rootIndex;
	private final boolean[] newVertex;

	@SuppressWarnings("unchecked")
	public SpanningTree(int N, Agent agent) {
		this.newVertex = new boolean[N];
		this.vertices = new ArrayList[N];
		rootIndex = agent.getIdInConnectedComponent();
		for (int i = 0; i < N; i++) {
			vertices[i] = new ArrayList<Integer>();
			newVertex[i] = true;
		}
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
		return vertices;
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
	private final void depthFirstSearch(Agent rootAgent) {
		newVertex[rootAgent.getIdInConnectedComponent()] = false;
		for (Site site : rootAgent.getSites()) {
			Site linkSite = (Site) site.getLinkState().getConnectedSite();
			if (linkSite != null) {
				Agent agent = linkSite.getParentAgent();
				Integer vertexIndex = agent.getIdInConnectedComponent();
				if (newVertex[vertexIndex]) {
					vertices[rootAgent.getIdInConnectedComponent()]
							.add(vertexIndex);
					depthFirstSearch(agent);
				}
			}
		}
	}
}
