package com.plectix.simulator.probability.avl;

public abstract class AncestorsVisitor {
	protected void visit(WeightedNode<?> currentVertex, AncestorVisitStrategy strategy) {
		WeightedNode<?> nextVertex = currentVertex;
		while (nextVertex != null) {
			nextVertex = strategy.visit(nextVertex);
		}
	}
}
