package com.plectix.simulator.probability.avl;

public interface AncestorVisitStrategy {
	/**
	 * This method do smth with the given vertex and returns next vertex to be handled
	 * @param vertex current vertex
	 * @return next vertex to be handled
	 */
	public WeightedNode<?> visit(WeightedNode<?> vertex);
}
