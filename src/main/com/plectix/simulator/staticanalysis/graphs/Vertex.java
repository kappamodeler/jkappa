package com.plectix.simulator.staticanalysis.graphs;

import java.util.LinkedList;
import java.util.List;

public class Vertex {
	private final LinkedList<Edge> edges;
	private final LinkedList<Vertex> neighbourVertices = new LinkedList<Vertex>();
	private Vertex parentVertex;
	private boolean tag = false;
	//TODO appropriate names!!
	private int explored = 0;
	private boolean mark2;

	public Vertex() {
		edges = new LinkedList<Edge>();
		setExplored(0);
		setMark2(false);
	}
	
	public final void setTag(boolean tag) {
		this.tag = tag;
	}

	public final boolean isTag() {
		return tag;
	}

	public final Vertex next() {
		while (getExplored() < edges.size()) {
			if (edges.get(getExplored()).getTarget() != this) {
				setExplored(getExplored() + 1);
				return edges.get(getExplored() - 1).getTarget();
			}else
			{
				setExplored(getExplored() + 1);
			}
		}
		setExplored(0);
		return null;

	}

	public final void setParentVertex(Vertex parentVertex) {
		this.parentVertex = parentVertex;
	}

	public final Vertex getParentVertex() {
		return this.parentVertex;
	}

	public final void setMark2(boolean mark2) {
		this.mark2 = mark2;
	}

	public final boolean isMark2() {
		return mark2;
	}
	
	public final void removeEdge(Edge edge) { 
		edges.remove(edge);
	}

	public final void addEdge(Edge edge) {
		edges.add(edge);
	}
	
	public final void clearEdges() {
		edges.clear();
	}
	
	public final void setExplored(int explored) {
		this.explored = explored;
	}

	final int getExplored() {
		return explored;
	}

	public final List<Edge> getEdges() {
		return edges;
	}

	public final LinkedList<Vertex> getNeighbourVertices() {
		return neighbourVertices;
	}
	
	public final void removeNeighbour(Vertex neighbourVertex) {
		neighbourVertices.remove(neighbourVertex);
	}

	public void addNeighbourVertex(Vertex neighbourVertex) {
		neighbourVertices.add(neighbourVertex);
	}
}
