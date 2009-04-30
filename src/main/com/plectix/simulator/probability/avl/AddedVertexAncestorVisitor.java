package com.plectix.simulator.probability.avl;

/*package*/ class AddedVertexAncestorVisitor implements AncestorVisitStrategy {

	public WeightedNode<?> visit(WeightedNode<?> vertex) {
		WeightedNode<?> parent = vertex.getParent();
		if (vertex.isLeftChild()) {
			parent.incBalance();
		} else {
			parent.decBalance();
		}
		if (parent.getBalance() == -2) {
			// see wiki =)
		} else if (parent.getBalance() == 2) {
			
		}
		return parent;
	}

}
