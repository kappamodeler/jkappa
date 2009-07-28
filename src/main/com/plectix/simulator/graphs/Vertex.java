package com.plectix.simulator.graphs;

import java.util.LinkedList;

public class Vertex {
	protected LinkedList<Edge> edges = null;
	protected LinkedList<Vertex> neighbors = null;
	protected Vertex parent = null;
	private boolean tag = false;
	public int explored = 0;

	public boolean mark2;

	public void setTag(boolean tag) {
		this.tag = tag;
	}

	public boolean isTag() {
		return tag;
	}

	public Vertex() {
		edges = new LinkedList<Edge>();
		neighbors = new LinkedList<Vertex>();
		explored = 0;
		mark2 = false;
	}

	public Vertex next() {

		while (explored < edges.size()) {
			if (edges.get(explored).getTarget() != this) {
				explored++;
				return edges.get(explored - 1).getTarget();
			}else
			{
				explored++;
			}
		}
		explored = 0;
		return null;

	}

}
