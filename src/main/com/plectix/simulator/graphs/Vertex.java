package com.plectix.simulator.graphs;

import java.util.LinkedList;
import java.util.List;

public class Vertex {
	protected LinkedList<Edge> edges = null;
	protected LinkedList<Vertex> neighbors = null;
	protected Vertex parent = null;
	private boolean tag = false;

	public void setTag(boolean tag) {
		this.tag = tag;
	}

	public boolean isTag() {
		return tag;
	}

	public Vertex() {
		edges = new LinkedList<Edge>();
		neighbors = new LinkedList<Vertex>();
	}

}
