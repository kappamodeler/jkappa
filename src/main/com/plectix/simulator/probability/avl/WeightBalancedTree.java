package com.plectix.simulator.probability.avl;

import com.plectix.simulator.probability.WeightedItem;

public class WeightBalancedTree<E extends WeightedItem> {
	private WeightedNode<E> myRoot = null;
	
	public WeightBalancedTree() {
		
	}
	
	public WeightBalancedTree(WeightedNode<E> root) {
	}

	public void addElement(E element) {
		WeightedNode<E> parent = addElement(element, myRoot);
		if (myRoot.getBalance() > 1) {
			restoreBalance(parent);
		}
	}
	
	private WeightedNode<E> addElement(E element, WeightedNode<E> parent) {
		if (myRoot == null) {
			myRoot = new WeightedNode<E>(element, parent);
			return myRoot;
		} else {
			if (element.getWeight() >= myRoot.getWeight()) {
				return myRoot.getRightSubTree().addElement(element, myRoot);
			} else {
				return myRoot.getLeftSubTree().addElement(element, myRoot);
			}
		}
	}

	private WeightedNode<E> getLeftVertex() {
		WeightedNode<E> vertex = myRoot;
		while (vertex.getLeftSubTree() != null) {
			vertex = vertex.getLeftSubTree().getRoot();
		}
		return vertex;
	}
	
	public void removeElement(E element) {
		removeElementRecursively(element);
	}
	
	private void removeElementRecursively(E element) {
		if (!isEmpty()) {
			if (element.getWeight() > myRoot.getWeight()) {
				myRoot.getRightSubTree().removeElement(element);
			} else if (element.getWeight() < myRoot.getWeight()) {
				myRoot.getLeftSubTree().removeElement(element);
			} else {
				// we have element.weight == myRoot.weight
				if (myRoot.getElement() != element) {
					myRoot.getRightSubTree().removeElement(element);
				} else {
					boolean noChildren = myRoot.hasNoLeftChild() && myRoot.hasNoRightChild();
					boolean hasOneChild = myRoot.hasNoLeftChild() || myRoot.hasNoRightChild();
					if (noChildren) {
						myRoot.getParent().forgetChild(myRoot);
					} else if (hasOneChild) {
						myRoot.erase();
					} else {
						WeightedNode<E> leftVertexOfRightSubTree = myRoot.getRightSubTree().getLeftVertex();
						WeightedNode<E> parentOfLeftVertex = leftVertexOfRightSubTree.getParent();
						myRoot.setElement(leftVertexOfRightSubTree.getElement());
						if (leftVertexOfRightSubTree.isLeftChild()) {
							parentOfLeftVertex.setLeftSubTree(leftVertexOfRightSubTree.getRightSubTree());
						} else {
							parentOfLeftVertex.setRightSubTree(leftVertexOfRightSubTree.getRightSubTree());
						}
					}
				}
			}
		}
	}
	
	private void restoreBalance(WeightedNode<E> vertex) {
		
	}
	
	private void restoreBalanceOnce(WeightedNode<E> vertex) {
		
	}
	
	public WeightedNode<E> getRoot() {
		return myRoot;
	}
	
	public boolean isEmpty() {
		return myRoot == null;
	}
}
