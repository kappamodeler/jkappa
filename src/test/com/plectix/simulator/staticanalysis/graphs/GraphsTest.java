package com.plectix.simulator.staticanalysis.graphs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class GraphsTest {

	private Graph tested = null;

	@Before
	public void setUp() throws Exception {
		tested = new Graph();

		for (int i = 0; i < 5; i++) {
			VertexTest newVertex = new VertexTest(i);
			tested.addVertex(newVertex);
		}

		for (int i = 0; i < 4; i++) {
			EdgeTest newEdge = new EdgeTest(tested.getVertices().get(i), tested
					.getVertices().get(i + 1));
			tested.addEdge(newEdge);
		}

	}

	@Test
	public void testStructure() {
		if (tested.getVertices().size() != 5)
			fail("bad number of vertices");
	}

	@Test
	public void testWeakComponent1() {
		if (tested.getAllWeakClosureComponent().size() != 1)
			fail("weak component :(");

	}

	@Test
	public void testWeakComponent2() {
		VertexTest added = new VertexTest(5);
		tested.addVertex(added);
		EdgeTest addEdge = new EdgeTest(added, tested.getVertices().get(1));
		tested.addEdge(addEdge);
		if (tested.getAllWeakClosureComponent().size() != 2)
			fail("weak component :(");
	}

	@Test
	public void testDetectCycles0() {
		Graph g = new Graph();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Edge e = new Edge(v1, v2);
		g.addVertex(v2);
		g.addVertex(v1);
		g.addEdge(e);

		if (!g.getAllEdgesInDirectedCycles().isEmpty())
			fail("bad cycles :(");

	}

	@Test
	public void testDetectCycles1() {
		Graph g = new Graph();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Edge e1 = new Edge(v1, v2);
		Edge e2 = new Edge(v2, v1);
		g.addVertex(v2);
		g.addVertex(v1);
		g.addEdge(e1);
		g.addEdge(e2);

		assertTrue(g.getAllEdgesInDirectedCycles().size() == 2);

	}

	@Test
	public void testDetectCycles2() {
		Graph g = new Graph();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Vertex v3 = new Vertex();
		Edge e1 = new Edge(v1, v2);
		Edge e2 = new Edge(v2, v3);
		g.addVertex(v3);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addEdge(e2);
		g.addEdge(e1);

		if (!g.getAllEdgesInDirectedCycles().isEmpty())
			fail("bad cycles :(");

	}

	@Test
	public void testDetectCycles3() {
		Graph g = new Graph();
		Vertex v1 = new VertexTest(1);
		Vertex v2 = new VertexTest(2);
		Vertex v3 = new VertexTest(3);
		Edge e1 = new Edge(v1, v2);
		Edge e2 = new Edge(v2, v3);

		Edge e3 = new Edge(v3, v1);
		g.addVertex(v3);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addEdge(e2);
		g.addEdge(e1);
		g.addEdge(e3);

		assertTrue(g.getAllEdgesInDirectedCycles().size() == 3);

	}

	@Test
	public void testDetectCycles4() {
		Graph g = new Graph();
		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		Vertex v3 = new Vertex();
		Edge e1 = new Edge(v1, v2);
		Edge e2 = new Edge(v2, v3);

		Edge e3 = new Edge(v3, v1);
		g.addVertex(v3);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addEdge(e2);
		g.addEdge(e1);
		g.addEdge(e3);
		g.addEdge(e2);
		g.addEdge(e1);
		g.addEdge(e3);

		assertTrue(g.getAllEdgesInDirectedCycles().size() == 3);

	}

	@Test
	public void testDetectCycles5() {
		Graph g = new Graph();
		Vertex v1 = new VertexTest(1);
		Vertex v2 = new VertexTest(2);
		Vertex v3 = new VertexTest(3);

		Vertex v4 = new VertexTest(4);
		Edge e1 = new Edge(v1, v2);
		Edge e2 = new Edge(v2, v3);
		Edge e3 = new Edge(v3, v1);
		Edge e4 = new Edge(v1, v4);
		Edge e5 = new Edge(v4, v3);

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

		assertTrue(g.getAllEdgesInDirectedCycles().size() == 5);

	}

}
