package com.plectix.simulator.staticanalysis.cycledetection;

import com.plectix.simulator.staticanalysis.graphs.Edge;

final class EdgeFromContactMap extends Edge {
	public EdgeFromContactMap(NodeFromContactMap firstVertex, NodeFromContactMap lastVertex) {
		super(firstVertex, lastVertex);
	}
}
