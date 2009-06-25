package com.plectix.simulator.graphs;

public class Edge {
	private Vertex first = null;
	private Vertex end = null;
	
	public void setSource(Vertex first) {
		this.first = first;
	}
	public Vertex getSource() {
		return first;
	}
	public void setTarget(Vertex end) {
		this.end = end;
	}
	public Vertex getTarget() {
		return end;
	}
	
	public Edge(Vertex v1, Vertex v2){
		first = v1;
		end = v2;
	}

}
