package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CSpanningTree {

	private List<Integer>[] vertexes;

	public List<Integer>[] getVertexes() {
		return vertexes;
	}

	private boolean[] newVertex;

	public CSpanningTree() {
	}

	@SuppressWarnings("unchecked")
	public CSpanningTree(int N, CAgent agent) {
		this.newVertex = new boolean[N];
		this.vertexes = new ArrayList[N];
		for (int i = 0; i < N; i++) {
			vertexes[i] = new ArrayList<Integer>();
			newVertex[i] = true;
		}
		if ((agent != null) && (N>1))
			WGD(agent);
	}

	private void WGD(CAgent rootAgent) {
		newVertex[rootAgent.getIdInConnectedComponent()] = false;
		for (CSite site : rootAgent.getSites()) {
			CAgent agent = site.getAgentLink();
			if (agent != null) {
				Integer vertexIndex = agent.getIdInConnectedComponent();
				if (newVertex[vertexIndex]) {
					vertexes[rootAgent.getIdInConnectedComponent()].add(vertexIndex);
					WGD(agent);
				}
			}
		}
	}

}
