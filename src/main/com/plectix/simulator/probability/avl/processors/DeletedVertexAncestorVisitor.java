package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.avl.Orientation;
import com.plectix.simulator.probability.avl.WeightedNode;

public class DeletedVertexAncestorVisitor implements AncestorVisitStrategy  {

	public WeightedNode<?> visit(WeightedNode<?> vertex) {
		WeightedNode<?> parent = vertex;
		if (parent == null) {
			return null;
		}
		if (vertex.isLeftChild()) {
			parent.incBalance();
		} else {
			parent.decBalance();
		}
		int parentBalance = parent.getBalance();
		if (parentBalance == -1 || parentBalance == 1) {
			// stop here, because balance of elder ancestors doesn't change
			return null;
		} else if (parentBalance == -2) {
			WeightedNode<?> leftChild = parent.getLeftChild();
			if (leftChild.getBalance() == 1) {
				DoubleRotatePerformer.getInstance().perform(parent, 
						Orientation.LEFT);
			} else {
				SingleRotatePerformer.getInstance().perform(parent, 
						Orientation.RIGHT);
			}
		} else if (parentBalance == 2) {
			WeightedNode<?> rightChild = parent.getRightChild();
			if (rightChild.getBalance() == -1) {
				DoubleRotatePerformer.getInstance().perform(parent, 
						Orientation.RIGHT);
			} else {
				SingleRotatePerformer.getInstance().perform(parent, 
						Orientation.LEFT);
			}
		} else {
			return parent;
		}
		return null;
	}

}
