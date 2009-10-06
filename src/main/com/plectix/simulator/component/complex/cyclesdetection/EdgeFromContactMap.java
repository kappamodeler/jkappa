package com.plectix.simulator.component.complex.cyclesdetection;

import com.plectix.simulator.graphs.Edge;

public final class EdgeFromContactMap extends Edge {
	public EdgeFromContactMap(NodeFromContactMap firstVertex, NodeFromContactMap lastVertex) {
		super(firstVertex, lastVertex);
	}
}
