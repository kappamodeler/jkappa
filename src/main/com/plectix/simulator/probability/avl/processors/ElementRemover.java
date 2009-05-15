package com.plectix.simulator.probability.avl.processors;

import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.probability.avl.WeightedNode;
import com.plectix.simulator.probability.avl.Orientation;

public class ElementRemover {
	private static ElementRemover instance = new ElementRemover();
	
	private ElementRemover() {
	}
	
	public static ElementRemover getInstance() {
		return instance;
	}
	
//	public void removeElement(E element) {
//		removeElementRecursively(element);
//	}
	
	private <E extends WeightedItem> WeightedNode<E> getExtremeNode(WeightedNode<E> root,
			Orientation o) {
		WeightedNode<E> nextNode = root;
		while (nextNode.getChild(o) != null) {
			nextNode = nextNode.getChild(o);
		}
		return nextNode;
	}
	
	private <E extends WeightedItem> WeightedNode<E> findCloserOne(WeightedNode<E> vertex) {
		if (vertex.getBalance() == -1) {
			return getExtremeNode(vertex.getLeftChild(), 
					Orientation.RIGHT);
		} else {
			return getExtremeNode(vertex.getRightChild(), 
					Orientation.LEFT);
		}
	}
	
	/**
	 * This method removes vertex, containing given element 
	 * @param <E>
	 * @param vertex
	 * @return
	 */
	public <E extends WeightedItem> WeightedNode<E> removeElement(WeightedNode<E> vertex) {
		if (vertex.isLeave()) {
			Orientation whichChild = vertex.whichChild();
			if (whichChild != Orientation.UNKNOWN) {
				// else see in BalancedTree.remove
				WeightedNode<E> parent = vertex.getParent();
				parent.forgetChild(whichChild);
				return parent; 
			} else {
				return null;
			}
		} else {
			WeightedNode<E> closestVertex = findCloserOne(vertex);
			vertex.swapElements(closestVertex);
			return removeElement(closestVertex);
		}
	}
}
