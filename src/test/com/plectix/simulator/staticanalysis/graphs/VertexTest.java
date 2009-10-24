package com.plectix.simulator.staticanalysis.graphs;


public class VertexTest extends Vertex {
	private int id;

	public VertexTest(int i) {
		id = i;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
