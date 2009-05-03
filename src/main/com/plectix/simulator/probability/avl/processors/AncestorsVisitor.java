package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.avl.WeightedNode;

public abstract class AncestorsVisitor {
	protected void visit(WeightedNode<?> currentVertex, AncestorVisitStrategy strategy) {
		WeightedNode<?> nextVertex = currentVertex;
		while (nextVertex != null) {
			nextVertex = strategy.visit(nextVertex);
		}
	}
}
