package com.plectix.simulator.graphs;

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
	

	//@Test
	public void testDetectCycles() {
	
		if (tested.getAllEdgesInCycles().size() > 0)
			fail("bad cycles :(");

	}

}
