package com.plectix.simulator.graphs;

public class Edge {
	private Vertex firstVertex;
	private Vertex lastVertex;
	
	public Edge(Vertex firstVertex, Vertex lastVertex){
		this.firstVertex = firstVertex;
		this.lastVertex = lastVertex;
	}
	
	public final Vertex getSource() {
		return firstVertex;
	}
	
	public final Vertex getTarget() {
		return lastVertex;
	}
	
	public final void setTarget(Vertex lastVertex) {
		this.lastVertex = lastVertex;
	}
	
	public final void setSource(Vertex firstTarget) {
		this.firstVertex = firstTarget;
	}
}
