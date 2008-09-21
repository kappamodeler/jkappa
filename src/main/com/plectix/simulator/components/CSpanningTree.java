package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CSpanningTree {

	private List<Integer>[] vertexes;

	private int rootIndex;

	private boolean[] newVertex;
	
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

	public final boolean getNewVertexElement(int index){
		return newVertex[index];
	}
	
	public final int getRootIndex(){
		return rootIndex;
	}
	
	public final List<Integer>[] getVertexes() {
		return vertexes;
	}

	public final void setFalse(int index){
		newVertex[index]=false;
	}

	// TODO: Document
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
