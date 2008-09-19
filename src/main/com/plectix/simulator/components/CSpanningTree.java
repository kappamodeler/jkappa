package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CSpanningTree {

	private List<Integer>[] vertexes;

	public List<Integer>[] getVertexes() {
		return vertexes;
	}

	private int rootIndex;
	
	public int getRootIndex(){
		return rootIndex;
	}
	
	private boolean[] newVertex;

	public void setFalse(int index){
		newVertex[index]=false;
	}
	
	public boolean getNewVertexElement(int index){
		return newVertex[index];
	}
	
	public CSpanningTree() {
	}

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
			WGD(agent);
	}

	private void WGD(CAgent rootAgent) {
		newVertex[rootAgent.getIdInConnectedComponent()] = false;
		vertexes[rootAgent.getIdInConnectedComponent()].add(rootAgent.getIdInConnectedComponent());
		for (CSite site : rootAgent.getSites()) {
			CSite linkSite = (CSite)site.getLinkState().getSite();
			if (linkSite!=null){
			CAgent agent = linkSite.getAgentLink();
			//if (agent != null) {
				Integer vertexIndex = agent.getIdInConnectedComponent();
				if (newVertex[vertexIndex]) {
					vertexes[rootAgent.getIdInConnectedComponent()].add(vertexIndex);
					WGD(agent);
				}
			}
		}
	}

}
