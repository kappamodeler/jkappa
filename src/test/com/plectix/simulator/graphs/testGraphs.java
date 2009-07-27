package com.plectix.simulator.graphs;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import org.junit.Before;

import org.junit.Test;

public class testGraphs {

	private Graph tested = null;

	@Before
	public void setUp() throws Exception {
		tested = new Graph();

		for (int i = 0; i < 5; i++) {
			testVertex newVertex = new testVertex(i);
			tested.addVertex(newVertex);
		}

		for (int i = 0; i < 4; i++) {
			testEdge newEdge = new testEdge(tested.getVertices().get(i), tested
					.getVertices().get(i + 1));
			tested.addEdge(newEdge);
		}

	}

	@Test
	public void testStructure() {

		if (tested.numberOfVertices != 5)
			fail("bad number of vertices");

	}

	@Test
	public void testWeakComponent1() {
		if (tested.getAllWeakClosureComponent().size() != 1)
			fail("weak component :(");
		

	}
	@Test
	public void testWeakComponent2() {
		testVertex added = new testVertex(5);
		tested.addVertex(added);
		testEdge addEdge = new testEdge(added, tested.getVertices().get(1));
		tested.addEdge(addEdge);
		if (tested.getAllWeakClosureComponent().size() !=2 )
			fail("weak component :(");
	}
	

	@Test
	public void testDetectCycles0() {
		Graph g = new Graph();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Edge e = new Edge(v1,v2);
		g.addVertex(v2);
		g.addVertex(v1);
		g.addEdge(e);

		if(!g.getAllEdgesInDirectedCycles().isEmpty())
			fail("bad cycles :(");
		
	}

	@Test
	public void testDetectCycles1() {
		Graph g = new Graph();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Edge e1 = new Edge(v1,v2);
		Edge e2 = new Edge(v2,v1);
		g.addVertex(v2);
		g.addVertex(v1);
		g.addEdge(e1);
		g.addEdge(e2);

		assertTrue(g.getAllEdgesInDirectedCycles().size()==2);
		
	}

	
	@Test
	public void testDetectCycles2() {
		Graph g = new Graph();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Vertex v3 = new Vertex();
		Edge e1 = new Edge(v1,v2);
		Edge e2 = new Edge(v2,v3);
		g.addVertex(v3);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addEdge(e2);
		g.addEdge(e1);

		if(!g.getAllEdgesInDirectedCycles().isEmpty())
			fail("bad cycles :(");
		
	}
	
	@Test
	public void testDetectCycles3() {
		Graph g = new Graph();
		Vertex v1 = new testVertex(1);
		Vertex v2 = new testVertex(2);
		Vertex v3 = new testVertex(3);
		Edge e1 = new Edge(v1,v2);
		Edge e2 = new Edge(v2,v3);

		Edge e3 = new Edge(v3,v1);
		g.addVertex(v3);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addEdge(e2);
		g.addEdge(e1);
		g.addEdge(e3);

		assertTrue(g.getAllEdgesInDirectedCycles().size()==3);
		
	}

	@Test
	public void testDetectCycles4() {
		Graph g = new Graph();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Vertex v3 = new Vertex();
		Edge e1 = new Edge(v1,v2);
		Edge e2 = new Edge(v2,v3);

		Edge e3 = new Edge(v3,v1);
		g.addVertex(v3);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addEdge(e2);
		g.addEdge(e1);
		g.addEdge(e3);
		g.addEdge(e2);
		g.addEdge(e1);
		g.addEdge(e3);

		assertTrue(g.getAllEdgesInDirectedCycles().size()==3);
		
	}
	
	
	@Test
	public void testDetectCycles5() {
		Graph g = new Graph();
		Vertex v1 = new testVertex(1);
		Vertex v2 = new testVertex(2);
		Vertex v3 = new testVertex(3);

		Vertex v4 = new testVertex(4);
		Edge e1 = new Edge(v1,v2);
		Edge e2 = new Edge(v2,v3);
		Edge e3 = new Edge(v3,v1);
		Edge e4 = new Edge(v1,v4);
		Edge e5 = new Edge(v4,v3);

		g.addVertex(v2);
		g.addVertex(v4);
		g.addVertex(v3);
		g.addVertex(v1);
		g.addEdge(e2);
		g.addEdge(e1);
		g.addEdge(e3);
		g.addEdge(e2);
		g.addEdge(e1);
		g.addEdge(e3);
		g.addEdge(e5);
		g.addEdge(e4);

		assertTrue(g.getAllEdgesInDirectedCycles().size()==5);
		
	}
		


}
