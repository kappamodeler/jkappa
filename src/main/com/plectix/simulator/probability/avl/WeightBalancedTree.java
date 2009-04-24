package com.plectix.simulator.probability.avl;

import com.plectix.simulator.probability.Ponderable;

public class WeightBalancedTree<E extends Ponderable> {
	private WeightedNode<E> myRoot = null;
	private int myHeight = 0; 
	private boolean heightIsUpToDate = true;
	
	public WeightBalancedTree() {
		
	}
	
	public WeightBalancedTree(WeightedNode<E> root) {
		heightIsUpToDate = false;
	}

	public void addElement(E element) {
		addElement(element, myRoot);
		heightIsUpToDate = false;
		if (myRoot.getBalance() > 1) {
			restoreBalance();
		}
	}
	
	private void addElement(E element, WeightedNode<E> parent) {
		if (myRoot == null) {
			myRoot = new WeightedNode<E>(element, parent);
			myHeight = 1;
			heightIsUpToDate = true;
			return;
		} else {
			if (element.getWeight() >= myRoot.getWeight()) {
				myRoot.getRightSubTree().addElement(element, myRoot);
				myRoot.getRightSubTree().heightIsUpToDate = false;
			} else {
				myRoot.getLeftSubTree().addElement(element, myRoot);
				myRoot.getLeftSubTree().heightIsUpToDate = false;
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
		if (myRoot != null) {
			if (myRoot.getBalance() > 1) {
				restoreBalance();
			}
		}
	}
	
	private void removeElementRecursively(E element) {
		if (!isEmpty()) {
			heightIsUpToDate = false;
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
	
	private void restoreBalance() {
		
	}
	
	public WeightedNode<E> getRoot() {
		return myRoot;
	}
	
	public boolean isEmpty() {
		return myRoot == null;
	}
	
	public int getHeight() {
		if (heightIsUpToDate) {
			return myHeight;
		} else {
			if (myRoot == null) {
				myHeight = 0;
			} else {
				myHeight = Math.max(myRoot.getLeftSubTree().getHeight(), 
						myRoot.getRightSubTree().getHeight());
			}
			heightIsUpToDate = true;
		}
		return myHeight;
	}
}
