package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.avl.Orientation;
import com.plectix.simulator.probability.avl.WeightedNode;

public class DeletedVertexAncestorVisitor implements AncestorVisitStrategy  {

	public WeightedNode<?> visit(WeightedNode<?> vertex) {
		WeightedNode<?> parent = vertex.getParent();
		if (parent == null) {
			return null;
		}
		if (vertex.isLeftChild()) {
			parent.incBalance();
		} else {
			parent.decBalance();
		}
		int vertexBalance = vertex.getBalance();
		if (vertexBalance == -1 || vertexBalance == 1) {
			// stop here, because balance of elder ancestors doesn't change
			return null;
		} else if (vertexBalance == -2) {
			WeightedNode<?> leftChild = vertex.getLeftChild();
			if (leftChild.getBalance() == 1) {
				DoubleRotatePerformer.getInstance().perform(vertex, 
						Orientation.LEFT);
			} else {
				SingleRotatePerformer.getInstance().perform(vertex, 
						Orientation.RIGHT);
			}
		} else if (vertexBalance == 2) {
			WeightedNode<?> rightChild = vertex.getRightChild();
			if (rightChild.getBalance() == -1) {
				DoubleRotatePerformer.getInstance().perform(vertex, 
						Orientation.RIGHT);
			} else {
				SingleRotatePerformer.getInstance().perform(vertex, 
						Orientation.LEFT);
			}
		} else {
			return parent;
		}
		return null;
	}
	
	/**
	 * This method describes what we should do when visiting the first vertex.
	 * The first vertex to be visited after deleting vertex V (V is always leave!) is
	 * it's parent. We should  
	 * @param vertex
	 * @return
	 */
	public WeightedNode<?> visitFirstVertex(WeightedNode<?> vertex) {
		if (vertex.isLeftChild()) {
			vertex.getParent().incBalance();
		} else {
			vertex.getParent().decBalance();
		}
		//TODO almost auto generated =) 
		return null;
	}

}
